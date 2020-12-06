package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.extra.RxField;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.home.bean.ConfigBean;
import com.gykj.zhumulangma.home.bean.Dictionary;
import com.gykj.zhumulangma.home.bean.TabBean;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/13 11:10
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class MainHomeViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<HotWord>> mHotWordsEvent;
    private SingleLiveEvent<List<TabBean>> mTabsEvent;

    public MainHomeViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);

    }

    public void init() {
        RxField<List<TabBean>> tabs = new RxField<>(new ArrayList<>());
        getTabsObservable()
                .flatMap((Function<List<TabBean>, ObservableSource<HotWordList>>) bannerDTOS -> {
                    tabs.get().addAll(bannerDTOS);
                    Map<String, String> map12 = new HashMap<String, String>();
                    map12.put(DTransferConstants.TOP, String.valueOf(20));
                    return mModel.getHotWords(map12);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairs -> {
                    getHotWordsEvent().setValue(pairs.getHotWordList());
                    getTabsEvent().setValue(tabs.get());
                    getClearStatusEvent().call();
                }, e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    public void getHotWords() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, String.valueOf(20));
        mModel.getHotWords(map)
                .subscribe(hotWordList -> getHotWordsEvent().setValue(hotWordList.getHotWordList()), e -> {
                    e.printStackTrace();
                });
    }

    private Observable<List<TabBean>> getTabsObservable() {
        BmobQuery<Dictionary> query = new BmobQuery<>();
        query.addWhereEqualTo("name", "config");
        return query.findObjectsObservable(Dictionary.class)
                .map(strings -> {
                    String value = strings.get(0).getValue();
                    ConfigBean configBean = new Gson().fromJson(value, ConfigBean.class);
                    return configBean.getTabs();
                });
    }

    public SingleLiveEvent<List<HotWord>> getHotWordsEvent() {
        return mHotWordsEvent = createLiveData(mHotWordsEvent);
    }

    public SingleLiveEvent<List<TabBean>> getTabsEvent() {
        return mTabsEvent = createLiveData(mTabsEvent);
    }

}
