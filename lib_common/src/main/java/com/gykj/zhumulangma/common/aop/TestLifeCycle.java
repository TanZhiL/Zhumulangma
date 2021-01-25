package com.gykj.zhumulangma.common.aop;

import com.thomas.aspectj.OkAspectj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Author: Thomas.<br/>
 * Date: 2019/12/18 13:24<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */

@OkAspectj("execution(* android.app.Activity.on**(..))")
@Target(ElementType.METHOD)
public @interface TestLifeCycle {
}
