package com.demo.netty.demo.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * created by Yingjie Zheng at 2019-12-05 14:52
 *
 * 客户端启动时通过protobuf序列化发送一个student对象到服务器
 */
@Slf4j
public class NettyProtoBufClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道就绪时就触发此方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        StudentPOJO.Student student = StudentPOJO.Student
                .newBuilder()
                .setId(1)
                .setName("张三")
                .build();
        ctx.writeAndFlush(student);
    }


    /**
     * 接收数据的方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf= (ByteBuf) msg;
        log.info("receive server message:{}",buf.toString(CharsetUtil.UTF_8));
        log.info("server address:{}",ctx.channel().remoteAddress());
    }

    /**
     * 异常处理的方法
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
