package com.demo.netty.demo.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * created by Yingjie Zheng at 2019-12-06 17:45
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //添加一个处理http的编解码器
        pipeline.addLast(new HttpServerCodec());

        //添加自定义处理器
        pipeline.addLast(new HttpServerHandler());

    }
}
