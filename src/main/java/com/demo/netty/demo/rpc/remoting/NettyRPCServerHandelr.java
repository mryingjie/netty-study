package com.demo.netty.demo.rpc.remoting;

import com.alibaba.fastjson.JSON;
import com.demo.netty.demo.rpc.annotation.Service;
import com.demo.netty.demo.rpc.bean.RequestParam;
import com.demo.netty.demo.rpc.bean.ResponseParam;
import com.demo.netty.demo.rpc.protocol.Protocol;
import com.demo.netty.demo.rpc.protocol.protobuf.MessagePOJO;
import com.demo.netty.demo.rpc.protocol.protobuf.ProtobufProtocol;
import com.demo.netty.demo.rpc.util.ClassScanner;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * created by Yingjie Zheng at 2019-12-13 17:16
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyRPCServerHandelr extends ChannelInboundHandlerAdapter {

    /**
     * 初始化的服务列表  <接口全类名,实现类对象>
     */
    private static Map<String, Object> serviceMap;


    private Protocol protocol;



    public NettyRPCServerHandelr(Protocol protocol, String basePackage) {
        this.protocol = protocol;
        Set<Class<?>> serviceClass = ClassScanner.getClassesByAnnotation(basePackage, Service.class);
        if (serviceClass.size() > 0) {
            serviceMap = new HashMap<>(serviceClass.size());
        }
        for (Class<?> aClass : serviceClass) {
            Class<?>[] interfaces = aClass.getInterfaces();
            try {
                serviceMap.put(interfaces[0].getName(), aClass.newInstance());
                log.info("初始化服务:[{}]",interfaces[0].getName());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("初始化服务列表失败", e);
            }
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("receive msg : {}",msg);
        String className = protocol.getClassName(msg);
        String param = protocol.getParam(msg);
        String methodName = protocol.getMethod(msg);
        Object service = serviceMap.get(className);
        RequestParam requestParam = JSON.parseObject(param, RequestParam.class);
        Class<?> aClass = service.getClass();
        Class[] paramTypes = Arrays.stream(requestParam.getParamType()).map((Function<String, Class>) s -> {
            try {
                return Class.forName(s);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("param type not found", e);
            }
        }).toArray(Class[]::new);
        Method method = aClass.getDeclaredMethod(methodName, paramTypes);
        String[] argsStr = requestParam.getArgs();
        Object[] args = new Object[argsStr.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Class paramClass = paramTypes[i];
            args[i] = JSON.parseObject(argsStr[i], paramClass);
        }
        Object response = method.invoke(service, args);
        MessagePOJO.Message responseMessage = MessagePOJO.Message.newBuilder()
                .setDataType(MessagePOJO.Message.DataType.ResponseMessage)
                .setResponse(
                        MessagePOJO.ResponseMessage.newBuilder()
                                .setResponse(JSON.toJSONString(new ResponseParam(JSON.toJSONString(response),method.getReturnType().getName()) ))
                )
                .build();
        ctx.writeAndFlush(responseMessage);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务执行异常，关闭通道",cause);
        ctx.close();
    }

    public static void main(String[] args) {
        NettyRPCServerHandelr nettyServerHandelr = new NettyRPCServerHandelr(new ProtobufProtocol(), "com.demo.netty.demo.rpc.provider");

    }


}
