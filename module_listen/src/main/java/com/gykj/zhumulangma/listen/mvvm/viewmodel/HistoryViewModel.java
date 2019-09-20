package com.gykj.zhumulangma.listen.mvvm.viewmodel;

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
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.util.DateUtil;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.listen.bean.PlayHistoryItem;
import com.gykj.zhumulangma.listen.mvvm.model.HistoryModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/8/20 13:56
 * Email: 1071931588@qq.com
 * Description:
 */
public class HistoryViewModel extends BaseViewModel<HistoryModel> {
    private SingleLiveEvent<List<PlayHistoryItem>> mHistorysSingleLiveEvent;
    private static final int PAGESIZE = 20;
    private int curPage = 1;

    public HistoryViewModel(@NonNull Application application, HistoryModel model) {
        super(application, model);
    }

    public void _getHistory() {
        mModel.getHistory(curPage, PAGESIZE)
                .observeOn(Schedulers.io())
                .map(playHistoryBeans -> convertSections(playHistoryBeans))
                .subscribe(playHistorySections -> {
                    if(playHistorySections.size()>0)
                    curPage++;
                    getHistorySingleLiveEvent().postValue(playHistorySections);
                }, e -> {
                    e.printStackTrace();
                    postShowNoDataViewEvent(true);
                });
    }

    public void play(long albumId,long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> postShowInitLoadViewEvent(true))
                .doFinally(() -> postShowInitLoadViewEvent(false))
                .subscribe(trackList -> {
                    for (int i = 0; i < trackList.getTracks().size(); i++) {
                        if(trackList.getTracks().get(i).getDataId()==trackId){
                            XmPlayerManager.getInstance(getApplication()).playList(trackList,i);
                            break;
                        }
                    }
                    Object navigation = ARouter.getInstance()
                            .build(AppConstants.Router.Home.F_PLAY_TRACK).navigation();
                    if (null != navigation) {
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
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
                    RadioUtil.fillData(schedulesx,radio);
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
                /*    else {
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
    private List<PlayHistoryItem> convertSections(List<PlayHistoryBean> beans){
        List<PlayHistoryItem> sections=new ArrayList<>();
        Map<String,List<PlayHistoryBean>> map=new LinkedHashMap<>();

        for (PlayHistoryBean bean:beans) {
            List<PlayHistoryBean> playHistoryBeans = map.get(dateCovert(bean.getDatatime()));
            if(playHistoryBeans==null){
                playHistoryBeans=new ArrayList<>();
                map.put(dateCovert(bean.getDatatime()),playHistoryBeans);
            }
            playHistoryBeans.add(bean);
        }

        Iterator<Map.Entry<String, List<PlayHistoryBean>>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry entry = iterator.next();
            String key =(String)entry.getKey();
            sections.add(new PlayHistoryItem(PlayHistoryItem.HEADER,key));
            List<PlayHistoryBean> list = (List<PlayHistoryBean>) entry.getValue();
            for (PlayHistoryBean bean : list) {
                sections.add(new PlayHistoryItem(bean.getKind().equals(PlayableModel.KIND_TRACK) ?
                        PlayHistoryItem.TRACK:PlayHistoryItem.SCHEDULE,bean));
            }
        }

        return sections;
    }
    public String dateCovert(long datetime){

        if(datetime > DateUtil.getDayBegin().getTime()){
            return "今天";
        }else if(datetime > DateUtil.getBeginDayOfYesterday().getTime()){
            return "昨天";
        }else {
            return "更早";
        }

    }
    public void clear() {
        mModel.clearAll(PlayHistoryBean.class).subscribe();
    }

    public SingleLiveEvent<List<PlayHistoryItem>> getHistorySingleLiveEvent() {
        return mHistorysSingleLiveEvent = createLiveData(mHistorysSingleLiveEvent);
    }

}
