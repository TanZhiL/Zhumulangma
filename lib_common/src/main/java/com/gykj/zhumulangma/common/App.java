package com.gykj.zhumulangma.common;

import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.Utils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.gykj.util.log.TLog;
import com.gykj.videotrimmer.VideoTrimmer;
import com.gykj.zhumulangma.common.dao.DaoMaster;
import com.gykj.zhumulangma.common.dao.DaoSession;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.ximalaya.ting.android.opensdk.constants.ConstantsOpenSdk;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.util.Logger;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.http.RequestParams;
import com.ximalaya.ting.android.sdkdownloader.http.app.RequestTracker;
import com.ximalaya.ting.android.sdkdownloader.http.request.UriRequest;

import me.yokeyword.fragmentation.Fragmentation;

/**
 * Description: <初始化应用程序><br>
 * Author:      mxdl<br>
 * Date:        2018/6/6<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class App extends android.app.Application {
    private static App mApplication;
    //static 代码段可以防止内存泄露
    static {

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            ClassicsHeader classicsHeader=new ClassicsHeader(context);
            classicsHeader.setTextSizeTitle(14);
            classicsHeader.setTextSizeTime(10);
            classicsHeader.setDrawableSize(18);
            classicsHeader.setFinishDuration(0);
            return classicsHeader;
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            ClassicsFooter classicsFooter=new ClassicsFooter(context);
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

        if(AppConfig.ISDORAEMONKIT) {
            //调试助手
            DoraemonKit.install(this);
            // H5任意门功能需要，非必须
            DoraemonKit.setWebDoorCallback((context, s) -> {
                // 使用自己的H5容器打开这个链接
            });
        }

    }
    public static App getInstance(){
        return mApplication;
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "zhumulangma.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private static DaoSession daoSession;
    public  static DaoSession getDaoSession() {
        return daoSession;
    }

    private void initXmly() {

        ConstantsOpenSdk.isDebug = true;
        CommonRequest.getInstanse().init(this,AppConstants.Ximalaya.SECRET);
        XmPlayerManager.getInstance(this).init();
        XmDownloadManager.Builder(this)
                .maxDownloadThread(1)			// 最大的下载个数 默认为1 最大为3
             //   .maxSpaceSize(Long.MAX_VALUE)	// 设置下载文件占用磁盘空间最大值，单位字节。不设置没有限制
                .connectionTimeOut(15000)		// 下载时连接超时的时间 ,单位毫秒 默认 30000
                .readTimeOut(15000)				// 下载时读取的超时时间 ,单位毫秒 默认 30000
                .fifo(false)					// 等待队列的是否优先执行先加入的任务. false表示后添加的先执行(不会改变当前正在下载的音频的状态) 默认为true
                .maxRetryCount(3)				// 出错时重试的次数 默认2次
                .requestTracker(requestTracker)				// 出错时重试的次数 默认2次
                .progressCallBackMaxTimeSpan(1000)//  进度条progress 更新的频率 默认是800
                .savePath(getExternalFilesDir("mp3").getAbsolutePath())	// 保存的地址 会检查这个地址是否有效
                .create();

        // 此代码表示播放时会去监测下是否已经下载(setDownloadPlayPathCallback 方法已经废弃 请使用如下方法)
        XmPlayerManager.getInstance(this).setCommonBusinessHandle(XmDownloadManager.getInstance());
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

    @Override
    public void onTerminate() {
        super.onTerminate();
        XmPlayerManager.release();
    }
}
