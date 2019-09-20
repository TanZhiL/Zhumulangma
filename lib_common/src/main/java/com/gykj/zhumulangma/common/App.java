package com.gykj.zhumulangma.common;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.widget.TRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import static com.gykj.zhumulangma.common.AppConstants.Ximalaya.NOTIFICATION_ID;

/**
 * Description: <初始化应用程序><br>
 * Author:      mxdl<br>
 * Date:        2018/6/6<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class App extends android.app.Application implements IXmPlayerStatusListener {
    private static App mApplication;
    private static final String TAG = "App";

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

    public static App getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called " + System.currentTimeMillis());
        super.onCreate();
        mApplication = this;
        XmPlayerManager.getInstance(this).addPlayerStatusListener(this);
        AppHelper.init(this)
                        .initXmly()
                        .initXmlyDownloader()
                        .initXmlyPlayer()
                        .initGreenDao()
                        .initLog()
                        .initMultiDex()
                        .initRouter()
                        .initNet()
                        .initFragmentation()
                        .initDoraemonKit()
                        .initSpeech()
                        .initUtils();
        Log.d(TAG, "onCreate() called " + System.currentTimeMillis());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate() called");
        XmPlayerManager.getInstance(this).removePlayerStatusListener(this);
        XmPlayerManager.release();
        CommonRequest.release();
    }
    @Override
    public void onPlayStart() {
        try {
            PlayableModel currSound = XmPlayerManager.getInstance(this).getCurrSound();
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
            PlayableModel currSound = XmPlayerManager.getInstance(this).getCurrSound();
            if (null == currSound) {
                return;
            }
            if (currSound.getKind().equals(PlayableModel.KIND_TRACK)) {
                Track track = (Track) currSound;
                int currPos = XmPlayerManager.getInstance(this).getPlayCurrPositon();
                int duration = XmPlayerManager.getInstance(this).getDuration();
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

}
