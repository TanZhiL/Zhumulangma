package com.ximalaya.ting.android.opensdk.test;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

import com.ximalaya.ting.android.opensdk.constants.ConstantsOpenSdk;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.test.reciver.MyPlayerReceiver;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.opensdk.util.Logger;
import com.ximalaya.ting.android.player.XMediaPlayerConstants;
import com.ximalaya.ting.android.sdkdownloader.http.RequestParams;
import com.ximalaya.ting.android.sdkdownloader.http.app.RequestTracker;
import com.ximalaya.ting.android.sdkdownloader.http.request.UriRequest;

import org.xutils.x;

/**
 * Created by le.xin on 2016/6/12.
 */
public class TingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
//        x.Ext.setDebug(true); // 是否输出debug日志, 开启debug会影响性能.

        ConstantsOpenSdk.isDebug = true;
        XMediaPlayerConstants.isDebug = true;
        XMediaPlayerConstants.isDebugPlayer = true;
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

        if(!BaseUtil.isPlayerProcess(this)) {
//            XmDownloadManager.Builder(this)
//                    .maxDownloadThread(1)			// 最大的下载个数 默认为1 最大为3
//                    .maxSpaceSize(Long.MAX_VALUE)	// 设置下载文件占用磁盘空间最大值，单位字节。不设置没有限制
//                    .connectionTimeOut(15000)		// 下载时连接超时的时间 ,单位毫秒 默认 30000
//                    .readTimeOut(15000)				// 下载时读取的超时时间 ,单位毫秒 默认 30000
//                    .fifo(false)					// 等待队列的是否优先执行先加入的任务. false表示后添加的先执行(不会改变当前正在下载的音频的状态) 默认为true
//                    .maxRetryCount(3)				// 出错时重试的次数 默认2次
//                    .progressCallBackMaxTimeSpan(1000)//  进度条progress 更新的频率 默认是800
//                    .requestTracker(requestTracker)	// 日志 可以打印下载信息
//                    .savePath(mp3)	// 保存的地址 会检查这个地址是否有效
//                    .create();
        }

        if(BaseUtil.getCurProcessName(this).contains(":player")) {
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
