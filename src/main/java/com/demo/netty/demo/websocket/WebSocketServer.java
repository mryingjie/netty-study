package com.demo.netty.demo.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * created by Yingjie Zheng at 2019-12-11 15:48
 */
@Slf4j
@SuppressWarnings("all")
public class WebSocketServer {

    private int port;

    public WebSocketServer(int port) {
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
                    .handler(new LoggingHandler(LogLevel.INFO))                    // 将在boosGroup生效的handler
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 将在workerGroup生效的handler
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //添加编码解码器 WebSocket基于http协议因此需要添加一个http解码器
                            pipeline.addLast(new HttpServerCodec());
                            //是以块方式写，添加 ChunkedWriteHandler 处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            /*
                                说明
                                1. http 数据在传输过程中是分段, HttpObjectAggregator ，就是可以将多个段聚合
                                2. 这就就是为什么，当浏览器发送大量数据时，就会发出多次 http 请求
                            */
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            /*
                                说明
                                1. 对应 websocket ，它的数据是以 帧(frame) 形式传递
                                2. 可以看到 WebSocketFrame 下面有六个子类
                                3. 浏览器请求时 ws://localhost:7000/hello 表示请求的 uri
                                4. WebSocketServerProtocolHandler 核心功能是将 http 协议升级为 ws 协议 , 保持长连接 5. 是通过一个 状态码 101
                            */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/index"));

                            pipeline.addLast(new WebSocketServerHandler());//为workerGroup的EventLoop对应的管道设置处理器
                        }
                    });
            ChannelFuture ch = bootstrap.bind(port).sync();

            log.info("server started at port {}", port);

            // ch.addListener(new GenericFutureListener<Future<? super Void>>() {
            //     @Override
            //     public void operationComplete(Future<? super Void> future) throws Exception {
            //         Object object = future.get();
            //     }
            // })
            ch.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty server start error！", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new WebSocketServer(8888).run();
    }

}


