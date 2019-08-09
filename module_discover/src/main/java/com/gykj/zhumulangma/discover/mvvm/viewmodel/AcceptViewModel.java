package com.gykj.zhumulangma.discover.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.TaskBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.discover.mvvm.model.AcceptModel;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Author: Thomas.
 * Date: 2019/8/6 14:14
 * Email: 1071931588@qq.com
 * Description:
 */
public class AcceptViewModel extends BaseViewModel<AcceptModel> {

    private SingleLiveEvent<TaskBean> mTaskBeanSingleLiveEvent;

    public AcceptViewModel(@NonNull Application application, AcceptModel model) {
        super(application, model);
    }

    public void accept(String type) {
        Observable.defer(() -> mModel.accept(type, null).doOnSubscribe(this))
                .doOnSubscribe(d -> postShowTransLoadingViewEvent("查询中..."))
                .doFinally(() -> postShowTransLoadingViewEvent(null))
                .subscribe(taskBeanResponseDTO -> getTaskBeanSingleLiveEvent().postValue(taskBeanResponseDTO.result)
                        , new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });
    }

    public SingleLiveEvent<TaskBean> getTaskBeanSingleLiveEvent() {
        return mTaskBeanSingleLiveEvent = createLiveData(mTaskBeanSingleLiveEvent);
    }
}
