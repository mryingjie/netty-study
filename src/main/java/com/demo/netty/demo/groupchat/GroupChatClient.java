package com.demo.netty.demo.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * created by Yingjie Zheng at 2019-12-10 17:20
 */
@SuppressWarnings("all")
@Slf4j
public class GroupChatClient implements Runnable{

    private String host;

    private int port;

    public GroupChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run(){
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)//客户端要使用NioSocketChannel类
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder",new StringDecoder());
                            ch.pipeline().addLast("encoder",new StringEncoder());
                            ch.pipeline().addLast(new GroupChatCLientHandler()); //设置消息处理器
                        }
                    });
            //连接服务器
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            log.info("client connect [{}:{}] succeed!!",host,port);
            //监听关闭通道的事件
            Channel channel = sync.channel();

            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()){
                String msg = sc.nextLine();
                channel.writeAndFlush(msg+"\r\n");
            }

        }catch (Exception e){
            log.error("客户端启动异常",e);
        }finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Thread(new GroupChatClient("127.0.0.1",8888)).start();
    }

}
