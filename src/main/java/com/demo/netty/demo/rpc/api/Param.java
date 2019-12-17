package com.demo.netty.demo.rpc.api;

/**
 * created by Yingjie Zheng at 2019-12-16 13:24
 */
public class Param {


    String name;

    int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Param(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
