package com.gykj.zhumulangma.common.net.exception;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:可以被拦截的异常类
 */
public class InterceptableException extends Exception {
    public String code;
    public String message;

    public InterceptableException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public InterceptableException(Throwable throwable, String code) {
        super(throwable);
        this.code = code;
    }

    @Override
    public String toString() {
        return "CustException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

   public static final String TOKEN_OUTTIME = "0004";
}
