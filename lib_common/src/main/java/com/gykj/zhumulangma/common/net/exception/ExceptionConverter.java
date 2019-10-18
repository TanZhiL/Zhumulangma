package com.gykj.zhumulangma.common.net.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:网络异常转换
 */
public class ExceptionConverter {

   public static Exception convert(Throwable e) {
        String msg;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            switch (String.valueOf(httpException.code())) {
                case NET_ERROR.UNAUTHORIZED:
                    msg="操作未授权";
                    break;
                case NET_ERROR.FORBIDDEN:
                    msg= "请求被拒绝";
                    break;
                case NET_ERROR.NOT_FOUND:
                    msg = "资源不存在";
                    break;
                case NET_ERROR.REQUEST_TIMEOUT:
                    msg = "服务器执行超时";
                    break;
                case NET_ERROR.INTERNAL_SERVER_ERROR:
                    msg = "服务器内部错误";
                    break;
                case NET_ERROR.SERVICE_UNAVAILABLE:
                    msg = "服务器不可用";
                    break;
                default:
                    msg = "网络错误";
                    break;
            }
            msg+=":"+httpException.code();
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof 
                ParseException || e instanceof MalformedJsonException) {
            msg = "解析错误";
        } else if (e instanceof ConnectException) {
            msg = "连接失败";
        } else if (e instanceof javax.net.ssl.SSLException) {
            msg = "证书验证失败";
        } else if (e instanceof ConnectTimeoutException) {
            msg = "连接超时";
        } else if (e instanceof java.net.SocketTimeoutException) {
            msg = "连接超时";
        } else if (e instanceof java.net.UnknownHostException) {
            msg = "主机地址未知";
        } else {
            msg = "未知异常";
        }
        return new Exception(msg);
    }

    public interface NET_ERROR {
        String UNAUTHORIZED = "401";
        String FORBIDDEN = "403";
        String NOT_FOUND = "404";
        String REQUEST_TIMEOUT = "408";
        String INTERNAL_SERVER_ERROR = "500";
        String SERVICE_UNAVAILABLE = "503";
    }

    public interface APP_ERROR {
        String SUCCESS = "0000";
        String TOKEN_OUTTIME = "0004";
        String ACCOUNT_ERROR = "0003";
    }
}
