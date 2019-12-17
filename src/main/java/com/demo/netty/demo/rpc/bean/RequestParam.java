package com.demo.netty.demo.rpc.bean;


/**
 * created by Yingjie Zheng at 2019-12-16 11:18
 */
public class RequestParam {


    String[] paramType;

    String[] args;


    public String[] getParamType() {
        return paramType;
    }

    public void setParamType(String[] paramType) {
        this.paramType = paramType;
    }

    public RequestParam(String[] paramType,String[] args) {
        this.paramType = paramType;
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
