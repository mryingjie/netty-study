package com.demo.netty.demo.groupchat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * created by Yingjie Zheng at 2019-12-10 16:34
 * 每次连接都会新创建一个channel和handler
 *
 */
@Slf4j
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {


    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final String dtfPattern = "yyyy-MM-dd HH:mm:ss";

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dtfPattern);

    /**
     * handlerAdded 表示连接建立，一旦连接建立就被执行
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded客户端[{}]在[{}]时上线。。。", ctx.channel().remoteAddress(), LocalDateTime.now().format(dtf));
        channelGroup.writeAndFlush("用户:[" + ctx.channel().remoteAddress() + "]加入了聊天室。。。\r\n");
        channelGroup.add(ctx.channel());
    }

    // /**
    //  * 表示 channel 处于活动状态, 提示 xx 上线  后于handlerAdded方法执行
    //  */
    // @Override
    // public void channelActive(ChannelHandlerContext ctx) throws Exception {
    //     log.info("channelActive客户端[{}]上线。。。", ctx.channel().remoteAddress());
    // }
    //
    // /**
    //  * 表示 channel 处于不活动状态, 提示 xx 离线了  先于handlerRemoved执行
    //  */
    // @Override
    // public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    //     log.info("channelInactive客户端:[{}]离线。。。", ctx.channel().remoteAddress());
    // }


    /**
     * 断开连接, 将 xx 客户离开信息推送给当前在线的客户  最后执行  调用这个方法时channelGroup已经移除了当前的channel因此不用再次移除了
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // channelGroup.remove(ctx.channel()); //已经移除了不必再次移除
        log.info("handlerRemoved客户端:[{}]离线。。。", ctx.channel().remoteAddress());
        ctx.channel().writeAndFlush("用户:[" + ctx.channel().remoteAddress() + "]下线。。。。\r\n");
    }

    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 接收到消息时触发
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        channelGroup.forEach(channel -> {
            if (channel.equals(ctx.channel())) {
                channel.writeAndFlush("你说:"+msg+"\r\n");
            } else {
                channel.writeAndFlush("用户:["+ctx.channel().remoteAddress()+"]说:"+msg+"\r\n");
            }
        });
    }


}
