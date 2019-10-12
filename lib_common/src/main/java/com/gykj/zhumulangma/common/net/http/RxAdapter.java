package com.gykj.zhumulangma.common.net.http;

import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.util.ToastUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:Rx适配器
 */
public class RxAdapter {

    /**
     * 线程调度器
     */
    public static ObservableTransformer schedulersTransformer() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 异常内部拦截
     * <br/>HandleException(将内部异常选择性抛出)->
     * <br/>RetryException(拦截指定内部异常,如Token超时等)->
     * <br/>DoOnException(统一处理未被拦截内部异常和所有外部异常)
     */
    public static ObservableTransformer exceptionTransformer() {

        return observable -> observable
                .flatMap(new HandleException())
                .retryWhen(new RetryException())//拦截需要处理的异常
                .onErrorResumeNext(new DoOnException());
    }
    /**
     * 将内部异常选择性抛出
     */
    private static class HandleException implements Function<Object, Observable> {

        @Override
        public Observable apply(Object o) throws Exception {
            if (o instanceof ResponseDTO) {
                ResponseDTO respDTO = (ResponseDTO) o;
                //选择性抛出内部异常
                if (!respDTO.code.equals(ExceptionHandler.APP_ERROR.SUCCESS)) {
                    RespException throwable = new RespException(respDTO.code, respDTO.msg);
                    return Observable.error(throwable);
                }
            }
            return Observable.just(o);
        }
    }
    /**
     * 统一处理未被拦截内部异常和所有外部异常
     *
     */
    private static class DoOnException implements Function<Throwable, Observable> {
        @Override
        public Observable apply(Throwable t) {
            RespException exception = ExceptionHandler.handleException(t);
            ToastUtil.showToast(ToastUtil.LEVEL_E,"网络异常:"+exception.message);
            return Observable.error(exception);
        }
    }
}
