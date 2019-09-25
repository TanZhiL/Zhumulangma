package com.gykj.zhumulangma.common.net;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.net.config.API;
import com.gykj.zhumulangma.common.util.log.TLog;

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
 * Description: <RetrofitManager><br>
 * Author:      mxdl<br>
 * Date:        2019/6/22<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class RetrofitManager {
    public static RetrofitManager retrofitManager;
    public static Context mContext;
    private Retrofit mRetrofit;
    private int hostStatus = API.HostStatus.ONLINE;
    OkHttpClient.Builder okHttpBuilder;

    private RetrofitManager() {
        okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.interceptors().add(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        okHttpBuilder.addInterceptor(new Interceptor() {
                                         @Override
                                         public Response intercept(Chain chain) throws IOException {
                                             Request original = chain.request();
                                             Request.Builder requestBuilder = original.newBuilder()
                                                     .header("token", SPUtils.getInstance().getString(AppConstants.SP.TOKEN));
                                             Request request = requestBuilder.build();
                                             return chain.proceed(request);
                                         }
                                     });
        //证书相关
       /* SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();
        if (sslContext != null) {
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            okHttpBuilder.sslSocketFactory(socketFactory);
        }
        okHttpBuilder.hostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);*/


        //动态改变baseUrl拦截器
        okHttpBuilder.addInterceptor(new BaseUrlInterceptor());
        //防抓包
        okHttpBuilder.proxy(Proxy.NO_PROXY);
        mRetrofit = new Retrofit.Builder()
                .client(okHttpBuilder.build())
                .baseUrl(API.ONLINE_HOST1)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void init(Application application) {
        mContext = application;
    }

    public static RetrofitManager getInstance() {
        if (retrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (retrofitManager == null) {
                    retrofitManager = new RetrofitManager();
                }
            }
        }
        return retrofitManager;
    }

    /**
     * 创建一个公共服务
     *
     * @return
     */
    public CommonService getCommonService() {
        return mRetrofit.create(CommonService.class);
    }


    public EventService getEventService() {
        return mRetrofit.create(EventService.class);
    }


    public PollutionService getPollutionService() {
        return mRetrofit.create(PollutionService.class);
    }
    public TaskService getTaskService() {
        return mRetrofit.create(TaskService.class);
    }

    public VideoService getVideoService() {
        return mRetrofit.create(VideoService.class);
    }

    public void setHostStatus(int hostStatus) {
        TLog.d("切换环境:hostStatus = "+hostStatus);
        this.hostStatus = hostStatus;
    }

    /**
     * 动态更换baseUrl
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
            List<String> headerValues = request.headers(API.BaseUrl.KEY);
            if (headerValues != null && headerValues.size() > 0) {
                //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
                builder.removeHeader(API.BaseUrl.KEY);
                //匹配获得新的BaseUrl
                String headerValue = headerValues.get(0);
                HttpUrl newBaseUrl = null;
                if (API.BaseUrl.HOST1.equals(headerValue)&& hostStatus == API.HostStatus.OFFLINE) {
                    newBaseUrl = HttpUrl.parse(API.OFFLINE_HOST1);
                }else if (API.BaseUrl.HOST1.equals(headerValue)&& hostStatus == API.HostStatus.ONLINE) {
                    newBaseUrl = HttpUrl.parse(API.ONLINE_HOST1);
                } else if (API.BaseUrl.HOST2.equals(headerValue)&& hostStatus == API.HostStatus.OFFLINE) {
                    newBaseUrl = HttpUrl.parse(API.OFFLINE_HOST2);
                } else if (API.BaseUrl.HOST2.equals(headerValue)&& hostStatus == API.HostStatus.ONLINE) {
                    newBaseUrl = HttpUrl.parse(API.ONLINE_HOST2);
                } else {
                    newBaseUrl = oldHttpUrl;
                }
                //重建新的HttpUrl，修改需要修改的url部分
                HttpUrl newFullUrl = oldHttpUrl
                        .newBuilder()
                        .host(newBaseUrl.host())//更换主机名
                        .port(newBaseUrl.port())//更换端口
                        .build();

                Log.i("BaseUrlInterceptor","Url重定向:"+newFullUrl.toString());
                return chain.proceed(builder.url(newFullUrl).build());
            }
            return chain.proceed(request);
        }
    }
}