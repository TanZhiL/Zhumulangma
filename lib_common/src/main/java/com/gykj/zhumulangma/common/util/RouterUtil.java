package com.gykj.zhumulangma.common.util;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.aop.LoginHelper;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.mvvm.view.SupportActivity;
import com.gykj.zhumulangma.common.mvvm.view.SupportFragment;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.<br/>
 * Date: 2019/10/24 15:25<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:页面跳转
 */
public class RouterUtil {
    private static final String TAG = "RouterUtil";
    public static void navigateTo(String path) {
        navigateTo(ARouter.getInstance().build(path));
    }

    public static void navigateTo(Postcard postcard) {
        navigateTo(postcard, ISupportFragment.SINGLETASK);
    }

    public static void navigateTo(String path, int launchMode) {
        navigateTo(ARouter.getInstance().build(path), launchMode);
    }

    public static void navigateTo(Postcard postcard, int launchMode) {
        navigateTo(postcard, launchMode, null);
    }

    public static void navigateTo(String path, ExtraTransaction extraTransaction) {
        navigateTo(ARouter.getInstance().build(path), extraTransaction);
    }

    public static void navigateTo(Postcard postcard, ExtraTransaction extraTransaction) {
        navigateTo(postcard, ISupportFragment.SINGLETASK, extraTransaction);
    }

    public static void navigateTo(String path, int launchMode, ExtraTransaction extraTransaction) {
        navigateTo(ARouter.getInstance().build(path), launchMode, extraTransaction);
    }

    public static void navigateTo(Postcard postcard, int launchMode, ExtraTransaction extraTransaction) {
        Object navigation = postcard.navigation();
        NavigateBean navigateBean = new NavigateBean(postcard.getPath(), (SupportFragment) navigation);
        navigateBean.launchMode = launchMode;
        navigateBean.extraTransaction = extraTransaction;
        if (null != navigation) {
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE, navigateBean));
        }
    }

    /**
     * 分发路由
     *
     * @param activity
     * @param navigateBean
     */
    public static void dispatcher(SupportActivity activity, NavigateBean navigateBean) {
        Objects.requireNonNull(navigateBean);
        Objects.requireNonNull(navigateBean.fragment);
        switch (navigateBean.path) {
            case Constants.Router.User.F_MESSAGE:
                //登录拦截
                if (!AccessTokenManager.getInstanse().hasLogin()) {
                    LoginHelper.getInstance().login(activity);
                } else {
                    activity.start(navigateBean.fragment);
                }
                break;
            case Constants.Router.Home.F_PLAY_TRACK:
            case Constants.Router.Home.F_PLAY_RADIIO:
                activity.extraTransaction().setCustomAnimations(
                        com.gykj.zhumulangma.common.R.anim.push_bottom_in,
                        com.gykj.zhumulangma.common.R.anim.no_anim,
                        com.gykj.zhumulangma.common.R.anim.no_anim,
                        com.gykj.zhumulangma.common.R.anim.push_bottom_out).start(
                        navigateBean.fragment, ISupportFragment.SINGLETASK);
                break;
            default:
                if (navigateBean.extraTransaction != null) {
                    navigateBean.extraTransaction.start(navigateBean.fragment, navigateBean.launchMode);
                } else {
                    activity.start(navigateBean.fragment, navigateBean.launchMode);
                }
                break;
        }
    }
}
