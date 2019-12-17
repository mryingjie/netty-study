package com.demo.netty.demo.protobuf2;

import com.demo.netty.demo.protobuf.StudentPOJO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * created by Yingjie Zheng at 2019-12-05 11:16
 */
@Slf4j
@SuppressWarnings("all")
public class NettyProtoBufServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//一个线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();//cpu核数*2

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//设置服务器的管道实现  不同的协议类型会对应不同的channel实现
                    .option(ChannelOption.SO_BACKLOG, 1024)//设置线程队列的连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动链接状态
                    // .handler(null)                                       // 将在boosGroup生效的handler
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 将在workerGroup生效的handler
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //在服务器端加入protobuf decoder  并指定对那种对象进行解码
                            ch.pipeline().addLast("decoder",new ProtobufDecoder(MessagePOJO.Message.getDefaultInstance()));
                            ch.pipeline().addLast(new NettyProtoBufServerHandler());//为workerGroup的EventLoop对应的管道设置处理器
                        }
                    });
            ChannelFuture ch = bootstrap.bind(8888).sync();

            log.info("server started at port 8888");

            // ch.addListener(new GenericFutureListener<Future<? super Void>>() {
            //     @Override
            //     public void operationComplete(Future<? super Void> future) throws Exception {
            //         Object object = future.get();
            //     }
            // })
            ch.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
