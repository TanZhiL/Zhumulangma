package com.gykj.zhumulangma.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.Utils;
import com.gykj.zhumulangma.common.dao.DaoMaster;
import com.gykj.zhumulangma.common.dao.DaoSession;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.hjq.toast.ToastUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.ximalaya.ting.android.opensdk.auth.constants.XmlyConstants;
import com.ximalaya.ting.android.opensdk.constants.ConstantsOpenSdk;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.httputil.XimalayaException;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.NotificationColorUtils;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerConfig;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.opensdk.util.Logger;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import me.yokeyword.fragmentation.Fragmentation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.gykj.zhumulangma.common.AppConstants.Ximalaya.NOTIFICATION_ID;
import static com.gykj.zhumulangma.common.AppConstants.Ximalaya.REDIRECT_URL;
import static com.gykj.zhumulangma.common.AppConstants.Ximalaya.REFRESH_TOKEN_URL;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 8:36
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:App初始化帮助类
 */
public class AppHelper {

    private static final String TAG = "AppHelper";

    private static Application mApplication;
    private static volatile AppHelper instance;
    public static RefWatcher refWatcher;


    private AppHelper() {
    }

    public static AppHelper getInstance(Application application) {
        if (instance == null) {
            synchronized (AppHelper.class) {
                if (instance == null) {
                    mApplication = application;
                    instance = new AppHelper();
                }
            }
        }
        return instance;
    }

