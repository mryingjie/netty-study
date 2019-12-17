package com.demo.netty.demo.rpc.bean;

/**
 * created by Yingjie Zheng at 2019-12-16 13:44
 */
public class ResponseParam {

    String response;

    String responseType;

    public ResponseParam(String response, String responseType) {
        this.response = response;
        this.responseType = responseType;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
}
