package com.gykj.thomas.third;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.view.Gravity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.Utils;
import com.gykj.thomas.aspectj.OkAspectjHelper;
import com.gykj.thomas.aspectj.PointHandler;
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

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import cn.bmob.v3.Bmob;
import me.yokeyword.fragmentation.Fragmentation;

public class ThirdHelper {

    private static final String TAG = "ThirdHelper";

    private static Application mApplication;
    private static volatile ThirdHelper instance;
    public static RefWatcher refWatcher;


    private ThirdHelper() {
    }

    public static ThirdHelper getInstance(Application application) {
        if (instance == null) {
            synchronized (ThirdHelper.class) {
                if (instance == null) {
                    mApplication = application;
                    instance = new ThirdHelper();
                }
            }
        }
        return instance;
    }

    public ThirdHelper initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(mApplication)) {
            return this;
        }
        refWatcher = LeakCanary.install(mApplication);
        return this;
    }

    public ThirdHelper initAgentWebX5() {
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
    
    public ThirdHelper initUM() {
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(mApplication, UMConfigure.DEVICE_TYPE_PHONE, "");
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setSinaWeibo(Constants.SINA_ID, Constants.SINA_KEY, "http://sns.whalecloud.com");
        PlatformConfig.setQQZone(Constants.QQ_ID, Constants.QQ_KEY);
        return this;
    }


    
    public ThirdHelper initBugly() {
            Beta.largeIconId = R.drawable.third_launcher_ting;
            Beta.smallIconId = R.drawable.third_launcher_ting;
            Beta.upgradeDialogLayoutId = R.layout.third_dialog_update;

            Beta.canNotifyUserRestart = true;
            //生产环境
            // Bugly.init(mApplication, Constants.Bugly.SPEECH_ID,, false);
            //开发设备
            Bugly.setIsDevelopmentDevice(mApplication, true);
            Bugly.init(mApplication, Constants.BUGLY_ID, true);
        return this;
    }

    public ThirdHelper initRouter() {

        if (BuildConfig.DEBUG) {
            // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(mApplication); // 尽可能早，推荐在Application中初始化
        return this;
    }

    public ThirdHelper initUtils() {
        Utils.init(mApplication);
        ToastUtils.init(mApplication);
        ToastUtils.setView(R.layout.third_layout_toast);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        KeyboardUtils.clickBlankArea2HideSoftInput();
        return this;
    }

    @SuppressLint("RestrictedApi")
    public ThirdHelper initCrashView() {
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


    public ThirdHelper initFragmentation(boolean isDebug) {

        // 建议在Application里初始化
        Fragmentation.builder()
                // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(isDebug)
                .install();
        return this;
    }
    public ThirdHelper initAspectj(PointHandler handler) {
        OkAspectjHelper.setmHandler(handler);
        return this;
    }

    public ThirdHelper initBmob() {
        Bmob.initialize(mApplication, Constants.BMOB_ID);
        return this;
    }
    public ThirdHelper initSpeech() {
        SpeechUtility.createUtility(mApplication, SpeechConstant.APPID + "=" + Constants.SPEECH_ID);
        return this;
    }

}