    public AppHelper initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(mApplication)) {
            return this;
        }
        refWatcher = LeakCanary.install(mApplication);
        return this;
    }

    public AppHelper initAgentWebX5() {
        QbSdk.initX5Environment(mApplication, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.d(TAG, "onCoreInitFinished() called");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.d(TAG, "onViewInitFinished() called with: b = [" + b + "]");
            }
        });
        return this;
    }

    public AppHelper initXmly() {
        ConstantsOpenSdk.isDebug = true;
        CommonRequest.getInstanse().init(mApplication, AppConstants.Ximalaya.SECRET);
        CommonRequest.getInstanse().setDefaultPagesize(20);

        if (BaseUtil.isMainProcess(mApplication)) {
            AccessTokenManager.getInstanse().init(mApplication);
            if (AccessTokenManager.getInstanse().hasLogin()) {
                registerLoginTokenChangeListener(mApplication);
            }
        }
        return this;
    }

    public AppHelper initUM() {
        UMConfigure.init(mApplication, AppConstants.Share.UM_ID
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setSinaWeibo(AppConstants.Share.SINA_ID,AppConstants.Share.SINA_KEY,"http://sns.whalecloud.com");
        PlatformConfig.setQQZone(AppConstants.Share.QQ_ID, AppConstants.Share.QQ_KEY);
        return this;
    }

    public AppHelper initXmlyPlayer() {
        try {
            Method method = XmPlayerConfig.getInstance(mApplication).getClass().getDeclaredMethod("setUseSystemPlayer", Boolean.class);
            method.setAccessible(true);
            method.invoke(XmPlayerConfig.getInstance(mApplication), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 此代码表示播放时会去监测下是否已经下载(setDownloadPlayPathCallback 方法已经废弃 请使用如下方法)
        XmPlayerManager.getInstance(mApplication).setCommonBusinessHandle(XmDownloadManager.getInstance());


        NotificationColorUtils.isTargerSDKVersion24More = true;
        try {
            Notification mNotification = XmNotificationCreater.getInstanse(mApplication)
                    .initNotification(mApplication, Class.forName(ActivityUtils.getLauncherActivity()));
            XmPlayerManager.getInstance(mApplication).init(NOTIFICATION_ID, mNotification);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    public AppHelper initXmlyDownloader() {
        if (BaseUtil.isMainProcess(mApplication)) {
            XmDownloadManager.Builder(mApplication)
                    .maxDownloadThread(1)            // 最大的下载个数 默认为1 最大为3
                    //   .maxSpaceSize(Long.MAX_VALUE)	// 设置下载文件占用磁盘空间最大值，单位字节。不设置没有限制
                    .connectionTimeOut(15000)        // 下载时连接超时的时间 ,单位毫秒 默认 30000
                    .readTimeOut(15000)                // 下载时读取的超时时间 ,单位毫秒 默认 30000
                    //     .fifo(false)                    // 等待队列的是否优先执行先加入的任务. false表示后添加的先执行(不会改变当前正在下载的音频的状态) 默认为true
                    .maxRetryCount(3)                // 出错时重试的次数 默认2次
                    .progressCallBackMaxTimeSpan(10)//  进度条progress 更新的频率 默认是800
                    .savePath(mApplication.getExternalFilesDir("mp3").getAbsolutePath())    // 保存的地址 会检查这个地址是否有效
                    .create();
        }
        return this;
    }

    private static DaoSession daoSession;

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public AppHelper initGreenDao() {
        QueryBuilder.LOG_SQL = false;
        QueryBuilder.LOG_VALUES = false;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mApplication, "zhumulangma.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        return this;
    }

    public AppHelper initLog() {
        TLog.init(true);
        return this;
    }

    public AppHelper initBugly() {
        if (BaseUtil.isMainProcess(mApplication)) {
            Beta.largeIconId = R.drawable.common_launcher_ting;
            Beta.smallIconId = R.drawable.common_launcher_ting;
            Beta.upgradeDialogLayoutId = R.layout.common_dialog_update;

            Beta.canNotifyUserRestart = true;
            //生产环境
//            Bugly.init(mApplication, AppConstants.Bugly.UM_ID, false);
            //开发设备
            Bugly.setIsDevelopmentDevice(mApplication, true);
            Bugly.init(mApplication, AppConstants.Bugly.ID, true);
        }
        return this;
    }

    public AppHelper initRouter() {

        if (BuildConfig.IS_DEBUG) {
            // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(mApplication); // 尽可能早，推荐在Application中初始化

        return this;
    }

    public AppHelper initUtils() {
        Utils.init(mApplication);
        ToastUtils.init(mApplication);
        ToastUtils.setView(R.layout.common_layout_toast);
        ToastUtils.setGravity(Gravity.CENTER,0,0);
        KeyboardUtils.clickBlankArea2HideSoftInput();
        return this;
    }
    @SuppressLint("RestrictedApi")
    public AppHelper initCrashView() {
            CaocConfig.Builder.create()
                    .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                    .enabled(true)//这阻止了对崩溃的拦截,false表示阻止。用它来禁用customactivityoncrash框架
                    .minTimeBetweenCrashesMs(2000)      //定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中。比如：在规定的时间内再次崩溃，框架将不处理，让系统处理！
                    .errorActivity(DefaultErrorActivity.class) //程序崩溃后显示的页面
                    .apply();
        //如果没有任何配置，程序崩溃显示的是默认的设置
        CustomActivityOnCrash.install(mApplication);
        return this;
    }


    public AppHelper initFragmentation(boolean isDebug) {

        // 建议在Application里初始化
        Fragmentation.builder()
                // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(isDebug)
                .install();
        return this;
    }

    public AppHelper initSpeech() {
        SpeechUtility.createUtility(mApplication, SpeechConstant.APPID + "=" + AppConstants.Speech.ID);
        return this;
    }

    public static void registerLoginTokenChangeListener(final Context context) {
        // 使用此回调了就表示贵方接了需要用户登录才能访问的接口,如果没有此类接口可以不用设置此接口,之前的逻辑没有发生改变
        CommonRequest.getInstanse().setITokenStateChange(new CommonRequest.ITokenStateChange() {
            // 此接口表示token已经失效 ,
            @Override
            public boolean getTokenByRefreshSync() {
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
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
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
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
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.LOGIN));
            }
        });
    }

    private static void refresh() throws XimalayaException {
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

                if (jsonObject != null) {
                    AccessTokenManager.getInstanse().setAccessTokenAndUid(jsonObject.optString("access_token"),
                            jsonObject.optString("refresh_token"), jsonObject.optLong("expires_in"), jsonObject
                                    .optString("uid"));
                }
            }
        });
    }

    private static boolean refreshSync() throws XimalayaException {
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
            if (execute.isSuccessful()) {
                try {
                    String string = execute.body().string();
                    JSONObject jsonObject = new JSONObject(string);
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

    private class CrashEventListener implements CustomActivityOnCrash.EventListener {
        @Override
        public void onLaunchErrorActivity() {
            Log.d(TAG, "onLaunchErrorActivity() called");
        }

        @Override
        public void onRestartAppFromErrorActivity() {
            Log.d(TAG, "onRestartAppFromErrorActivity() called");
        }

        @Override
        public void onCloseAppFromErrorActivity() {
            Log.d(TAG, "onCloseAppFromErrorActivity() called");
        }
    }
}
