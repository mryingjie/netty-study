package com.demo.netty.demo.rpc.api;

/**
 * created by Yingjie Zheng at 2019-12-16 13:25
 */
public class Student {

    String name;

    int age;

    public Student() {
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

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
}
