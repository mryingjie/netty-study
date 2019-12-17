package com.demo.netty.demo.rpc.remoting;

import com.demo.netty.demo.rpc.protocol.Protocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * created by Yingjie Zheng at 2019-12-13 16:44
 */
@Slf4j
@SuppressWarnings("all")
public class NettyRPCServer {

    private int port;

    private Protocol protocol;

    private String basePackage;


    public NettyRPCServer(int port, Protocol protocol, String serBasePkg) {
        this.port = port;
        this.protocol = protocol;
        this.basePackage = serBasePkg;
    }

    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//一个线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();//cpu核数*2

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            NettyRPCServerHandelr nettyRPCServerHandelr = new NettyRPCServerHandelr(protocol, basePackage);
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//设置服务器的管道实现  不同的协议类型会对应不同的channel实现
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动链接状态
                    .handler(new LoggingHandler(LogLevel.DEBUG))             // 将在boosGroup生效的handler  日志处理器

                    // .handler(null)                                       // 将在boosGroup生效的handler
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 将在workerGroup生效的handler
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(protocol.getDecoder());
                            pipeline.addLast(protocol.getEncoder());
                            pipeline.addLast(nettyRPCServerHandelr);

                        }
                    });
            ChannelFuture ch = bootstrap.bind(port).sync();

            log.info("netty rpc server started at port {}",port);
            ch.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) {

    }

}
