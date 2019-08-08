package com.gykj.zhumulangma.common.net.http;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * Description: <ExceptionHandler><br>
 * Author:      mxdl<br>
 * Date:        2019/3/18<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class ExceptionHandler {

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, SYSTEM_ERROR.HTTP_ERROR);
            switch (String.valueOf(httpException.code())) {
                case SYSTEM_ERROR.UNAUTHORIZED:
                    ex.message = "操作未授权";
                    break;
                case SYSTEM_ERROR.FORBIDDEN:
                    ex.message = "请求被拒绝";
                    break;
                case SYSTEM_ERROR.NOT_FOUND:
                    ex.message = "资源不存在";
                    break;
                case SYSTEM_ERROR.REQUEST_TIMEOUT:
                    ex.message = "服务器执行超时";
                    break;
                case SYSTEM_ERROR.INTERNAL_SERVER_ERROR:
                    ex.message = "服务器内部错误";
                    break;
                case SYSTEM_ERROR.SERVICE_UNAVAILABLE:
                    ex.message = "服务器不可用";
                    break;
                default:
                    ex.message = "网络错误";
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException || e instanceof MalformedJsonException) {
            ex = new ResponseThrowable(e, SYSTEM_ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponseThrowable(e, SYSTEM_ERROR.NETWORD_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLException) {
            ex = new ResponseThrowable(e, SYSTEM_ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ResponseThrowable(e, SYSTEM_ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponseThrowable(e, SYSTEM_ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof java.net.UnknownHostException) {
            ex = new ResponseThrowable(e, SYSTEM_ERROR.TIMEOUT_ERROR);
            ex.message = "主机地址未知";
            return ex;
        } else if (e instanceof ResponseThrowable) {
            return (ResponseThrowable) e;
        } else {
            ex = new ResponseThrowable(e, SYSTEM_ERROR.UNKNOWN);
            ex.message = "未知错误";
            return ex;
        }

    }

    public class SYSTEM_ERROR {
        public static final String UNAUTHORIZED = "401";
        public static final String FORBIDDEN = "403";
        public static final String NOT_FOUND = "404";
        public static final String REQUEST_TIMEOUT = "408";
        public static final String INTERNAL_SERVER_ERROR = "500";
        public static final String SERVICE_UNAVAILABLE = "503";

        /**
         * 未知错误
         */
        public static final String UNKNOWN = "1000";
        /**
         * 解析错误
         */
        public static final String PARSE_ERROR = "1001";
        /**
         * 网络错误
         */
        public static final String NETWORD_ERROR = "1002";
        /**
         * 协议出错
         */
        public static final String HTTP_ERROR = "1003";

        /**
         * 证书出错
         */
        public static final String SSL_ERROR = "1005";

        /**
         * 连接超时
         */
        public static final String TIMEOUT_ERROR = "1006";

    }

    public interface APP_ERROR {
        int SUCC = 0;//	处理成功，无错误

        String SUCCESS = "0000";
        String TOKEN_OUTTIME = "0004";
        String ACCOUNT_ERROR= "0003";
    }
}
