package com.gykj.zhumulangma.common.net.http;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:自定义异常类
 */
public class CustException extends Exception {
    public String code;
    public String message;

    public CustException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
