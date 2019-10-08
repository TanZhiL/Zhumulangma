package com.gykj.zhumulangma.common;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.widget.TRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.beta.Beta;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import static com.gykj.zhumulangma.common.AppConstants.Ximalaya.NOTIFICATION_ID;


/**
 * Author: Thomas.
 * Date: 2019/9/18 13:58
 * Email: 1071931588@qq.com
 * Description:App
 */
public class App extends Application {
    private static App mApplication;
    private static final String TAG = "App";

    public static App getInstance() {
        return mApplication;
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new TRefreshHeader(context));
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            ClassicsFooter classicsFooter = new ClassicsFooter(context);
            classicsFooter.setTextSizeTitle(12);
            classicsFooter.setDrawableSize(16);
            classicsFooter.setFinishDuration(0);
            return classicsFooter;
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(mApplication);
        Beta.installTinker();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        Log.d(TAG, "onCreate() called " + System.currentTimeMillis());
                AppHelper.getInstance(this)
                        .initLeakCanary()
                   //     .initCrashHandler()
                        .initFragmentation(false)
                        .initSpeech()
                   //     .initDoraemonKit()
                        .initLog()
                        .initXmly()
                        .initGreenDao()
                        .initUM()
                        .initNet()
                        .initRouter()
                        .initXmlyPlayer()
                        .initUtils()
                        .initBugly()
                        .initXmlyDownloader();
        XmPlayerManager.getInstance(this).addPlayerStatusListener(mPlayerStatusListener);
        Log.d(TAG, "onCreate() called " + System.currentTimeMillis());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate() called");
        XmPlayerManager.getInstance(this).removePlayerStatusListener(mPlayerStatusListener);
        XmPlayerManager.release();
        CommonRequest.release();
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onPlayStart() {
            try {
                PlayableModel currSound = XmPlayerManager.getInstance(App.this).getCurrSound();
                if (null == currSound) {
                    return;
                }
                if (currSound.getKind().equals(PlayableModel.KIND_SCHEDULE)) {
                    Schedule schedule = (Schedule) currSound;
                    AppHelper.getDaoSession().insertOrReplace(new PlayHistoryBean(currSound.getDataId(), schedule.getRadioId(), currSound.getKind(),
                            System.currentTimeMillis(), schedule));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlayPause() {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (null != notificationManager) {
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
            try {
                PlayableModel currSound = XmPlayerManager.getInstance(App.this).getCurrSound();
                if (null == currSound) {
                    return;
                }
                if (currSound.getKind().equals(PlayableModel.KIND_TRACK)) {
                    Track track = (Track) currSound;
                    int currPos = XmPlayerManager.getInstance(App.this).getPlayCurrPositon();
                    int duration = XmPlayerManager.getInstance(App.this).getDuration();
                    AppHelper.getDaoSession().insertOrReplace(new PlayHistoryBean(currSound.getDataId(), track.getAlbum().getAlbumId(),
                            currSound.getKind(), 100 * currPos / duration,
                            System.currentTimeMillis(), track));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onError(XmPlayerException e) {
            return false;
        }
    };
}
