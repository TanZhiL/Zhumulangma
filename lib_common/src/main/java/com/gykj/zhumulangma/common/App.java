package com.gykj.zhumulangma.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.gykj.util.log.TLog;
import com.gykj.videotrimmer.VideoTrimmer;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.bean.TrackDownloadBean;
import com.gykj.zhumulangma.common.dao.DaoMaster;
import com.gykj.zhumulangma.common.dao.DaoSession;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.ximalaya.ting.android.opensdk.auth.constants.XmlyConstants;
import com.ximalaya.ting.android.opensdk.constants.ConstantsOpenSdk;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.httputil.XimalayaException;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.NotificationColorUtils;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerConfig;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.opensdk.util.Logger;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.http.RequestParams;
import com.ximalaya.ting.android.sdkdownloader.http.app.RequestTracker;
import com.ximalaya.ting.android.sdkdownloader.http.request.UriRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;

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
 * Description: <初始化应用程序><br>
 * Author:      mxdl<br>
 * Date:        2018/6/6<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class App extends android.app.Application implements IXmPlayerStatusListener, IXmDownloadTrackCallBack {
    private static App mApplication;
    private static final String TAG = "App";
    private ZhumulangmaModel model =new ZhumulangmaModel(this);
    //static 代码段可以防止内存泄露
    static {

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            ClassicsHeader classicsHeader = new ClassicsHeader(context);
            classicsHeader.setTextSizeTitle(14);
            classicsHeader.setTextSizeTime(10);
            classicsHeader.setDrawableSize(18);
            classicsHeader.setFinishDuration(0);
            return classicsHeader;
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            ClassicsFooter classicsFooter = new ClassicsFooter(context);
            classicsFooter.setTextSizeTitle(14);
            classicsFooter.setDrawableSize(18);
            classicsFooter.setFinishDuration(0);
            return classicsFooter;
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initGreenDao();
        initXmly();
        MultiDex.install(this);

        TLog.init(AppConfig.LOGER);

        if (AppConfig.ISDEBUGAROUTER) {
            // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化

        RetrofitManager.init(this);

        Utils.init(this);

        // 建议在Application里初始化
        Fragmentation.builder()
                // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(false)
                .install();

        VideoTrimmer.init(this);

        /*JPushInterface.setDebugMode(true);
        JPushInterface.init(this);*/

        if (AppConfig.ISDORAEMONKIT) {
            //调试助手
            DoraemonKit.install(this);
            // H5任意门功能需要，非必须
            DoraemonKit.setWebDoorCallback((context, s) -> {
                // 使用自己的H5容器打开这个链接
            });
        }

    }

    public static App getInstance() {
        return mApplication;
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "zhumulangma.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private static DaoSession daoSession;

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    private void initXmly() {

        ConstantsOpenSdk.isDebug = true;
        CommonRequest.getInstanse().init(this, AppConstants.Ximalaya.SECRET);

        if (BaseUtil.isMainProcess(this)) {
            AccessTokenManager.getInstanse().init(this);
            if (AccessTokenManager.getInstanse().hasLogin()) {
                registerLoginTokenChangeListener(this);
            }
        }
        if(!BaseUtil.isPlayerProcess(this)) {
            XmDownloadManager.Builder(this)
                    .maxDownloadThread(1)            // 最大的下载个数 默认为1 最大为3
                    //   .maxSpaceSize(Long.MAX_VALUE)	// 设置下载文件占用磁盘空间最大值，单位字节。不设置没有限制
                    .connectionTimeOut(15000)        // 下载时连接超时的时间 ,单位毫秒 默认 30000
                    .readTimeOut(15000)                // 下载时读取的超时时间 ,单位毫秒 默认 30000
                    .fifo(false)                    // 等待队列的是否优先执行先加入的任务. false表示后添加的先执行(不会改变当前正在下载的音频的状态) 默认为true
                    .maxRetryCount(3)                // 出错时重试的次数 默认2次
                    .requestTracker(requestTracker)                // 出错时重试的次数 默认2次
                    .progressCallBackMaxTimeSpan(1000)//  进度条progress 更新的频率 默认是800
                    .savePath(getExternalFilesDir("mp3").getAbsolutePath())    // 保存的地址 会检查这个地址是否有效
                    .create();
            XmDownloadManager.getInstance().addDownloadStatueListener(this);
        }
        // 此代码表示播放时会去监测下是否已经下载(setDownloadPlayPathCallback 方法已经废弃 请使用如下方法)
        XmPlayerManager.getInstance(this).setCommonBusinessHandle(XmDownloadManager.getInstance());
        try {
            Method method = XmPlayerConfig.getInstance(this).getClass().getDeclaredMethod("setUseSystemPlayer", Boolean.class);
            method.setAccessible(true);
            method.invoke(XmPlayerConfig.getInstance(this),true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationColorUtils.isTargerSDKVersion24More = true;
        try {
            Notification mNotification = XmNotificationCreater.getInstanse(this)
                    .initNotification(this.getApplicationContext(), Class.forName(ActivityUtils.getLauncherActivity()));
            XmPlayerManager.getInstance(this).init(NOTIFICATION_ID, mNotification);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        XmPlayerManager.getInstance(this).addPlayerStatusListener(this);

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
                EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.LOGIN));
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

                if (jsonObject != null) {
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
            if (execute.isSuccessful()) {
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

    @Override
    public void onTerminate() {
        super.onTerminate();
        XmPlayerManager.getInstance(this).removePlayerStatusListener(this);
        XmDownloadManager.getInstance().removeDownloadStatueListener(this);
        XmPlayerManager.release();
        CommonRequest.release();
    }

    @Override
    public void onPlayStart() {

    }

    @Override
    public void onPlayPause() {
        Log.e(TAG, "onPlayPause: " );
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(null!=notificationManager){
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {

    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int i) {

    }

    @Override
    public void onPlayProgress(int i, int i1) {
        Track currSound = XmPlayerManager.getInstance(this).getCurrSoundIgnoreKind(true);
        if(null==currSound){
            return;
        }
        int currPos=XmPlayerManager.getInstance(this).getPlayCurrPositon();
        int duration=XmPlayerManager.getInstance(this).getDuration();
        model.insert(new PlayHistoryBean(currSound.getDataId(),currSound.getKind(),100 * currPos /duration,
                System.currentTimeMillis(),currSound)).subscribe();
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }

    @Override
    public void onWaiting(Track track) {
        model.insert(new TrackDownloadBean(track.getDataId(),XmDownloadManager.getInstance()
                .getSingleTrackDownloadStatus(track.getDataId()))).subscribe();
    }

    @Override
    public void onStarted(Track track) {
        model.insert(new TrackDownloadBean(track.getDataId(),XmDownloadManager.getInstance()
                .getSingleTrackDownloadStatus(track.getDataId()))).subscribe();
    }

    @Override
    public void onSuccess(Track track) {
        model.insert(new TrackDownloadBean(track.getDataId(),XmDownloadManager.getInstance()
                .getSingleTrackDownloadStatus(track.getDataId()))).subscribe();
    }

    @Override
    public void onError(Track track, Throwable throwable) {
        model.insert(new TrackDownloadBean(track.getDataId(),XmDownloadManager.getInstance()
                .getSingleTrackDownloadStatus(track.getDataId()))).subscribe();
    }

    @Override
    public void onCancelled(Track track, com.ximalaya.ting.android.sdkdownloader.task.Callback.CancelledException e) {
        model.insert(new TrackDownloadBean(track.getDataId(),XmDownloadManager.getInstance()
                .getSingleTrackDownloadStatus(track.getDataId()))).subscribe();
    }

    @Override
    public void onProgress(Track track, long l, long l1) {

    }

    @Override
    public void onRemoved() {
    }
}
