package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.home.fragment.RadioListFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/8/14 10:21
 * Email: 1071931588@qq.com
 * Description:
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
                getRadioList(PROVINCE, Integer.parseInt(SPUtils.getInstance().getString(
                        AppConstants.SP.PROVINCE_CODE, AppConstants.Default.PROVINCE_CODE)));
                break;
            case RadioListFragment.COUNTRY:
                getRadioList(COUNTRY, -1);
                break;
            case RadioListFragment.PROVINCE:
               getRadioList(PROVINCE,mProvinceCode);
                break;
            case RadioListFragment.INTERNET:
                getRadioList(INTERNET, -1);
                break;
            case RadioListFragment.RANK:
               getRankRadios();
                break;
            case RadioListFragment.LOCAL_CITY:
               getLocalCity(SPUtils.getInstance().getString(AppConstants.SP.CITY_CODE, AppConstants.Default.CITY_CODE));
                break;
            default:
               getRadiosByCategory();
                break;
        }
    }

    public void getLocalCity(String cityCode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CITY_CODE, cityCode);
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadiosByCity(map)
                .doOnSubscribe(d->getShowInitViewEvent().call())
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
            if (extras!=mProvinceCode) {
                curPage = 1;
                mProvinceCode = extras;
            }
            map.put(DTransferConstants.PROVINCECODE, String.valueOf(extras));
        }
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadios(map)
                .doOnSubscribe(d->getShowInitViewEvent().call())
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
            if (extras!=mProvinceCode) {
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
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_CATEGORY_ID, String.valueOf(mType));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadiosByCategory(map)
                .doOnSubscribe(d->getShowInitViewEvent().call())
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
        Map<String, String> map = new HashMap<String, String>();
        //获取前100名
        map.put(DTransferConstants.RADIO_COUNT, "100");
        mModel.getRankRadios(map)
                .doOnSubscribe(d->getShowInitViewEvent().call())
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
                getMoreRadioList(PROVINCE, Integer.parseInt(SPUtils.getInstance().getString(
                        AppConstants.SP.PROVINCE_CODE, AppConstants.Default.PROVINCE_CODE)));
                break;
            case RadioListFragment.COUNTRY:
                getMoreRadioList(RadioListViewModel.COUNTRY, -1);
                break;
            case RadioListFragment.PROVINCE:
                getMoreRadioList(RadioListViewModel.PROVINCE,mProvinceCode);
                break;
            case RadioListFragment.INTERNET:
                getMoreRadioList(RadioListViewModel.INTERNET, -1);
                break;
            case RadioListFragment.LOCAL_CITY:
                getMoreLocalCity(SPUtils.getInstance().getString(AppConstants.SP.CITY_CODE, AppConstants.Default.CITY_CODE));
                break;
            default:
                getMoreRadiosByCategory();
                break;
        }
    }

    private Radio radio;

    public void play(String radioId) {
        List<Schedule> schedulesx = new ArrayList<>();
        Map<String, String> yestoday = new HashMap();
        yestoday.put("radio_id", radioId);
        Calendar calendar0 = Calendar.getInstance();
        calendar0.add(Calendar.DAY_OF_MONTH, -1);
        yestoday.put("weekday", calendar0.get(Calendar.DAY_OF_WEEK) - 1 + "");

        Map<String, String> today = new HashMap();
        today.put("radio_id", radioId);

        Map<String, String> tomorrow = new HashMap();
        tomorrow.put("radio_id", radioId);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow.put("weekday", calendar0.get(Calendar.DAY_OF_WEEK) - 1 + "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy:MM:dd");

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIO_IDS, radioId);
        mModel.getRadiosByIds(map)
                .doOnNext(radioListById -> radio = radioListById.getRadios().get(0))
                .flatMap((Function<RadioListById, ObservableSource<List<Schedule>>>) radioListById ->
                        mModel.getSchedules(yestoday))
                .doOnNext(schedules -> {
                    Iterator var7 = schedules.iterator();
                    while (var7.hasNext()) {
                        Schedule schedulex = (Schedule) var7.next();
                        schedulex.setStartTime(simpleDateFormat.format(calendar0.getTime()) + ":" + schedulex.getStartTime());
                        schedulex.setEndTime(simpleDateFormat.format(calendar0.getTime()) + ":" + schedulex.getEndTime());
                    }
                    schedulesx.addAll(schedules);
                })
                .flatMap((Function<List<Schedule>, ObservableSource<List<Schedule>>>) schedules ->
                        RadioUtil.getSchedules(today))
                .doOnNext(schedules -> {
                    Iterator var7 = schedules.iterator();
                    while (var7.hasNext()) {
                        Schedule schedulex = (Schedule) var7.next();
                        schedulex.setStartTime(simpleDateFormat.format(Calendar.getInstance().getTime()) + ":" + schedulex.getStartTime());
                        schedulex.setEndTime(simpleDateFormat.format(Calendar.getInstance().getTime()) + ":" + schedulex.getEndTime());
                    }
                    schedulesx.addAll(schedules);
                })
                .flatMap((Function<List<Schedule>, ObservableSource<List<Schedule>>>) schedules ->
                        RadioUtil.getSchedules(tomorrow))
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(schedules -> {
                    Iterator var7 = schedules.iterator();
                    while (var7.hasNext()) {
                        Schedule schedulex = (Schedule) var7.next();
                        schedulex.setStartTime(simpleDateFormat.format(calendar1.getTime()) + ":" + schedulex.getStartTime());
                        schedulex.setEndTime(simpleDateFormat.format(calendar1.getTime()) + ":" + schedulex.getEndTime());
                    }
                    schedulesx.addAll(schedules);
                    RadioUtil.fillData(schedulesx, radio);

                    if (!CollectionUtils.isEmpty(schedulesx)) {
                        XmPlayerManager.getInstance(getApplication()).playSchedule(schedulesx, -1);
                        Object navigation = ARouter.getInstance()
                                .build(AppConstants.Router.Home.F_PLAY_RADIIO).navigation();
                        if (null != navigation) {
                            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                                    new NavigateBean(AppConstants.Router.Home.F_PLAY_RADIIO,
                                            (ISupportFragment) navigation)));
                        }
                    }
                }, e -> e.printStackTrace());
    }

    public SingleLiveEvent<List<Radio>> getInitRadiosEvent() {
        return mInitRadiosEvent = createLiveData(mInitRadiosEvent);
    }

    public void setType(int type) {
        mType = type;
    }

    public void setProvinceCode(int provinceCode) {
        if(provinceCode!=mProvinceCode){
            curPage=1;
        }
        mProvinceCode = provinceCode;
    }

}
