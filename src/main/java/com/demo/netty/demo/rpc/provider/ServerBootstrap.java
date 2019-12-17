package com.demo.netty.demo.rpc.provider;

import com.demo.netty.demo.rpc.protocol.protobuf.ProtobufProtocol;
import com.demo.netty.demo.rpc.remoting.NettyRPCServer;

/**
 * created by Yingjie Zheng at 2019-12-16 14:59
 */
public class ServerBootstrap {

    public static void main(String[] args) {
        new NettyRPCServer(8888, new ProtobufProtocol(), "com.demo.netty.demo.rpc.provider").run();
    }
}
