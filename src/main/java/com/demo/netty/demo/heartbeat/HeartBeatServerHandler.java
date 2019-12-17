package com.demo.netty.demo.heartbeat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * created by Yingjie Zheng at 2019-12-11 11:12
 */
@Slf4j
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(ctx.channel().remoteAddress()+"通道异常，关闭通道！！！",cause);
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            Channel channel = ctx.channel();
            String remoteAddress = channel.remoteAddress().toString();
            switch (state){
                case ALL_IDLE:
                    log.info(remoteAddress+" 读空闲。。。");
                    break;
                case READER_IDLE:
                    log.info(remoteAddress+" 写空闲。。。");
                    break;
                case WRITER_IDLE:
                    log.info(remoteAddress+" 读写空闲。。。");
            }
            // ctx.close(); //此处可以根据业务关闭通道

        }
    }
}
