package com.gykj.zhumulangma.common.net.exception;


import android.util.Log;

import com.google.gson.Gson;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.UserBean;
import com.gykj.zhumulangma.common.db.DBManager;
import com.gykj.zhumulangma.common.net.NetManager;
import com.gykj.zhumulangma.common.net.RxAdapter;
import com.gykj.zhumulangma.common.net.dto.LoginDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.util.log.TLog;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/1 8:01
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:所有异常都会经过此处,可拦截需要重试的内部异常,如Token超时等
 */
public class ExceptionRetry implements Function<Observable<Throwable>, Observable<?>> {
    private DBManager mDBManager=DBManager.getInstance(App.getInstance());
    @Override
    public Observable<?> apply(Observable<Throwable> observable) throws Exception {

        return observable.compose(upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()))
                .flatMap((Function<Throwable, Observable<?>>) throwable -> {
                    TLog.d(Log.getStackTraceString(throwable));
                    return Observable.error(throwable);
                  /*
                    //拦截内部异常
                    if (throwable instanceof InterceptableException) {
                        InterceptableException ex = (InterceptableException) throwable;
                        switch (ex.code) {
                            case InterceptableException.TOKEN_OUTTIME:
                                return reLogin();
                            default:
                                return Observable.error(throwable);
                        }
                    } else {
                        //外部异常直接放过
                        return Observable.error(throwable);
                    }*/
                });
    }

    /**
     * 重新登陆
     *
     * @return
     */
    private Observable reLogin() {
        return mDBManager.getSPString(Constants.SP.USER)
                .map(s -> {
                    final UserBean userBean = new Gson().fromJson(s, UserBean.class);
                    LoginDTO loginDTO = new LoginDTO();
                    loginDTO.setCode(userBean.getCode());
                    loginDTO.setDescer_name(userBean.getDescer_name());
                    loginDTO.setDescer_phone(userBean.getDescer_phone());
                    loginDTO.setGraer_name(userBean.getGraer_name());
                    loginDTO.setGraer_phone(userBean.getGraer_phone());
                    return loginDTO;
                }).flatMap((Function<LoginDTO, ObservableSource<ResponseDTO<UserBean>>>) loginDTO ->
                        NetManager.getInstance().getUserService().login(loginDTO))
                .flatMap((Function<ResponseDTO<UserBean>, Observable<?>>) responseDTO -> {
                    if (!responseDTO.code.equals(ExceptionConverter.APP_ERROR.SUCCESS)) {
                        return Observable.error(new CustException(responseDTO.code,
                                responseDTO.msg));
                    } else {
                        return  mDBManager.putSP(Constants.SP.TOKEN, responseDTO.result.getToken())
                                .flatMap((Function<Boolean, ObservableSource<?>>) aBoolean ->
                                        mDBManager.putSP(Constants.SP.USER, new Gson().toJson(responseDTO.result)));
                    }
                })
                .compose(RxAdapter.schedulersTransformer())
                .compose(RxAdapter.exceptionTransformer());
    }
}
