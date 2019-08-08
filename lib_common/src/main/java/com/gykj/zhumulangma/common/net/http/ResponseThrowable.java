package com.gykj.zhumulangma.common.net.http;

/**
 * Description: <ResponseThrowable><br>
 * Author:      mxdl<br>
 * Date:        2019/3/18<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class ResponseThrowable extends Exception {
    public String code;
    public String message;

    public ResponseThrowable(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseThrowable(Throwable throwable, String code) {
        super(throwable);
        this.code = code;
    }

    @Override
    public String toString() {
        return "ResponseThrowable{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
