package com.demo.netty.nio.buffer;

import java.nio.IntBuffer;

/**
 * created by Yingjie Zheng at 2019-11-26 14:46
 */
public class BufferTest {

    public static void main(String[] args) {

        //创建一个能存放5个int的Buffer  底层是个数组 int[] hb
        IntBuffer buffer = IntBuffer.allocate(5);
        // private int mark = -1;
        // private int position = 0; //下一个可读或可写的数组索引
        // private int limit; //写入或读取的数据最大索引(终点)  可以修改
        // private int capacity; //容量 初始化后就确定不能修改

        //buffer的容量allocate.capacity()
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(i * 2);//存入数据
        }

        //写入数据
        //首先切换读写模式
        // limit = position;  最大索引设置为当前索引
        // position = 0;  当前位置设置为0
        // mark = -1;
        buffer.flip();

        //判断是否有剩余  类似于迭代器
        while (buffer.hasRemaining()){
            System.out.println(buffer.get());
        }

    }

}
