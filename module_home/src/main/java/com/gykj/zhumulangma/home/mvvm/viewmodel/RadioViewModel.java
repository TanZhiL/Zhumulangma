package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.home.mvvm.model.RadioModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class RadioViewModel extends BaseRefreshViewModel<RadioModel, Album> {

    private SingleLiveEvent<List<PlayHistoryBean>> mHistorysEvent;
    private SingleLiveEvent<List<Radio>> mLocalsEvent;
    private SingleLiveEvent<List<Radio>> mTopsEvent;

    private String mCityCode;

    public RadioViewModel(@NonNull Application application, RadioModel model) {
        super(application, model);
    }

    @Override
    public void onViewRefresh() {
        init(mCityCode);
    }

    public void getHistory() {
        mModel.getHistory(1, 5)
                .subscribe(historyBeans -> getHistorysEvent().setValue(historyBeans), Throwable::printStackTrace);
    }

    public void init(String cityCode) {
        mCityCode = cityCode;
        mModel.getHistory(1, 5)
                .doOnNext(historyBeans -> getHistorysEvent().setValue(historyBeans))
                .flatMap((Function<List<PlayHistoryBean>, ObservableSource<RadioList>>) historyBeans -> getLocalListObservable(mCityCode))
                .flatMap((Function<RadioList, ObservableSource<RadioList>>) radioList -> getTopListObservable())
                .doFinally(() -> super.onViewRefresh())
                .subscribe(r -> getClearStatusEvent().call(), e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private Observable<RadioList> getLocalListObservable(String cityCode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CITY_CODE, cityCode);
        map.put(DTransferConstants.PAGE_SIZE, "5");
        map.put(DTransferConstants.PAGE, String.valueOf(1));
        return mModel.getRadiosByCity(map)
                .doOnNext(radioList -> {
                    getLocalsEvent().setValue(radioList.getRadios());
                });
    }


    public void getTopList() {
        getTopListObservable().subscribe(r -> {
        }, Throwable::printStackTrace);
    }

    private Observable<RadioList> getTopListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_COUNT, "5");

        return mModel.getRankRadios(map)
                .doOnNext(radioList -> getTopsEvent().setValue(radioList.getRadios()));
    }

    public void playRadio(String radioId) {
        playRadio(mModel.getSchedulesSource(radioId));

    }

    public void playRadio(Radio radio) {
        playRadio(mModel.getSchedulesSource(radio));
    }

    private void playRadio(Observable<List<Schedule>> observable) {
        observable.doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(schedules ->
                {
                    XmPlayerManager.getInstance(getApplication()).playSchedule(schedules, -1);
                    RouteUtil.navigateTo(Constants.Router.Home.F_PLAY_RADIIO);
                }, Throwable::printStackTrace);
    }

    public SingleLiveEvent<List<PlayHistoryBean>> getHistorysEvent() {
        return mHistorysEvent = createLiveData(mHistorysEvent);
    }

    public SingleLiveEvent<List<Radio>> getLocalsEvent() {
        return mLocalsEvent = createLiveData(mLocalsEvent);
    }

    public SingleLiveEvent<List<Radio>> getTopsEvent() {
        return mTopsEvent = createLiveData(mTopsEvent);
    }

}
