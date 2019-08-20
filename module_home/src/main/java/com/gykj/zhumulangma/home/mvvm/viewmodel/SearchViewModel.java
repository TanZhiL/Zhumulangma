package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.SearchHistoryBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * Date: 2019/8/13 11:10
 * Email: 1071931588@qq.com
 * Description:
 */
public class SearchViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<HotWord>> mHotWordsSingleLiveEvent;
    private SingleLiveEvent<List<SearchHistoryBean>> mHistorySingleLiveEvent;
    public SearchViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);

    }
    public void _getHotWords(){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, String.valueOf(20));
        mModel.getHotWords(map)
                .subscribe(hotWordList -> mHotWordsSingleLiveEvent.postValue(
                        hotWordList.getHotWordList()), e->e.printStackTrace());
    }
    public void clearHistory(){

        mModel.clear(SearchHistoryBean.class)
                .subscribe(aBoolean -> {

                }, e->e.printStackTrace());
    }
    public void insertHistory(SearchHistoryBean entity){
        mModel.list(SearchHistoryBean.class)
                .filter(historyBeanList -> !historyBeanList.contains(entity))
                .flatMap((Function<List<SearchHistoryBean>, ObservableSource<SearchHistoryBean>>)
                        historyBeanList -> mModel.insert(entity))
                .subscribe(bean -> {}, e->e.printStackTrace());
    }
    public void getHistory(){
        mModel.list(SearchHistoryBean.class)
                .subscribe(searchHistoryBeans -> getHistorySingleLiveEvent().postValue(searchHistoryBeans));
    }

    public SingleLiveEvent<List<HotWord>> getHotWordsSingleLiveEvent() {
        return mHotWordsSingleLiveEvent=createLiveData(mHotWordsSingleLiveEvent);
    }

    public SingleLiveEvent<List<SearchHistoryBean>> getHistorySingleLiveEvent() {
        return mHistorySingleLiveEvent=createLiveData(mHistorySingleLiveEvent);
    }
}
