package com.gykj.zhumulangma.common.net.dto;

/**
 * Author: Thomas.
 * Date: 2019/7/30 17:43
 * Email: 1071931588@qq.com
 * Description:
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
