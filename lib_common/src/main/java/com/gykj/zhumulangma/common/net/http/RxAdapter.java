package com.gykj.zhumulangma.common.net.http;

import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Description: <Rx适配器><br>
 * Author:      mxdl<br>
 * Date:        2019/3/18<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class RxAdapter {
    /**
     * 生命周期绑定
     *
     * @param lifecycle Activity
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LifecycleProvider lifecycle) {
        if (lifecycle != null) {
            return lifecycle.bindUntilEvent(ActivityEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("context not the LifecycleProvider type");
        }
    }

    /**
     * 线程调度器
     */
/*    public static SingleTransformer singleSchedulersTransformer() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static SingleTransformer singleExceptionTransformer() {

        return observable -> observable
                .flatMap(new HandleFuc())
                .retryWhen(new RetryWithNewToken())//拦截需要处理的异常
                .onErrorResumeNext(new HttpResponseFunc());
    }*/

    /**
     * 线程调度器
     */
    public static ObservableTransformer schedulersTransformer() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static ObservableTransformer exceptionTransformer() {

        return observable -> observable
                .flatMap(new HandleFuc())
//                .retryWhen(new RetryWithNewToken())//拦截需要处理的异常
                .onErrorResumeNext(new HttpResponseFunc());
    }

    /**
     * 拦截未被处理的异常
     *
     * @param <T>
     */
    private static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable t) {
            ResponseThrowable exception = ExceptionHandler.handleException(t);
            ToastUtil.showToast(ToastUtil.LEVEL_E,"网络异常:"+exception.code);
            return Observable.error(exception);
        }
    }

    private static class HandleFuc implements Function<Object, Observable> {

        @Override
        public Observable apply(Object o) throws Exception {
            if (o instanceof ResponseDTO) {
                ResponseDTO respDTO = (ResponseDTO) o;
                if (!respDTO.code.equals(ExceptionHandler.APP_ERROR.SUCCESS)) {
                    ResponseThrowable throwable = new ResponseThrowable(respDTO.code, respDTO.msg);
                    return Observable.error(throwable);
                }
            }
            return Observable.just(o);
        }
    }

}
