package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.home.fragment.RadioListFragment;
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
    private SingleLiveEvent<String> mCityNameEvent;
    private SingleLiveEvent<String> mTitleEvent;
    private SingleLiveEvent<Void> mStartLocationEvent;

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
                    RouterUtil.navigateTo(Constants.Router.Home.F_PLAY_RADIIO);
                }, Throwable::printStackTrace);
    }

    public void init() {
        mModel.getSPString(Constants.SP.CITY_CODE, Constants.Default.CITY_CODE)
                .doOnSubscribe(this)
                .doOnNext(this::init)
                .flatMap((Function<String, ObservableSource<String>>) s ->
                        mModel.getSPString(Constants.SP.CITY_NAME, Constants.Default.CITY_NAME))
                .subscribe(cn -> {
                    getCityNameEvent().setValue(cn);
                    getStartLocationEvent().call();
                }, Throwable::printStackTrace);
    }

    /**
     * 保存定位结果
     *
     * @param aMapLocation
     */
    public void saveLocation(AMapLocation aMapLocation) {
        if (!TextUtils.isEmpty(aMapLocation.getAdCode()) && !mCityCode.equals(aMapLocation.getAdCode().substring(0, 4))) {
            String city = aMapLocation.getCity();
            String province = aMapLocation.getProvince();
            mModel.putSP(Constants.SP.CITY_CODE, aMapLocation.getAdCode().substring(0, 4))
                    .doOnSubscribe(this)
                    .flatMap((Function<String, ObservableSource<String>>) aBoolean ->
                            mModel.putSP(Constants.SP.CITY_NAME, city.substring(0, city.length() - 1)))
                    .flatMap((Function<String, ObservableSource<String>>) aBoolean ->
                            mModel.putSP(Constants.SP.PROVINCE_CODE, aMapLocation.getAdCode().substring(0, 3) + "000"))
                    .flatMap((Function<String, ObservableSource<String>>) aBoolean ->
                            mModel.putSP(Constants.SP.PROVINCE_NAME, province.substring(0, province.length() - 1)))
                    .flatMap((Function<String, ObservableSource<String>>) aBoolean ->
                            mModel.getSPString(Constants.SP.CITY_NAME))
                    .flatMap((Function<String, ObservableSource<String>>) s -> {
                        getTitleEvent().setValue(s);
                        return mModel.getSPString(Constants.SP.CITY_CODE);
                    })
                    .subscribe(this::init, Throwable::printStackTrace);
        }
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

    public SingleLiveEvent<String> getCityNameEvent() {
        return mCityNameEvent = createLiveData(mCityNameEvent);
    }

    public SingleLiveEvent<Void> getStartLocationEvent() {
        return mStartLocationEvent = createLiveData(mStartLocationEvent);
    }

    public SingleLiveEvent<String> getTitleEvent() {
        return mTitleEvent = createLiveData(mTitleEvent);
    }

    public void navigateToCity() {
        mModel.getSPString(Constants.SP.CITY_NAME, Constants.Default.CITY_NAME)
                .doOnSubscribe(this)
                .subscribe(s ->
                        RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_RADIO_LIST)
                                .withInt(KeyCode.Home.TYPE, RadioListFragment.LOCAL_CITY)
                                .withString(KeyCode.Home.TITLE, s)));
    }

    public void navigateToProvince() {
        mModel.getSPString(Constants.SP.PROVINCE_NAME, Constants.Default.PROVINCE_NAME)
                .doOnSubscribe(this)
                .subscribe(s ->
                        RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_RADIO_LIST)
                                .withInt(KeyCode.Home.TYPE, RadioListFragment.LOCAL_PROVINCE)
                                .withString(KeyCode.Home.TITLE, s)));
    }
}

