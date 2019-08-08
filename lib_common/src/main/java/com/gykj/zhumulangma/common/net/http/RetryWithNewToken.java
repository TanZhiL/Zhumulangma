package com.gykj.zhumulangma.common.net.http;


import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.UserBean;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.dto.LoginDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.util.log.TLog;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * Date: 2019/8/1 8:01
 * Email: 1071931588@qq.com
 * Description:拦截需要处理的异常如：token超时自动刷新
 */
public class RetryWithNewToken implements Function<Observable<Throwable>, Observable<?>> {

    @Override
    public Observable<?> apply(Observable<Throwable> observable) throws Exception {

        return observable.compose(upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()))
                .flatMap((Function<Throwable, Observable<?>>) throwable -> {
                    TLog.d(Log.getStackTraceString(throwable));
                    //拦截内部异常
                    if (throwable instanceof ResponseThrowable) {
                        ResponseThrowable ex = (ResponseThrowable) throwable;
                        if (ex.code.equals(ExceptionHandler.APP_ERROR.TOKEN_OUTTIME)) {
                            final UserBean userBean = new Gson().fromJson(SPUtils.getInstance()
                                    .getString(AppConstants.SP.USER), UserBean.class);
                            if (null != userBean) {
                                LoginDTO loginDTO = new LoginDTO();
                                loginDTO.setCode(userBean.getCode());
                                loginDTO.setDescer_name(userBean.getDescer_name());
                                loginDTO.setDescer_phone(userBean.getDescer_phone());
                                loginDTO.setGraer_name(userBean.getGraer_name());
                                loginDTO.setGraer_phone(userBean.getGraer_phone());
                                return getFlowable(loginDTO);
                            } else {
                                return Observable.error(new ResponseThrowable(ExceptionHandler.APP_ERROR.ACCOUNT_ERROR,
                                        "账户异常,请先登陆"));
                            }
                        } else {
                            return Observable.error(throwable);
                        }
                    } else {
                        return Observable.error(throwable);
                    }

                });
    }

    private Observable<Object> getFlowable(LoginDTO loginDTO) {
        return RetrofitManager.getInstance().getCommonService().login(loginDTO)
                .flatMap((Function<ResponseDTO<UserBean>, Observable<?>>) userBeanResponseDTO -> {
                    if (!userBeanResponseDTO.code.equals(ExceptionHandler.APP_ERROR.SUCCESS)) {
                        return Observable.error(new ResponseThrowable(ExceptionHandler.APP_ERROR.ACCOUNT_ERROR,
                                "账户异常,请先登陆"));
                    } else {
                        SPUtils.getInstance().put(AppConstants.SP.TOKEN, userBeanResponseDTO.result.getToken());
                        SPUtils.getInstance().put(AppConstants.SP.USER, new Gson().toJson(userBeanResponseDTO.result));
                        return Observable.just(0);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
