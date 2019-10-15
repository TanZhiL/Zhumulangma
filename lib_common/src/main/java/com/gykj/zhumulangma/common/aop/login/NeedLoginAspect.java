package com.gykj.zhumulangma.common.aop.login;

import com.gykj.zhumulangma.common.util.ToastUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Author: Thomas.<br/>
 * Date: 2019/10/15 14:37<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:登陆拦截切面
 */
@Aspect
public class NeedLoginAspect {

    @Pointcut("execution(@com.gykj.zhumulangma.common.aop.login.NeedLogin * *(..))")
    public void pointcutNeedLogin() {
    }

    @Around("pointcutNeedLogin()")
    public void aroundNeedLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        if (AccessTokenManager.getInstanse().hasLogin()) {
            //方法执行
            joinPoint.proceed();
        } else {
            ToastUtil.showToast("请先登陆");
        }
    }

}