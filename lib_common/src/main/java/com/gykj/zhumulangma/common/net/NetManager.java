package com.gykj.zhumulangma.common.net;

import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.AppConstants;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:Retrofit
 */
public class NetManager {
    private static final String TAG = "NetManager";
    public static NetManager instance;
    private Retrofit mRetrofit;
    private int mHostStatus = Api.STATUS_ONLINE;

    private NetManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header(Api.TOKEN_KEY, SPUtils.getInstance().getString(AppConstants.SP.TOKEN));
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        //证书相关
       /* SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();
        if (sslContext != null) {
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            okHttpBuilder.sslSocketFactory(socketFactory);
        }
        okHttpBuilder.hostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);*/

        //动态改变baseUrl拦截器
        builder.addInterceptor(new BaseUrlInterceptor());
        //防抓包
        builder.proxy(Proxy.NO_PROXY);
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Api.ONLINE_HOST1)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetManager getInstance() {
        if (instance == null) {
            synchronized (NetManager.class) {
                if (instance == null) {
                    instance = new NetManager();
                }
            }
        }
        return instance;
    }

    /**
     * 创建一个公共服务
     *
     * @return
     */
    public CommonService getCommonService() {
        return mRetrofit.create(CommonService.class);
    }

    /**
     * 创建一个User服务
     *
     * @return
     */
    public UserService getUserService() {
        return mRetrofit.create(UserService.class);
    }

    /**
     * 切换网络环境(默认在线)
     *
     * @param hostStatus Api.STATUS_OFFLINE(离线)/Api.STATUS_ONLINE(在线)
     */
    public void setHostStatus(int hostStatus) {
        Log.d(TAG, "setHostStatus() called with: hostStatus = [" + hostStatus + "]");
        this.mHostStatus = hostStatus;
    }

    /**
     * 动态更换主机
     */
    public class BaseUrlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //获取request
            Request request = chain.request();
            //从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldHttpUrl = request.url();
            //获取request的创建者builder
            Request.Builder builder = request.newBuilder();
            //从request中获取headers，通过给定的键url_name
            List<String> headerValues = request.headers(Api.HOST_KEY);
            if (headerValues != null && headerValues.size() > 0) {
                //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
                builder.removeHeader(Api.HOST_KEY);
                //匹配获得新的BaseUrl
                String headerValue = headerValues.get(0);
                HttpUrl newBaseUrl = null;
                if (Api.HOST1_VALUE.equals(headerValue) && mHostStatus == Api.STATUS_OFFLINE) {
                    newBaseUrl = HttpUrl.parse(Api.OFFLINE_HOST1);
                } else if (Api.HOST1_VALUE.equals(headerValue) && mHostStatus == Api.STATUS_ONLINE) {
                    newBaseUrl = HttpUrl.parse(Api.ONLINE_HOST1);
                } else if (Api.HOST2_VALUE.equals(headerValue) && mHostStatus == Api.STATUS_OFFLINE) {
                    newBaseUrl = HttpUrl.parse(Api.OFFLINE_HOST2);
                } else if (Api.HOST2_VALUE.equals(headerValue) && mHostStatus == Api.STATUS_ONLINE) {
                    newBaseUrl = HttpUrl.parse(Api.ONLINE_HOST2);
                } else {
                    newBaseUrl = oldHttpUrl;
                }
                //重建新的HttpUrl，修改需要修改的url部分
                HttpUrl newFullUrl = oldHttpUrl
                        .newBuilder()
                        .host(newBaseUrl.host())//更换主机名
                        .port(newBaseUrl.port())//更换端口
                        .build();
                Log.d(TAG, "Url重定向:" + newFullUrl.toString());
                return chain.proceed(builder.url(newFullUrl).build());
            }
            return chain.proceed(request);
        }
    }
}