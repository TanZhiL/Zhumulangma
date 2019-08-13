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

import me.yokeyword.fragmentation.Fragmentation;

/**
 * Description: <初始化应用程序><br>
 * Author:      mxdl<br>
 * Date:        2018/6/6<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class Application extends android.app.Application {
    private static Application mApplication;
    //static 代码段可以防止内存泄露
    static {

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            ClassicsHeader classicsHeader=new ClassicsHeader(context);
            classicsHeader.setTextSizeTitle(14);
            classicsHeader.setTextSizeTime(10);
            classicsHeader.setDrawableSize(18);
            return classicsHeader;
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            ClassicsFooter classicsFooter=new ClassicsFooter(context);
            classicsFooter.setTextSizeTitle(14);
            classicsFooter.setDrawableSize(18);
            return classicsFooter;
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initGreenDao();
        MultiDex.install(this);

        ConstantsOpenSdk.isDebug = true;
        CommonRequest.getInstanse().init(this,AppConstants.Ximalaya.SECRET);

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
    public static Application getInstance(){
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
}
