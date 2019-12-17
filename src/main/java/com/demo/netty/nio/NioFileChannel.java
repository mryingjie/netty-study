package com.demo.netty.nio;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * created by Yingjie Zheng at 2019-11-26 16:10
 */
public class NioFileChannel {

    public static void main(String[] args) throws IOException {
        //一  向文件写入数据
        String str = "NIO写入文件测试\n";

        //获取channel
        File file = new File("/Users/lsjr3/IdeaProjects/extend/netty/src/main/resources/NioText.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel outChannel = fileOutputStream.getChannel();

        //准备缓冲区
        byte[] bytes = str.getBytes();
        ByteBuffer outBuffer = ByteBuffer.allocate(bytes.length);
        //放入缓冲区
        outBuffer.put(bytes);

        outBuffer.flip();
        //将buffer的数据写入到channel
        outChannel.write(outBuffer);
        outChannel.close();
        fileOutputStream.close();


        //二  读取文件数据
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel inChannel = fileInputStream.getChannel();
        //因为汉字是3个字节 这里如果不是3的整数倍就会导致一个汉字的完整字节被拆分 所以大文件读取建议不使用nio  而是使用Reader一行一行读取
        ByteBuffer inBuffer = ByteBuffer.allocate(3);
        int read;
        while ((read = inChannel.read(inBuffer)) != -1) {
            inBuffer.clear();
            byte[] array = inBuffer.array();
            String s = new String(array, 0, read);
            System.out.println(s);
        }
        fileInputStream.close();
        inChannel.close();


        // 三 文件拷贝
        // outChannel.transferFrom(inChannel, 0, inChannel.size());
    }


}
