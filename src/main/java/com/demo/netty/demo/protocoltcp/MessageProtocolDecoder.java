package com.demo.netty.demo.protocoltcp;

import com.demo.netty.demo.rpc.protocol.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * created by Yingjie Zheng at 2019-12-13 10:34
 */
public class MessageProtocolDecoder extends ReplayingDecoder<Void> {
    /**
     * 将数据解析为MessageProtocol  并传递到下一个handler
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readInt();
        byte[] content = new byte[len];
        in.readBytes(content);
        out.add(new MessageProtocol(len,content));
    }
}
