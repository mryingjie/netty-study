package com.demo.netty.demo.protocoltcp;

import com.demo.netty.demo.rpc.protocol.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * created by Yingjie Zheng at 2019-12-05 14:52
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    /**
     * 当通道就绪时就触发此方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = "测试Tcp粘包拆包消息-";
        for (int i = 0; i < 10; i++) {
            byte[] bytes = (msg + i).getBytes(StandardCharsets.UTF_8);
            log.info("send message:{}",(msg + i) );
            ctx.writeAndFlush(new MessageProtocol(bytes.length, bytes));
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

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
