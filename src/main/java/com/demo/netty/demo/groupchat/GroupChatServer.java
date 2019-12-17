package com.demo.netty.demo.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * created by Yingjie Zheng at 2019-12-10 16:24
 *
 * 接受多个客户端的消息并转发
 */
@SuppressWarnings("all")
@Slf4j
public class GroupChatServer{

    private int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    public void run() {
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
                            //添加编码解码器
                            ch.pipeline().addLast("decoder",new StringDecoder());
                            ch.pipeline().addLast("encoder",new StringEncoder());


                            ch.pipeline().addLast(new GroupChatServerHandler());//为workerGroup的EventLoop对应的管道设置处理器
                        }
                    });
            ChannelFuture ch = bootstrap.bind(port).sync();

            log.info("server started at port {}",port);

            // ch.addListener(new GenericFutureListener<Future<? super Void>>() {
            //     @Override
            //     public void operationComplete(Future<? super Void> future) throws Exception {
            //         Object object = future.get();
            //     }
            // })
            ch.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("netty server start error！",e);
        } finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        new GroupChatServer(8888).run();
    }
}
