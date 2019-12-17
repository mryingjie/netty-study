package com.demo.netty.demo.rpc.consumer;

import com.demo.netty.demo.rpc.annotation.Reference;
import com.demo.netty.demo.rpc.api.TestRPCInterface;

/**
 * created by Yingjie Zheng at 2019-12-16 14:16
 */

public class TestConsumerService {

    @Reference
    private TestRPCInterface testRPCInterface;





}
