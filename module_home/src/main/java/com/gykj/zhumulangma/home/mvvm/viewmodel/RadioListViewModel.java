package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.home.mvvm.model.RadioModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.opensdk.util.ModelUtil;

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
public class RadioListViewModel extends BaseViewModel<RadioModel> {
    public static final String COUNTRY="1";
    public static final String PROVINCE="2";
    public static final String INTERNET="3";

    private SingleLiveEvent<List<Radio>> mRadioSingleLiveEvent;
    private SingleLiveEvent<List<PlayHistoryBean>> mHistorySingleLiveEvent;
    private int curPage = 1;
    private static final int PAGESIZE = 20;
    private String mProvinceCode="";
    public RadioListViewModel(@NonNull Application application, RadioModel model) {
        super(application, model);
    }

    public void getLocalCity(String cityCode) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CITY_CODE, cityCode);
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        map.put(DTransferConstants.PAGE,String.valueOf(curPage));
        mModel.getRadiosByCity(map)
                .doOnSubscribe(disposable -> postShowInitLoadViewEvent(curPage == 1))
                .subscribe(radioList -> {
                    postShowInitLoadViewEvent(false);
                    curPage++;
                    getRadioSingleLiveEvent().postValue(radioList.getRadios());
                }, e->e.printStackTrace());
    }

    public void getRadioList(String flag,String extras) {

        Map<String, String> map = new HashMap<String, String>();
        //电台类型：1-国家台，2-省市台，3-网络台
        map.put(DTransferConstants.RADIOTYPE, flag);
        if(flag.equals(PROVINCE)){
            if(!extras.equals(mProvinceCode)){
                curPage=1;
                mProvinceCode=extras;
            }
            map.put(DTransferConstants.PROVINCECODE, extras);
        }
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        mModel.getRadios(map)
                .doOnSubscribe(disposable -> postShowInitLoadViewEvent(curPage == 1))
                .subscribe(radioList -> {
                    postShowInitLoadViewEvent(false);
                    curPage++;
                    getRadioSingleLiveEvent().postValue(radioList.getRadios());
                }, e->e.printStackTrace());

    }

    public void _getRadiosByCategory(String type) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_CATEGORY_ID, String.valueOf(type));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getRadiosByCategory(map)
                .doOnSubscribe(disposable -> postShowInitLoadViewEvent(curPage == 1))
                .subscribe(listByCategory -> {
                    postShowInitLoadViewEvent(false);
                    curPage++;
                    getRadioSingleLiveEvent().postValue(listByCategory.getRadios());
                }, e -> e.printStackTrace());
    }

    public void _getRankRadios() {
        Map<String, String> map = new HashMap<String, String>();
        //获取前100名
        map.put(DTransferConstants.RADIO_COUNT, "100");
        mModel.getRankRadios(map)
                .doOnSubscribe(disposable -> postShowInitLoadViewEvent(curPage == 1))
                .subscribe(radioList -> {
                    postShowInitLoadViewEvent(false);
                    curPage++;
                    getRadioSingleLiveEvent().postValue(radioList.getRadios());
                }, e->e.printStackTrace());
    }

    public void _getHistory() {
        mModel.getHistory(curPage,PAGESIZE)
                .subscribe(historyBeans -> {
                    curPage++;
                    getHistorySingleLiveEvent().postValue(historyBeans);
                }, e->e.printStackTrace());
    }

    private Radio radio;
    public void play(String radioId) {
        List<Schedule> schedulesx=new ArrayList<>();
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
                .doOnSubscribe(d -> postShowInitLoadViewEvent(true))
                .doFinally(() -> postShowInitLoadViewEvent(false))
                .subscribe(schedules -> {
                    Iterator var7 = schedules.iterator();
                    while (var7.hasNext()) {
                        Schedule schedulex = (Schedule) var7.next();
                        schedulex.setStartTime(simpleDateFormat.format(calendar1.getTime()) + ":" + schedulex.getStartTime());
                        schedulex.setEndTime(simpleDateFormat.format(calendar1.getTime()) + ":" + schedulex.getEndTime());
                    }
                    schedulesx.addAll(schedules);
                    fillData(schedulesx);

                    if(!CollectionUtils.isEmpty(schedulesx)){
                        XmPlayerManager.getInstance(getApplication()).playSchedule(schedulesx,-1);
                        Object navigation = ARouter.getInstance()
                                .build(AppConstants.Router.Home.F_PLAY_RADIIO).navigation();
                        if (null != navigation) {
                            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                                    new NavigateBean(AppConstants.Router.Home.F_PLAY_RADIIO,
                                            (ISupportFragment) navigation)));
                        }
                    }
               /*     else {
                        Schedule schedule = ModelUtil.radioToSchedule(radio);
                        if (schedule == null) {
                            return;
                        }
                        schedulesx.add(schedule);
                        XmPlayerManager.getInstance(getApplication()).playSchedule(schedulesx, -1);
                        Object navigation = ARouter.getInstance()
                                .build(AppConstants.Router.Home.F_PLAY_RADIIO).navigation();
                        if (null != navigation) {
                            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                                    new NavigateBean(AppConstants.Router.Home.F_PLAY_RADIIO,
                                            (ISupportFragment) navigation)));
                        }
                    }*/
                }, e -> e.printStackTrace());
    }
    private void fillData(List<Schedule> schedulesx) {
        if (!CollectionUtils.isEmpty(schedulesx)) {
            Iterator var = schedulesx.iterator();
            while (var.hasNext()) {
                Schedule schedulex = (Schedule) var.next();
                Program program = schedulex.getRelatedProgram();
                if (program == null) {
                    program = new Program();
                    schedulex.setRelatedProgram(program);
                }
                program.setBackPicUrl(radio.getCoverUrlLarge());
                schedulex.setRadioId(radio.getDataId());
                schedulex.setRadioName(radio.getRadioName());
                schedulex.setRadioPlayCount(radio.getRadioPlayCount());
                if (BaseUtil.isInTime(schedulex.getStartTime() + "-" + schedulex.getEndTime()) == 0) {
                    program.setRate24AacUrl(radio.getRate24AacUrl());
                    program.setRate24TsUrl(radio.getRate24TsUrl());
                    program.setRate64AacUrl(radio.getRate64AacUrl());
                    program.setRate64TsUrl(radio.getRate64TsUrl());
                    break;
                }
            }
        }
    }
    public SingleLiveEvent<List<Radio>> getRadioSingleLiveEvent() {
        return mRadioSingleLiveEvent=createLiveData(mRadioSingleLiveEvent);
    }
    public SingleLiveEvent<List<PlayHistoryBean>> getHistorySingleLiveEvent() {
        return mHistorySingleLiveEvent=createLiveData(mHistorySingleLiveEvent);
    }
}
