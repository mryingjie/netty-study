package com.demo.netty.demo.rpc.provider;

import com.demo.netty.demo.rpc.annotation.Service;
import com.demo.netty.demo.rpc.api.Student;
import com.demo.netty.demo.rpc.api.TestRPCInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Yingjie Zheng at 2019-12-16 09:25
 */
@Service
public class TestService implements TestRPCInterface {

    @Override
    public List<Student> getByNameAndAge(String name, Integer age) {
        List<Student> results = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            results.add(new Student(name+i,i+10));
        }
        return results;
    }
}
