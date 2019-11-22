package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.home.fragment.RadioListFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 10:21
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class RadioListViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Radio> {

    public static final String COUNTRY = "1";
    public static final String PROVINCE = "2";
    public static final String INTERNET = "3";

    private SingleLiveEvent<List<Radio>> mInitRadiosEvent;
    private int curPage = 1;
    private int mProvinceCode;
    private int mType;

    public RadioListViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        switch (mType) {
            case RadioListFragment.LOCAL_PROVINCE:
                mModel.getSPString(Constants.SP.PROVINCE_CODE, Constants.Default.PROVINCE_CODE)
                        .doOnSubscribe(this)
                        .subscribe(r -> getRadioList(PROVINCE, Integer.valueOf(r)), Throwable::printStackTrace);
                break;
            case RadioListFragment.COUNTRY:
                getRadioList(COUNTRY, -1);
                break;
            case RadioListFragment.PROVINCE:
                getRadioList(PROVINCE, mProvinceCode);
                break;
            case RadioListFragment.INTERNET:
                getRadioList(INTERNET, -1);
                break;
            case RadioListFragment.RANK:
                getRankRadios();
                break;
            case RadioListFragment.LOCAL_CITY:
                mModel.getSPString(Constants.SP.CITY_CODE, Constants.Default.CITY_CODE)
                        .doOnSubscribe(this)
                        .subscribe(this::getLocalCity, Throwable::printStackTrace);
                break;
            default:
                getRadiosByCategory();
                break;
        }
    }

    public void getLocalCity(String cityCode) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CITY_CODE, cityCode);
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadiosByCity(map)
                .subscribe(radioList -> {
                    if (CollectionUtils.isEmpty(radioList.getRadios())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getClearStatusEvent().call();
                    getInitRadiosEvent().setValue(radioList.getRadios());
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    public void getMoreLocalCity(String cityCode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CITY_CODE, cityCode);
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadiosByCity(map)
                .subscribe(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getRadios())) {
                        curPage++;
                    }
                    getFinishLoadmoreEvent().setValue(radioList.getRadios());
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });
    }

    public void getRadioList(String flag, int extras) {
        Map<String, String> map = new HashMap<String, String>();
        //电台类型：1-国家台，2-省市台，3-网络台
        map.put(DTransferConstants.RADIOTYPE, flag);
        if (flag.equals(PROVINCE)) {
            if (extras != mProvinceCode) {
                curPage = 1;
                mProvinceCode = extras;
            }
            map.put(DTransferConstants.PROVINCECODE, String.valueOf(extras));
        }
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadios(map)
                .subscribe(radioList -> {
                    if (CollectionUtils.isEmpty(radioList.getRadios())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getClearStatusEvent().call();
                    getInitRadiosEvent().setValue(radioList.getRadios());
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });

    }

    public void getMoreRadioList(String flag, int extras) {
        Map<String, String> map = new HashMap<String, String>();
        //电台类型：1-国家台，2-省市台，3-网络台
        map.put(DTransferConstants.RADIOTYPE, flag);
        if (flag.equals(PROVINCE)) {
            if (extras != mProvinceCode) {
                curPage = 1;
                mProvinceCode = extras;
            }
            map.put(DTransferConstants.PROVINCECODE, String.valueOf(extras));
        }
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadios(map)
                .subscribe(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getRadios())) {
                        curPage++;
                    }
                    getFinishLoadmoreEvent().setValue(radioList.getRadios());
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });

    }


    public void getRadiosByCategory() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIO_CATEGORY_ID, String.valueOf(mType));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadiosByCategory(map)
                .subscribe(radioList -> {
                    if (CollectionUtils.isEmpty(radioList.getRadios())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getClearStatusEvent().call();
                    getInitRadiosEvent().setValue(radioList.getRadios());
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    public void getMoreRadiosByCategory() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_CATEGORY_ID, String.valueOf(mType));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadiosByCategory(map)
                .subscribe(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getRadios())) {
                        curPage++;
                    }
                    getFinishLoadmoreEvent().setValue(radioList.getRadios());
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });
    }

    public void getRankRadios() {
        Map<String, String> map = new HashMap<>();
        //获取前100名
        map.put(DTransferConstants.RADIO_COUNT, "100");
        mModel.getRankRadios(map)
                .subscribe(radioList -> {
                    if (CollectionUtils.isEmpty(radioList.getRadios())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    getClearStatusEvent().call();
                    getInitRadiosEvent().setValue(radioList.getRadios());
                    super.onViewLoadmore();
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    @Override
    public void onViewLoadmore() {
        switch (mType) {
            case RadioListFragment.LOCAL_PROVINCE:
                mModel.getSPString(Constants.SP.PROVINCE_CODE, Constants.Default.PROVINCE_CODE)
                        .doOnSubscribe(this)
                        .subscribe(r -> getMoreRadioList(PROVINCE, Integer.valueOf(r)), Throwable::printStackTrace);
                break;
            case RadioListFragment.COUNTRY:
                getMoreRadioList(RadioListViewModel.COUNTRY, -1);
                break;
            case RadioListFragment.PROVINCE:
                getMoreRadioList(RadioListViewModel.PROVINCE, mProvinceCode);
                break;
            case RadioListFragment.INTERNET:
                getMoreRadioList(RadioListViewModel.INTERNET, -1);
                break;
            case RadioListFragment.LOCAL_CITY:
                mModel.getSPString(Constants.SP.CITY_CODE, Constants.Default.CITY_CODE)
                        .doOnSubscribe(this)
                        .subscribe(this::getMoreLocalCity, Throwable::printStackTrace);
                break;
            default:
                getMoreRadiosByCategory();
                break;
        }
    }


    public void playRadio(Radio radio) {
        mModel.getSchedulesSource(radio)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(schedules ->
                {
                    XmPlayerManager.getInstance(getApplication()).playSchedule(schedules, -1);
                    RouterUtil.navigateTo(Constants.Router.Home.F_PLAY_RADIIO);
                }, Throwable::printStackTrace);
    }

    public SingleLiveEvent<List<Radio>> getInitRadiosEvent() {
        return mInitRadiosEvent = createLiveData(mInitRadiosEvent);
    }

    public void setType(int type) {
        mType = type;
    }

    public void setProvinceCode(int provinceCode) {
        if (provinceCode != mProvinceCode) {
            curPage = 1;
        }
        mProvinceCode = provinceCode;
    }

}
