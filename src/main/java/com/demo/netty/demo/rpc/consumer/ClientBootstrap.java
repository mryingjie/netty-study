package com.demo.netty.demo.rpc.consumer;

import com.demo.netty.demo.rpc.api.Student;
import com.demo.netty.demo.rpc.api.TestRPCInterface;
import com.demo.netty.demo.rpc.protocol.protobuf.ProtobufProtocol;
import com.demo.netty.demo.rpc.remoting.NettyRPCClient;
import io.netty.channel.Channel;

import java.util.List;

/**
 * created by Yingjie Zheng at 2019-12-16 14:58
 */
public class ClientBootstrap {

    public static void main(String[] args) {

        NettyRPCClient client = new NettyRPCClient(8888, "localhost", new ProtobufProtocol());
        client.run();
        TestRPCInterface service = (TestRPCInterface) client.getBean(TestRPCInterface.class);
        // new Thread(client).start();
        List<Student> student = service.getByNameAndAge("张三", 3);
        System.out.println(student);


    }

}
