package com.gykj.zhumulangma.common.net.http;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:自定义网络异常类
 */
public class RespException extends Exception {
    public String code;
    public String message;

    public RespException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public RespException(Throwable throwable, String code) {
        super(throwable);
        this.code = code;
    }

    @Override
    public String toString() {
        return "RespException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
