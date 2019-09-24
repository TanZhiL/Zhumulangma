package com.gykj.zhumulangma.common.mvvm.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.BaseModel;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Description: <BaseViewModel><br>
 * Author:      mxdl<br>
 * Date:        2019/06/30<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class BaseViewModel<M extends BaseModel> extends AndroidViewModel implements IBaseViewModel,Consumer<Disposable> {
    protected M mModel;
    protected UIChangeLiveData mUIChangeLiveData;

    public BaseViewModel(@NonNull Application application, M model) {
        super(application);
        this.mModel = model;
    }
    public UIChangeLiveData getUC() {
        if (mUIChangeLiveData == null) {
            mUIChangeLiveData = new UIChangeLiveData();
        }
        return mUIChangeLiveData;
    }

    public final class UIChangeLiveData extends SingleLiveEvent {
        private SingleLiveEvent<Boolean> showInitLoadViewEvent;
        private SingleLiveEvent<String> showLoadingViewEvent;
        private SingleLiveEvent<Boolean> showNoDataViewEvent;
        private SingleLiveEvent<Boolean> showNetWorkErrViewEvent;
        private SingleLiveEvent<ISupportFragment> startFragmentEvent;
        private SingleLiveEvent<Void> finishSelfEvent;
        public SingleLiveEvent<Boolean> getShowInitViewEvent() {
            return showInitLoadViewEvent = createLiveData(showInitLoadViewEvent);
        }

        public SingleLiveEvent<String> getShowLoadingViewEvent() {
            return showLoadingViewEvent = createLiveData(showLoadingViewEvent);
        }

        public SingleLiveEvent<Boolean> getShowNoDataViewEvent() {
            return showNoDataViewEvent = createLiveData(showNoDataViewEvent);
        }

        public SingleLiveEvent<Boolean> getShowNetWorkErrViewEvent() {
            return showNetWorkErrViewEvent = createLiveData(showNetWorkErrViewEvent);
        }

        public SingleLiveEvent<ISupportFragment> getStartFragmentEvent() {
            return startFragmentEvent = createLiveData(startFragmentEvent);
        }


        public SingleLiveEvent<Void> getFinishSelfEvent() {
            return finishSelfEvent = createLiveData(finishSelfEvent);
        }
    }
    protected SingleLiveEvent createLiveData(SingleLiveEvent liveData) {
        if (liveData == null) {
            liveData = new SingleLiveEvent();
        }
        return liveData;
    }
    public static final class ParameterField {
        public static String CLASS = "CLASS";
        public static String CANONICAL_NAME = "CANONICAL_NAME";
        public static String BUNDLE = "BUNDLE";
    }

    public void postShowLoadingViewEvent(String tip) {
        if (mUIChangeLiveData != null) {
            mUIChangeLiveData.showLoadingViewEvent.postValue(tip);
        }
    }

    public void postShowNoDataViewEvent(boolean show) {
        if (mUIChangeLiveData != null) {
            mUIChangeLiveData.showNoDataViewEvent.postValue(show);
        }
    }

    public void postShowInitViewEvent(boolean show) {
        if (mUIChangeLiveData != null) {
            mUIChangeLiveData.showInitLoadViewEvent.postValue(show);
        }
    }

    public void postShowNetWorkErrViewEvent(boolean show) {
        if (mUIChangeLiveData != null) {
            mUIChangeLiveData.showNetWorkErrViewEvent.postValue(show);
        }
    }
/*    public void postStartFragmentEvent(ISupportFragment fragment) {

        mUIChangeLiveData.startFragmentEvent.postValue(fragment);
    }*/




    public void postFinishSelfEvent() {
        mUIChangeLiveData.finishSelfEvent.call();
    }

    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void accept(Disposable disposable) throws Exception {
        if(mModel != null){
            mModel.addSubscribe(disposable);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mModel != null) {
            mModel.onCleared();
        }
    }
}
