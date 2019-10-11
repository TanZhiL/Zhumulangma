package com.gykj.zhumulangma.common;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.widget.TRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
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

    public static App getInstance() {
        return mApplication;
    }

    static {
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
          layout.setHeaderMaxDragRate(1.5f);
        });
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
        AppHelper.getInstance(this)
                .initLeakCanary()
                //     .initCrashHandler()
                .initFragmentation(false)
                .initSpeech()
                //     .initDoraemonKit()
                .initLog()
                .initAgentWebX5()
                .initXmly()
                .initGreenDao()
                .initUM()
                .initNet()
                .initRouter()
                .initXmlyPlayer()
                .initUtils()
                .initBugly()
                .initXmlyDownloader();
        XmPlayerManager.getInstance(this).addPlayerStatusListener(playerStatusListener);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        XmPlayerManager.getInstance(this).removePlayerStatusListener(playerStatusListener);
        XmPlayerManager.release();
        CommonRequest.release();
    }

    private IXmPlayerStatusListener playerStatusListener = new IXmPlayerStatusListener() {

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
