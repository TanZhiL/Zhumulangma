package com.gykj.zhumulangma.common.aop;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.thomas.okaspectj.IPointHandler;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;

import org.aspectj.lang.ProceedingJoinPoint;

public class PointHelper implements IPointHandler {
    private static final String TAG = "PointHelper";
    private Context mContext;

    public PointHelper(Context context) {
        mContext = context;
    }

    @Override
    public void onHandlePoint(Class clazz, ProceedingJoinPoint joinPoint) {
        Log.d(TAG, "handlePoint() called with: clazz = [" + clazz + "], joinPoint = [" + joinPoint + "]");
        try {
            if (clazz == NeedLogin.class) {
                if (AccessTokenManager.getInstanse().hasLogin()) {
                    joinPoint.proceed();
                } else {
                    ToastUtil.showToast("请先登陆");
                    LoginHelper.getInstance().login(ActivityUtils.getTopActivity());
                }
            }else {
                joinPoint.proceed();
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
