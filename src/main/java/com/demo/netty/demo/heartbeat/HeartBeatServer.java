package com.demo.netty.demo.heartbeat;

import com.demo.netty.demo.simple.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * created by Yingjie Zheng at 2019-12-11 10:05
 */
@SuppressWarnings("all")
@Slf4j
public class HeartBeatServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//一个线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();//cpu核数*2

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//设置服务器的管道实现  不同的协议类型会对应不同的channel实现
                    .option(ChannelOption.SO_BACKLOG, 1024)//设置线程队列的连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动链接状态
                    .handler(new LoggingHandler(LogLevel.INFO))             // 将在boosGroup生效的handler  日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 将在workerGroup生效的handler
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //1. IdleStateHandler 是 netty 提供的处理空闲状态的处理器
                            // 2. long readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接
                            // 3. long writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
                            // 4. long allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接
                            // 5. 当 IdleStateEvent 触发后 , 就会传递给管道 的下一个 handler 去处理
                            // 通过调用(触发)下一个 handler 的 userEventTiggered , 在该方法中去处理 IdleStateEvent(读空闲，写空闲，读写空闲)
                            ch.pipeline().addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new HeartBeatServerHandler());//为workerGroup的EventLoop对应的管道设置处理器
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
