package com.demo.netty.demo.rpc.api;

import java.util.List;

/**
 * created by Yingjie Zheng at 2019-12-13 14:26
 *
 * 消费端和服务端共用的接口
 */
public interface TestRPCInterface {

    public List<Student> getByNameAndAge(String name, Integer age);

}
