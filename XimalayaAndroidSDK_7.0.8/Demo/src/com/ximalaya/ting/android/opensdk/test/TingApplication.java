package com.ximalaya.ting.android.opensdk.test;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ximalaya.ting.android.opensdk.auth.constants.XmlyConstants;
import com.ximalaya.ting.android.opensdk.constants.ConstantsOpenSdk;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.httputil.XimalayaException;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.test.reciver.MyPlayerReceiver;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.opensdk.util.Logger;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.http.RequestParams;
import com.ximalaya.ting.android.sdkdownloader.http.app.RequestTracker;
import com.ximalaya.ting.android.sdkdownloader.http.request.UriRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ximalaya.ting.android.opensdk.test.XMAuthDemoActivity.REDIRECT_URL;

/**
 * Created by le.xin on 2016/6/12.
 */
public class TingApplication extends Application {
    public static final String REFRESH_TOKEN_URL = "https://api.ximalaya.com/oauth2/refresh_token?";

    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
//        x.Ext.setDebug(true); // 是否输出debug日志, 开启debug会影响性能.

        ConstantsOpenSdk.isDebug = true;
//        XMediaPlayerConstants.isDebug = true;
//        XMediaPlayerConstants.isDebugPlayer = true;
//        XmMediaPlayerFactory.setPlayerMode(true);

        String mp3 = getExternalFilesDir("mp3").getAbsolutePath();
        System.out.println("地址是  " + mp3);

        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if(DTransferConstants.isRelease) {
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this ,mAppSecret);
        } else {
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this ,mAppSecret);
        }

        if(BaseUtil.isMainProcess(this)) {
            AccessTokenManager.getInstanse().init(this);
            if(AccessTokenManager.getInstanse().hasLogin()) {
                registerLoginTokenChangeListener(this);
            }
        }

        if(!BaseUtil.isPlayerProcess(this)) {
            XmDownloadManager.Builder(this)
                    .maxDownloadThread(3)			// 最大的下载个数 默认为1 最大为3
                    .maxSpaceSize(Long.MAX_VALUE)	// 设置下载文件占用磁盘空间最大值，单位字节。不设置没有限制
                    .connectionTimeOut(15000)		// 下载时连接超时的时间 ,单位毫秒 默认 30000
                    .readTimeOut(15000)				// 下载时读取的超时时间 ,单位毫秒 默认 30000
                    .fifo(false)					// 等待队列的是否优先执行先加入的任务. false表示后添加的先执行(不会改变当前正在下载的音频的状态) 默认为true
                    .maxRetryCount(3)				// 出错时重试的次数 默认2次
                    .progressCallBackMaxTimeSpan(1000)//  进度条progress 更新的频率 默认是800
                    .requestTracker(requestTracker)	// 日志 可以打印下载信息
                    .savePath(mp3)	// 保存的地址 会检查这个地址是否有效
                    .create();
        }

        if(BaseUtil.isPlayerProcess(this)) {
            XmNotificationCreater instanse = XmNotificationCreater.getInstanse(this);
            instanse.setNextPendingIntent((PendingIntent)null);
            instanse.setPrePendingIntent((PendingIntent)null);

            String actionName = "com.app.test.android.Action_Close";
            Intent intent = new Intent(actionName);
            intent.setClass(this, MyPlayerReceiver.class);
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, 0);
            instanse.setClosePendingIntent(broadcast);

            String pauseActionName = "com.app.test.android.Action_PAUSE_START";
            Intent intent1 = new Intent(pauseActionName);
            intent1.setClass(this, MyPlayerReceiver.class);
            PendingIntent broadcast1 = PendingIntent.getBroadcast(this, 0, intent1, 0);
            instanse.setStartOrPausePendingIntent(broadcast1);
        }
    }

    public static void unregisterLoginTokenChangeListener() {
        CommonRequest.getInstanse().setITokenStateChange(null);
    }

    public static void registerLoginTokenChangeListener(final Context context) {
        // 使用此回调了就表示贵方接了需要用户登录才能访问的接口,如果没有此类接口可以不用设置此接口,之前的逻辑没有发生改变
        CommonRequest.getInstanse().setITokenStateChange(new CommonRequest.ITokenStateChange() {
            // 此接口表示token已经失效 ,
            @Override
            public boolean getTokenByRefreshSync() {
                if(!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    try {
                        return refreshSync();
                    } catch (XimalayaException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean getTokenByRefreshAsync() {
                if(!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    try {
                        refresh();
                        return true;
                    } catch (XimalayaException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public void tokenLosted() {
                Intent intent = new Intent(context ,XMAuthDemoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }


    public static void refresh() throws XimalayaException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .build();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token");
        builder.add(XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN, AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().getAppKey());
        builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().getDeviceId());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID);
        builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().getPackId());
        builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().getUid());
        builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URL);
        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url("https://api.ximalaya.com/oauth2/refresh_token?")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d("refresh", "refreshToken, request failed, error message = " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                String body = response.body().string();

                System.out.println("TingApplication.refreshSync  1  " + body);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(jsonObject != null) {
                    AccessTokenManager.getInstanse().setAccessTokenAndUid(jsonObject.optString("access_token"),
                            jsonObject.optString("refresh_token"), jsonObject.optLong("expires_in"), jsonObject
                                    .optString("uid"));
                }
            }
        });
    }

    public static boolean refreshSync() throws XimalayaException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .build();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token");
        builder.add(XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN, AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().getAppKey());
        builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().getDeviceId());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID);
        builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().getPackId());
        builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().getUid());
        builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URL);
        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url(REFRESH_TOKEN_URL)
                .post(body)
                .build();
        try {
            Response execute = client.newCall(request).execute();
            if(execute.isSuccessful()) {
                try {
                    String string = execute.body().string();
                    JSONObject jsonObject = new JSONObject(string);

                    System.out.println("TingApplication.refreshSync  2  " + string);

                    AccessTokenManager.getInstanse().setAccessTokenAndUid(jsonObject.optString("access_token"),
                            jsonObject.optString("refresh_token"), jsonObject.optLong("expires_in"), jsonObject
                                    .optString("uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private RequestTracker requestTracker = new RequestTracker() {
        @Override
        public void onWaiting(RequestParams params) {
            Logger.log("TingApplication : onWaiting " + params);
        }

        @Override
        public void onStart(RequestParams params) {
            Logger.log("TingApplication : onStart " + params);
        }

        @Override
        public void onRequestCreated(UriRequest request) {
            Logger.log("TingApplication : onRequestCreated " + request);
        }

        @Override
        public void onSuccess(UriRequest request, Object result) {
            Logger.log("TingApplication : onSuccess " + request + "   result = " + result);
        }

        @Override
        public void onRemoved(UriRequest request) {
            Logger.log("TingApplication : onRemoved " + request);
        }

        @Override
        public void onCancelled(UriRequest request) {
            Logger.log("TingApplication : onCanclelled " + request);
        }

        @Override
        public void onError(UriRequest request, Throwable ex, boolean isCallbackError) {
            Logger.log("TingApplication : onError " + request + "   ex = " + ex + "   isCallbackError = " + isCallbackError);
        }

        @Override
        public void onFinished(UriRequest request) {
            Logger.log("TingApplication : onFinished " + request);
        }
    };

}
