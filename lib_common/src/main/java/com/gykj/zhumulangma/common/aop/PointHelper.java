package com.gykj.zhumulangma.common.aop;

import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.gykj.thomas.aspectj.PointHandler;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;

import org.aspectj.lang.ProceedingJoinPoint;

public class PointHelper implements PointHandler {
    private Context mContext;

    public PointHelper(Context context) {
        mContext = context;
    }

    @Override
    public void handlePoint(Class clazz, ProceedingJoinPoint joinPoint) {
        try {
        if(clazz==NeedLogin.class){
            if (AccessTokenManager.getInstanse().hasLogin()) {
                    joinPoint.proceed();
            } else {
                ToastUtil.showToast("请先登陆");
                LoginHelper.getInstance().login(ActivityUtils.getTopActivity());
            }
        }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
