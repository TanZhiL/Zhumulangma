package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.extra.RxField;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.net.dto.BannerDTO;
import com.gykj.zhumulangma.home.bean.TabBean;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CONFIG_BANNER_ID;

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
        //获取banner
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CONFIG_BANNER_ID);
        mModel.getBanners1(map).flatMap((Function<BannerDTO, ObservableSource<String>>) bannerDTO -> {
            String bannerTitle = bannerDTO.getBanners().get(0).getBannerTitle();
            String tabs1 = JsonUtils.getString(bannerTitle, "tabs");
            String[] split = tabs1.split(",");
            return Observable.fromIterable(Arrays.asList(split));
        }).flatMap((Function<String, ObservableSource<BannerDTO>>) s -> {
            Map<String, String> map1 = new HashMap<>();
            map1.put(DTransferConstants.ID, s);
            return mModel.getBanners1(map1);
        }).map(bannerDTO -> {
            String bannerTitle = bannerDTO.getBanners().get(0).getBannerTitle();
            tabs.get().addAll(new Gson().fromJson(bannerTitle, new TypeToken<List<TabBean>>() {
            }.getType()));
            return bannerDTO;
        }).toList()
                .flatMapObservable((Function<List<BannerDTO>, ObservableSource<HotWordList>>) bannerDTOS -> {
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


    public SingleLiveEvent<List<HotWord>> getHotWordsEvent() {
        return mHotWordsEvent = createLiveData(mHotWordsEvent);
    }

    public SingleLiveEvent<List<TabBean>> getTabsEvent() {
        return mTabsEvent = createLiveData(mTabsEvent);
    }

}
