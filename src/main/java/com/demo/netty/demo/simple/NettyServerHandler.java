package com.demo.netty.demo.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * created by Yingjie Zheng at 2019-12-05 14:20
 * <p>
 * 服务器处理数据的handler
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 这个方法用于消息的接收处理
     *
     * @param ctx 上下文对象
     * @param msg 服务端接收到的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String str = buf.toString(CharsetUtil.UTF_8);
        //同步执行
        log.info("服务端接收到message:{}", str);
        log.info("message from address of :{}", ctx.channel().remoteAddress());


        //耗时任务可以放进该channel对应的taskQueue中异步执行 可以加入多个任务，这多个任务是同一个线程串行执行的
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(10 * 1000);
                log.info("耗时任务执行完毕");
                ctx.writeAndFlush(Unpooled.copiedBuffer("server message 2 ", CharsetUtil.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //提交定时任务到scheduleTaskQueue中  指定延迟多少秒后执行
        ctx.channel().eventLoop().schedule(
                () -> {
                    try {
                        Thread.sleep(10 * 1000);
                        log.info("定时任务执行完毕");
                        ctx.writeAndFlush(Unpooled.copiedBuffer("server message 3 ", CharsetUtil.UTF_8));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                5,
                TimeUnit.SECONDS
        );

    }


    /**
     * 这个方法用于消息接受处理后的处理  可用于回复消息
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello client!", CharsetUtil.UTF_8));
    }


    /**
     * 处理异常 一般需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
