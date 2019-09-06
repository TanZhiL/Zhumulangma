package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.program.ProgramList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.opensdk.util.ModelUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * Date: 2019/9/5 14:35
 * Email: 1071931588@qq.com
 * Description:
 */
public class PlayRadioViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<ProgramList> mProgramsSingleLiveEvent;

    private SingleLiveEvent<List<Schedule>> mYestodaySingleLiveEvent;
    private SingleLiveEvent<List<Schedule>> mTodaySingleLiveEvent;
    private SingleLiveEvent<List<Schedule>> mTomorrowSingleLiveEvent;

    public PlayRadioViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void _getPrograms(String radioId) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIOID, radioId);
        mModel.getProgram(map).subscribe(programList ->
                getProgramsSingleLiveEvent().postValue(programList), e -> e.printStackTrace());
    }

    private Radio radio;

    public void _getSchedules(String radioId) {


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
                    fillData(schedules);
                    getYestodaySingleLiveEvent().postValue(schedules);
                }).flatMap((Function<List<Schedule>, ObservableSource<List<Schedule>>>) schedules ->
                RadioUtil.getSchedules(today))
                .doOnNext(schedules -> {
                    Iterator var7 = schedules.iterator();
                    while (var7.hasNext()) {
                        Schedule schedulex = (Schedule) var7.next();
                        schedulex.setStartTime(simpleDateFormat.format(Calendar.getInstance().getTime()) + ":" + schedulex.getStartTime());
                        schedulex.setEndTime(simpleDateFormat.format(Calendar.getInstance().getTime()) + ":" + schedulex.getEndTime());
                    }
                    fillData(schedules);
                    getTodaySingleLiveEvent().postValue(schedules);
                }).flatMap((Function<List<Schedule>, ObservableSource<List<Schedule>>>) schedules ->
                RadioUtil.getSchedules(tomorrow))
                .subscribe(schedules -> {
                    Iterator var7 = schedules.iterator();
                    while (var7.hasNext()) {
                        Schedule schedulex = (Schedule) var7.next();
                        schedulex.setStartTime(simpleDateFormat.format(calendar1.getTime()) + ":" + schedulex.getStartTime());
                        schedulex.setEndTime(simpleDateFormat.format(calendar1.getTime()) + ":" + schedulex.getEndTime());
                    }
                    fillData(schedules);
                    getTomorrowSingleLiveEvent().postValue(schedules);
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

    public SingleLiveEvent<ProgramList> getProgramsSingleLiveEvent() {
        return mProgramsSingleLiveEvent = createLiveData(mProgramsSingleLiveEvent);
    }

    public SingleLiveEvent<List<Schedule>> getYestodaySingleLiveEvent() {
        return mYestodaySingleLiveEvent = createLiveData(mYestodaySingleLiveEvent);
    }

    public SingleLiveEvent<List<Schedule>> getTodaySingleLiveEvent() {
        return mTodaySingleLiveEvent = createLiveData(mTodaySingleLiveEvent);
    }

    public SingleLiveEvent<List<Schedule>> getTomorrowSingleLiveEvent() {
        return mTomorrowSingleLiveEvent = createLiveData(mTomorrowSingleLiveEvent);
    }
}
