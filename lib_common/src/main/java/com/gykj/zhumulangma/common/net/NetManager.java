package com.gykj.zhumulangma.common.net;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.db.DBManager;
import com.gykj.zhumulangma.common.net.service.CommonService;
import com.gykj.zhumulangma.common.net.service.HomeService;
import com.gykj.zhumulangma.common.net.service.UserService;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gykj.zhumulangma.common.net.Constans.HOST1_BING;
import static com.gykj.zhumulangma.common.net.Constans.HOST2_XMLY;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:网络请求管理类
 */
public class NetManager {
    private static final String TAG = "NetManager";
    private static File mCacheFile;
    private static volatile NetManager instance;
    private CacheProvider mCacheProvider;
    private Retrofit mRetrofit;
    private int mNetStatus;
    private volatile CommonService mCommonService;
    private volatile UserService mUserService;
    private volatile HomeService mHomeService;

    static final List<Map<String, String>> HOSTS = new ArrayList<>();

    static {
        Map<String, String> online = new HashMap<>(2, 1);
        online.put(HOST1_BING, "https://cn.bing.com/");
        online.put(HOST2_XMLY, "https://api.ximalaya.com");
        HOSTS.add(online);
    }

    public static void init(File cacheFile) {
        if (!cacheFile.exists())
            cacheFile.mkdirs();
        mCacheFile = cacheFile;
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

    private NetManager() {
        //先异步获取token
        DBManager.getInstance()
                .getSPString(Constants.SP.TOKEN)
                .compose(RxAdapter.exceptionTransformer())
                .subscribe(token -> {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            //添加头部信息
                            .addInterceptor(chain -> {
                                Request.Builder requestBuilder = chain.request().newBuilder()
                                        .header(Constans.TOKEN_KEY, token);
                                return chain.proceed(requestBuilder.build());
                            })
                            //动态改变baseUrl拦截器
                            .addInterceptor(new UrlInterceptor())
                            //日志
                            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                            //防抓包
                            .proxy(Proxy.NO_PROXY);

                    mRetrofit = new Retrofit.Builder()
                            .client(builder.build())
                            .baseUrl("https://www.baidu.com")
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    mCacheProvider = new RxCache.Builder()
                            .persistence(mCacheFile, new GsonSpeaker())
                            .using(CacheProvider.class);
                });
    }

    /**
     * 获取缓存对象
     *
     * @return
     */
    public CacheProvider getCacheProvider() {
        return mCacheProvider;
    }

    public CommonService getCommonService() {
        if (mCommonService == null) {
            synchronized (NetManager.class) {
                if (mCommonService == null) {
                    mCommonService = mRetrofit.create(CommonService.class);
                }
            }
        }
        return mCommonService;
    }
    public UserService getUserService() {
        if (mUserService == null) {
            synchronized (NetManager.class) {
                if (mUserService == null) {
                    mUserService = mRetrofit.create(UserService.class);
                }
            }
        }
        return mUserService;
    }

    public HomeService getHomeService() {
        if (mHomeService == null) {
            synchronized (NetManager.class) {
                if (mHomeService == null) {
                    mHomeService = mRetrofit.create(HomeService.class);
                }
            }
        }
        return mHomeService;
    }

    /**
     * 切换网络环境(默认在线)
     *
     * @param netStatus Constans.NET_OFFLINE(离线)/Constans.NET_ONLINE(在线)
     */
    public void setNetStatus(int netStatus) {
        Log.d(TAG, "setNetStatus() called with: netStatus = [" + netStatus + "]");
        this.mNetStatus = netStatus;
    }

    /**
     * 动态更换主机
     */
    class UrlInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            //获取request
            Request request = chain.request();
            //从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldUrl = request.url();
            //获取request的创建者builder
            Request.Builder builder = request.newBuilder();
            //从request中获取headers，通过给定的键url_name
            String hostValue = request.header(Constans.HOST_KEY);
            if (TextUtils.isEmpty(hostValue)) {
                return chain.proceed(request);
            }
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            builder.removeHeader(Constans.HOST_KEY);
            HttpUrl newBaseUrl = getBaseUrl(hostValue);
            if (null == newBaseUrl) {
                return chain.proceed(request);
            }
            //重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newUrl = oldUrl
                    .newBuilder()
                    .host(newBaseUrl.host())//更换主机名
                    .port(newBaseUrl.port())//更换端口
                    .build();
            Log.d(TAG, "Url重定向:" + newUrl.toString());
            return chain.proceed(builder.url(newUrl).build());
        }
    }

    /**
     * 根据header和netStatus组合baseUrl
     *
     * @param hostValue
     * @return
     */
    private HttpUrl getBaseUrl(String hostValue) {
        return HttpUrl.parse(Objects.requireNonNull(HOSTS.get(mNetStatus).get(hostValue)));
    }

}