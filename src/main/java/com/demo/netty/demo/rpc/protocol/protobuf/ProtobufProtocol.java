package com.demo.netty.demo.rpc.protocol.protobuf;

import com.demo.netty.demo.rpc.protocol.Protocol;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * created by Yingjie Zheng at 2019-12-13 17:09
 */
public class ProtobufProtocol implements Protocol {


    @Override
    public ChannelOutboundHandlerAdapter getEncoder() {
        return new ProtobufEncoder();
    }

    @Override
    public ChannelInboundHandlerAdapter getDecoder() {
        return new ProtobufDecoder(MessagePOJO.Message.getDefaultInstance());
    }

    @Override
    public String decode() {
        return null;
    }

    @Override
    public String encode() {
        return null;
    }

    @Override
    public String getParam(Object msg) {
        return transToRequestMessage(msg).getParam();
    }

    private MessagePOJO.RequestMessage transToRequestMessage(Object msg){
        MessagePOJO.Message message = (MessagePOJO.Message) msg;
        return message.getRequest();
    }

    private MessagePOJO.ResponseMessage transToResponseMessage(Object msg){
        MessagePOJO.Message message = (MessagePOJO.Message) msg;
        return message.getResponse();
    }

    @Override
    public String getClassName(Object msg) {
        return transToRequestMessage(msg).getClassName();
    }



    @Override
    public String getResponse(Object msg) {
        return transToResponseMessage(msg).getResponse();
    }

    @Override
    public String getMethod(Object msg) {
        return transToRequestMessage(msg).getMethod();
    }
}
