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
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.home.mvvm.model.RadioModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
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

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.ISupportFragment;

public class RadioViewModel extends BaseRefreshViewModel<RadioModel, Album> {

    private SingleLiveEvent<List<PlayHistoryBean>> mHistorySingleLiveEvent;
    private SingleLiveEvent<List<Radio>> mLocalSingleLiveEvent;
    private SingleLiveEvent<List<Radio>> mTopSingleLiveEvent;
    private SingleLiveEvent<Void> mRefreshSingleLiveEvent;

    private int totalLocalPage = 1;
    private int curLocalPage = 1;

    private String mCityCode;

    public RadioViewModel(@NonNull Application application, RadioModel model) {
        super(application, model);
    }

    @Override
    public void onViewRefresh() {
        curLocalPage = 1;
        init(mCityCode);
    }

    public void _getHistory() {
        mModel.getHistory(1, 5)
                .subscribe(historyBeans -> getHistorySingleLiveEvent().postValue(historyBeans), e -> e.printStackTrace());
    }


    public void init(String cityCode) {
        mCityCode = cityCode;
        mModel.getHistory(1, 5)
                .doOnNext(historyBeans -> getHistorySingleLiveEvent().postValue(historyBeans))
                .flatMap((Function<List<PlayHistoryBean>, ObservableSource<RadioList>>) historyBeans -> getLocalListObservable(mCityCode))
                .flatMap((Function<RadioList, ObservableSource<RadioList>>) radioList -> getTopListObservable())
                .doOnSubscribe(d->getShowLoadingViewEvent().postValue(""))
                .doFinally(()->getFinishRefreshEvent().postValue(new ArrayList<>()))
                .subscribe(r-> getClearStatusEvent().call(), e->
                {   getShowErrorViewEvent().postValue(true);
                    e.printStackTrace();
                });
    }

    private Observable<RadioList> getLocalListObservable(String cityCode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CITY_CODE, cityCode);
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curLocalPage = curLocalPage >= totalLocalPage ? 1 : curLocalPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curLocalPage++));
        return mModel.getRadiosByCity(map)
                .doOnNext(radioList -> {
                    totalLocalPage = radioList.getTotalPage();
                    getLocalSingleLiveEvent().postValue(radioList.getRadios());
                });
    }


    public void getTopList() {
        getTopListObservable().subscribe(r -> {
        }, e -> e.printStackTrace());
    }

    private Observable<RadioList> getTopListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_COUNT, "5");

        return mModel.getRankRadios(map)
                .doOnNext(radioList -> getTopSingleLiveEvent().postValue(radioList.getRadios()));
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
                .doOnSubscribe(d -> getShowLoadingViewEvent().postValue(""))
                .doFinally(() -> getShowLoadingViewEvent().postValue(null))
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

    public SingleLiveEvent<List<PlayHistoryBean>> getHistorySingleLiveEvent() {
        return mHistorySingleLiveEvent = createLiveData(mHistorySingleLiveEvent);
    }

    public SingleLiveEvent<List<Radio>> getLocalSingleLiveEvent() {
        return mLocalSingleLiveEvent = createLiveData(mLocalSingleLiveEvent);
    }

    public SingleLiveEvent<List<Radio>> getTopSingleLiveEvent() {
        return mTopSingleLiveEvent = createLiveData(mTopSingleLiveEvent);
    }

    public SingleLiveEvent<Void> getRefreshSingleLiveEvent() {
        return mRefreshSingleLiveEvent = createLiveData(mRefreshSingleLiveEvent);
    }
}
