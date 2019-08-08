package com.gykj.zhumulangma.common.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.BaseModel;
import java.util.List;

/**
 * Description: <BaseRefreshViewModel><br>
 * Author:      mxdl<br>
 * Date:        2019/06/30<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class BaseRefreshViewModel<T,M extends BaseModel> extends BaseViewModel<M> {

    protected UIChangeRefreshLiveData mUIChangeRefreshLiveData;
    public BaseRefreshViewModel(@NonNull Application application, M model) {
        super(application, model);
    }

    public UIChangeRefreshLiveData getUCRefresh() {
        if (mUIChangeRefreshLiveData == null) {
            mUIChangeRefreshLiveData = new UIChangeRefreshLiveData();
        }
        return mUIChangeRefreshLiveData;
    }

    public final class UIChangeRefreshLiveData extends SingleLiveEvent {
        private SingleLiveEvent<Void> mAutoRefresLiveEvent;
        private SingleLiveEvent<Boolean> mStopRefresLiveEvent;
        private SingleLiveEvent<List<T>> mRefresLiveEvent;
        private SingleLiveEvent<List<T>> mLoadMoreLiveEvent;
        private SingleLiveEvent<Boolean> mStopLoadMoreLiveEvent;
        private SingleLiveEvent<Boolean> mNoMoreDataLiveEvent;
        public SingleLiveEvent<Void> getAutoRefresLiveEvent() {
            return mAutoRefresLiveEvent = createLiveData(mAutoRefresLiveEvent);
        }
        public SingleLiveEvent<Boolean> getStopRefresLiveEvent() {
            return mStopRefresLiveEvent = createLiveData(mStopRefresLiveEvent);
        }
        public SingleLiveEvent<List<T>> getRefresLiveEvent() {
            return mRefresLiveEvent = createLiveData(mRefresLiveEvent);
        }
        public SingleLiveEvent<List<T>> getLoadMoreLiveEvent() {
            return mLoadMoreLiveEvent = createLiveData(mLoadMoreLiveEvent);
        }
        public SingleLiveEvent<Boolean> getStopLoadMoreLiveEvent() {
            return mStopLoadMoreLiveEvent = createLiveData(mStopLoadMoreLiveEvent);
        }
        public SingleLiveEvent<Boolean> getNoMoreDataLiveEvent() {
            return mNoMoreDataLiveEvent = createLiveData(mNoMoreDataLiveEvent);
        }
    }
    public void postAutoRefreshEvent(){
        if(mUIChangeRefreshLiveData != null){
            mUIChangeRefreshLiveData.getAutoRefresLiveEvent().call();
        }
    }
    public void postStopRefreshEvent(boolean success){
        if(mUIChangeRefreshLiveData != null){
            mUIChangeRefreshLiveData.getStopRefresLiveEvent().postValue(success);
        }
    }
    public void postRefreshDataEvent(List<T> list){
        if(mUIChangeRefreshLiveData != null){
            mUIChangeRefreshLiveData.getRefresLiveEvent().postValue(list);
        }
    }
    public void postLoadMoreEvent(List<T> list){
        if(mUIChangeRefreshLiveData != null){
            mUIChangeRefreshLiveData.getLoadMoreLiveEvent().postValue(list);
        }
    }
    public void postStopLoadMoreEvent(boolean success){
        if(mUIChangeRefreshLiveData != null){
            mUIChangeRefreshLiveData.getStopLoadMoreLiveEvent().postValue(success);
        }
    }
    public void postNoMoreDataEvent(boolean success){
        if(mUIChangeRefreshLiveData != null){
            mUIChangeRefreshLiveData.getNoMoreDataLiveEvent().postValue(success);
        }
    }
}
