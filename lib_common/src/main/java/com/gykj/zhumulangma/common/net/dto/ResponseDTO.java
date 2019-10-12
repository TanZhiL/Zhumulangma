package com.gykj.zhumulangma.common.net.dto;

/**
 * Author: Thomas.
 * <br/>Date: 2019/7/30 17:43
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class ResponseDTO<T>{

    public T result;
    public String code;
    public String msg;

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "result=" + result +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
