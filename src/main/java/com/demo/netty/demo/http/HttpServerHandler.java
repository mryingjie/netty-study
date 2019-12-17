package com.demo.netty.demo.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * created by Yingjie Zheng at 2019-12-06 17:49
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if(msg instanceof HttpRequest){
            URI uri = new URI(((HttpRequest) msg).uri());
            log.info("访问路径:{}",uri.getPath());
            log.info("访问ip:{}",ctx.channel().remoteAddress());

            //回复
            ByteBuf content = Unpooled.copiedBuffer("hello 大点", CharsetUtil.UTF_8);

            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            response.headers().set(HttpHeaderNames.ACCEPT_ENCODING, "utf-8");


            ctx.writeAndFlush(response);
        }

    }
}
