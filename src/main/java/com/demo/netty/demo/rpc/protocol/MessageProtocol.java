package com.demo.netty.demo.rpc.protocol;

import java.nio.charset.StandardCharsets;

/**
 * created by Yingjie Zheng at 2019-12-12 17:35
 */
public class MessageProtocol {

    private int len;

    private byte[] content;


    public MessageProtocol(int len, byte[] content) {
        this.len = len;
        this.content = content;
    }

    public MessageProtocol() {

    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageProtocol{" +
                "len=" + len +
                ", content=" + new String(content, StandardCharsets.UTF_8) +
                '}';
    }
}
