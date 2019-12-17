package com.demo.netty.demo.protocoltcp;

import com.demo.netty.demo.rpc.protocol.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * created by Yingjie Zheng at 2019-12-05 14:20
 * <p>
 * 服务器处理数据的handler
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        log.info("receive message:{}",msg.toString());
    }


    /**
     * 这个方法用于消息接受处理后的处理  可用于回复消息
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }


    /**
     * 处理异常 一般需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
