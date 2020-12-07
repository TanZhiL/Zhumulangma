package com.gykj.zhumulangma.common;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;

import com.gykj.thomas.third.ThirdHelper;
import com.gykj.zhumulangma.common.aop.PointHelper;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.db.DBManager;
import com.gykj.zhumulangma.common.net.RxAdapter;
import com.gykj.zhumulangma.common.widget.TRefreshHeader;
import com.noober.background.BackgroundLibrary;
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
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

import io.reactivex.internal.functions.Functions;


/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:App
 */
public class App extends MultiDexApplication {
    private static App instance;

    public static App getInstance() {
        return instance;
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
            ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多数据了,试试搜索功能吧";
            classicsFooter.setTextSizeTitle(12);
            classicsFooter.setDrawableSize(16);
            classicsFooter.setFinishDuration(0);
            return classicsFooter;
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (BaseUtil.isMainProcess(this)) {
            Beta.installTinker();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (BaseUtil.isMainProcess(this)) {
            ThirdHelper.getInstance(this)
//                    .initLeakCanary()
                    .initBmob()
                    .initSpeech()
                    .initAgentWebX5()
                    .initAspectj(new PointHelper(this))
                    .initUtils()
                    .initCrashView()
                    .initBugly(false);
            AppHelper.getInstance(this)
                    .initLog()
                    .initXmly()
                    .initNet()
                    .initXmlyDownloader();
            XmPlayerManager.getInstance(this).addPlayerStatusListener(playerStatusListener);
            registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
            AppHelper.getInstance(this)
                    .initXmlyPlayer();
        }
        ThirdHelper.getInstance(this)
                .initRouter()
                .initBugly(false)
                .initCrashView()
                .initUM();
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
                    DBManager.getInstance().insert(new PlayHistoryBean(currSound.getDataId(),
                            schedule.getRadioId(), currSound.getKind(), System.currentTimeMillis(), schedule))
                            .compose(RxAdapter.exceptionTransformer())
                            .compose(RxAdapter.schedulersTransformer())
                            .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlayPause() {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.cancel(Constants.Third.XIMALAYA_NOTIFICATION);
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
                    DBManager.getInstance().insert(new PlayHistoryBean(currSound.getDataId(),
                            track.getAlbum().getAlbumId(), currSound.getKind(), 100 * currPos / duration,
                            System.currentTimeMillis(), track))
                            .compose(RxAdapter.exceptionTransformer())
                            .compose(RxAdapter.schedulersTransformer())
                            .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
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

    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            BackgroundLibrary.inject(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };
}
