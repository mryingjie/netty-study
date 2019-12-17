package com.demo.netty.demo.rpc.remoting;

import com.alibaba.fastjson.JSON;
import com.demo.netty.demo.rpc.bean.RequestParam;
import com.demo.netty.demo.rpc.bean.ResponseParam;
import com.demo.netty.demo.rpc.protocol.Protocol;
import com.demo.netty.demo.rpc.protocol.protobuf.MessagePOJO;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * created by Yingjie Zheng at 2019-12-16 13:34
 */
@Slf4j
public class NettyRPCClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext ctx;

    private Protocol protocol;

    private Object result;

    private RequestParam requestParam;

    private String className;

    private String methodName;

    public NettyRPCClientHandler(Protocol protocol) {
        this.protocol = protocol;
    }

    public synchronized Object getResult() {
        return result;
    }

    public synchronized void setResult(Object result) {
        this.result = result;
    }

    public synchronized void setRequest(String className,String methodName,RequestParam requestParam){
        this.className = className;
        this.methodName = methodName;
        this.requestParam = requestParam;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String responseStr = protocol.getResponse(msg);
        log.info("接收服务端返回结果:{}",JSON.toJSONString(responseStr));
        ResponseParam responseParam = JSON.parseObject(responseStr, ResponseParam.class);
        String response = responseParam.getResponse();
        String responseType = responseParam.getResponseType();
        Class<?> responseClass = Class.forName(responseType);
        result  = JSON.parseObject(response, responseClass);
        notifyAll();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("远程服务调用异常,关闭通道，地址：{}", ctx.channel().remoteAddress());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接到服务器:{}",ctx.channel().remoteAddress());
        this.ctx = ctx;
    }

    @Override
    public synchronized Object call() throws Exception {
        log.info("execute task");
        MessagePOJO.Message message = MessagePOJO.Message.newBuilder()
                .setDataType(MessagePOJO.Message.DataType.RequestMessage)
                .setRequest(
                        MessagePOJO.RequestMessage.newBuilder()
                                .setClassName(className)
                                .setParam(JSON.toJSONString(requestParam))
                                .setMethod(methodName)
                                .build()
                )
                .build();
        log.info("message:[{}]", message);
        ctx.channel().writeAndFlush(message);
        log.info("远程服务地址:{}", ctx.channel().remoteAddress());
        wait();
        log.info("param:{}\nresult:{}","className:"+className+",methodName:"+methodName+",requestParam:"+JSON.toJSONString(requestParam),JSON.toJSONString(result));
        return result;
    }
}
