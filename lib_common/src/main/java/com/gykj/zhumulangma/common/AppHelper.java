package com.gykj.zhumulangma.common;

import android.app.Application;
import android.app.Notification;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.util.ActivityUtils;
import com.gykj.zhumulangma.common.aop.LoginHelper;
import com.gykj.zhumulangma.common.db.DaoMaster;
import com.gykj.zhumulangma.common.db.DaoSession;
import com.gykj.zhumulangma.common.net.NetManager;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.ximalaya.ting.android.opensdk.constants.ConstantsOpenSdk;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.NotificationColorUtils;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerConfig;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.lang.reflect.Method;


/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 8:36
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:App初始化帮助类
 */
public class AppHelper {

    private static Application mApplication;
    private static volatile AppHelper instance;


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

    public AppHelper initXmly() {
        ConstantsOpenSdk.isDebug = true;
        CommonRequest.getInstanse().init(mApplication, Constants.Third.XIMALAYA_SECRET);
        CommonRequest.getInstanse().setDefaultPagesize(20);

        AccessTokenManager.getInstanse().init(mApplication);
        if (AccessTokenManager.getInstanse().hasLogin()) {
           LoginHelper.registerLoginTokenChangeListener();
        }
        return this;
    }


    public  AppHelper  initXmlyPlayer() {
        try {
            Method method = XmPlayerConfig.getInstance(mApplication).getClass().getDeclaredMethod("setUseSystemPlayer", boolean.class);
            method.setAccessible(true);
            method.invoke(XmPlayerConfig.getInstance(mApplication), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationColorUtils.isTargerSDKVersion24More = true;
        try {
            Notification mNotification = XmNotificationCreater.getInstanse(mApplication)
                    .initNotification(mApplication, Class.forName(ActivityUtils.getLauncherActivity()));
            XmPlayerManager.getInstance(mApplication).init(Constants.Third.XIMALAYA_NOTIFICATION, mNotification);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 此代码表示播放时会去监测下是否已经下载(setDownloadPlayPathCallback 方法已经废弃 请使用如下方法)
        XmPlayerManager.getInstance(mApplication).setCommonBusinessHandle(XmDownloadManager.getInstance());
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

    public AppHelper initGreenDao(boolean logger) {
        QueryBuilder.LOG_SQL = logger;
        QueryBuilder.LOG_VALUES = logger;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mApplication, "zhumulangma.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        return this;
    }

    public AppHelper initLog() {
        TLog.init(BuildConfig.DEBUG);
        return this;
    }

    public AppHelper Net() {
        NetManager.init(new File( mApplication.getCacheDir(),"rx-cache"));
        return this;
    }
}
