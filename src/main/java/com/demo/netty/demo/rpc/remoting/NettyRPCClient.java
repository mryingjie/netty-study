package com.demo.netty.demo.rpc.remoting;

import com.alibaba.fastjson.JSON;
import com.demo.netty.demo.rpc.api.Student;
import com.demo.netty.demo.rpc.api.TestRPCInterface;
import com.demo.netty.demo.rpc.bean.RequestParam;
import com.demo.netty.demo.rpc.protocol.Protocol;
import com.demo.netty.demo.rpc.provider.TestService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;


/**
 * created by Yingjie Zheng at 2019-12-16 13:30
 */
@Slf4j
public class NettyRPCClient {

    private int port;

    private String host;

    private Protocol protocol;

    private NettyRPCClientHandler handler;



    private ExecutorService executor = new ThreadPoolExecutor(
            2, //核心线程数2
            Runtime.getRuntime().availableProcessors() * 2, //最大线程=cpu核数*2
            60, //非核心线程600秒内无任务被销毁
            //任务缓存队列最多缓存cpu核数*2个任务
            TimeUnit.SECONDS,
            new LinkedBlockingQueue(Runtime.getRuntime().availableProcessors() * 2),
            // 当线程池的任务缓存队列已满并且线程池中的线程数目达到最大值时由调用线程处理该任务
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public  Object getBean(final Class<?> serivceClass) {
        System.out.println("get bean");
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serivceClass},
                (proxy, method, args) -> {

                    Class<?>[] parameterTypes = method.getParameterTypes();

                    RequestParam requestParam = new RequestParam(
                            Arrays.stream(parameterTypes).map(Class::getName).toArray(String[]::new),
                            Arrays.stream(args).map(JSON::toJSONString).toArray(String[]::new)
                    );
                    handler.setRequest(serivceClass.getName(),method.getName(),requestParam);
                    return executor.submit(handler).get();
                });
    }

    public NettyRPCClient(int port, String host, Protocol protocol) {
        this.port = port;
        this.host = host;
        this.protocol = protocol;
    }


    public  Channel  run() {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        handler = new NettyRPCClientHandler(protocol);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)//客户端要使用NioSocketChannel类
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //添加一个protobuf编码器
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast("encoder", protocol.getEncoder());
                            pipeline.addLast("decoder", protocol.getDecoder());
                            pipeline.addLast(handler); //设置消息处理器
                        }
                    });
            //连接服务器
            ChannelFuture ch = bootstrap.connect(host, port).sync();
            log.info("client started success:{}",this);
            // TestRPCInterface service = (TestRPCInterface) this.getBean(TestRPCInterface.class);
            // List<Student> student = service.getByNameAndAge("aaa", 100);
            // System.out.println(student);
            return ch.channel();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
        return null;
    }


}
