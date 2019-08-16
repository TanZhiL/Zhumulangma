package com.gykj.zhumulangma.common.aop;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.AppConstants;
import com.ximalaya.ting.android.opensdk.auth.utils.AccessTokenKeeper;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;

import org.jetbrains.annotations.Nls;

/**
 * Author: Thomas.
 * Date: 2019/8/16 10:15
 * Email: 1071931588@qq.com
 * Description:
 */
@Interceptor(name = "login", priority = 6)
public class LoginInterceptorImpl implements IInterceptor {
    private static final String TAG = "LoginInterceptorImpl";

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        String path = postcard.getPath();
        Log.e(TAG, "process: "+AccessTokenKeeper.readAccessToken(App.getInstance()));
        Log.e(TAG, "path: "+path);
        if (TextUtils.isEmpty(AccessTokenKeeper.readAccessToken(App.getInstance()).getToken())) {
            switch (path) {
                case AppConstants.Router.User.F_MESSAGE:
                    callback.onInterrupt(new RuntimeException("用户尚未登录"));
                    ARouter.getInstance().build(AppConstants.Router.Common.A_LOGIN).navigation();
                    break;
                default:
                    callback.onContinue(postcard);
                    break;
            }
        }
        callback.onContinue(postcard);
    }

    @Override
    public void init(Context context) {
        Log.i(TAG, "登录拦截器初始化完成");
    }
}
