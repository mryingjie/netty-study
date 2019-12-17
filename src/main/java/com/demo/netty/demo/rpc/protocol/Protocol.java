package com.demo.netty.demo.rpc.protocol;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;

/**
 * created by Yingjie Zheng at 2019-12-13 17:01
 */
public interface Protocol {

    ChannelOutboundHandlerAdapter getEncoder();

    ChannelInboundHandlerAdapter getDecoder();

    String decode();

    String encode();

    String getParam(Object msg);

    String getClassName(Object msg);

    String getResponse(Object msg);

    String getMethod(Object msg);

}
