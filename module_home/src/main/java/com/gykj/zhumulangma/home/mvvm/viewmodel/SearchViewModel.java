package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.SearchHistoryBean;
import com.gykj.zhumulangma.common.dao.SearchHistoryBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.home.bean.SearchSuggestItem;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.word.AlbumResult;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
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
    private SingleLiveEvent<List<SearchSuggestItem>> mWordsSingleLiveEvent;

    public SearchViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);

    }

    public void _getHotWords() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, String.valueOf(20));
        mModel.getHotWords(map)
                .subscribe(hotWordList -> mHotWordsSingleLiveEvent.postValue(
                        hotWordList.getHotWordList()), e -> e.printStackTrace());
    }

    public void clearHistory() {

        mModel.clearAll(SearchHistoryBean.class)
                .subscribe(aBoolean -> {

                }, e -> e.printStackTrace());
    }

    public void insertHistory(SearchHistoryBean entity) {
        mModel.list(SearchHistoryBean.class)
                .filter(historyBeanList -> !historyBeanList.contains(entity))
                .flatMap((Function<List<SearchHistoryBean>, ObservableSource<SearchHistoryBean>>)
                        historyBeanList -> mModel.insert(entity))
                .subscribe(bean -> {
                }, e -> e.printStackTrace());
    }

    public void getHistory() {
        mModel.listDesc(SearchHistoryBean.class, 0, 0, SearchHistoryBeanDao.Properties.Datatime)
                .subscribe(searchHistoryBeans -> getHistorySingleLiveEvent().postValue(searchHistoryBeans));
    }

    public void _getSuggestWord(String q) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, q);
        mModel.getSuggestWord(map)
                .map(suggestWords -> {
                    List<SearchSuggestItem> suggestItems=new ArrayList<>();
                    for (AlbumResult album :suggestWords.getAlbumList()) {
                        suggestItems.add(new SearchSuggestItem(album));
                    }
                    for (QueryResult queryResult :suggestWords.getKeyWordList()) {
                        suggestItems.add(new SearchSuggestItem(queryResult));
                    }
                    return suggestItems;
                })
                .subscribe(suggestItems -> getWordsSingleLiveEvent().postValue(suggestItems), e -> e.printStackTrace());
    }

    public SingleLiveEvent<List<HotWord>> getHotWordsSingleLiveEvent() {
        return mHotWordsSingleLiveEvent = createLiveData(mHotWordsSingleLiveEvent);
    }

    public SingleLiveEvent<List<SearchHistoryBean>> getHistorySingleLiveEvent() {
        return mHistorySingleLiveEvent = createLiveData(mHistorySingleLiveEvent);
    }

    public SingleLiveEvent<List<SearchSuggestItem>> getWordsSingleLiveEvent() {
        return mWordsSingleLiveEvent = createLiveData(mWordsSingleLiveEvent);
    }
}
