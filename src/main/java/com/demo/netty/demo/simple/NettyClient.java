package com.demo.netty.demo.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * created by Yingjie Zheng at 2019-12-05 14:44
 */
@Slf4j
public class NettyClient {

    public static void main(String[] args) {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)//客户端要使用NioSocketChannel类
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler()); //设置消息处理器
                        }
                    });
            //连接服务器
            ChannelFuture sync = bootstrap.connect("127.0.0.1", 8888).sync();
            log.info("client started success");
            //监听关闭通道的事件
            sync.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            eventExecutors.shutdownGracefully();
        }


    }
}
