package com.gykj.zhumulangma.common.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.net.http.CustException;
import com.gykj.zhumulangma.common.net.http.RxAdapter;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;
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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/6 8:34
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class RadioUtil {
    private Context mContext;
    private static volatile RadioUtil instance;

    private RadioUtil(Context context) {
        this.mContext = context;
    }

    public static RadioUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (RadioUtil.class) {
                if (instance == null) {
                    if (context instanceof Activity) {
                        context = context.getApplicationContext();
                    }
                    instance = new RadioUtil(context);
                }
            }
        }
        return instance;
    }

    public void playLiveRadioForSDK(final Radio radio) {
        List<Schedule> schedulesx = new ArrayList();

        Map<String, String> yestoday = new HashMap();
        yestoday.put("radio_id", radio.getDataId() + "");
        Calendar calendar0 = Calendar.getInstance();
        calendar0.add(Calendar.DAY_OF_MONTH,-1);
        yestoday.put("weekday", calendar0.get(Calendar.DAY_OF_WEEK)-1+"");

        Map<String, String> today = new HashMap();
        today.put("radio_id", radio.getDataId() + "");

        Map<String, String> tomorrow = new HashMap();
        tomorrow.put("radio_id", radio.getDataId() + "");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH,1);
        tomorrow.put("weekday", calendar0.get(Calendar.DAY_OF_WEEK)-1+"");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy:MM:dd");

        getSchedules(yestoday).doOnNext(schedules -> {
            Iterator var7 = schedules.iterator();
            while (var7.hasNext()) {
                Schedule schedulex = (Schedule) var7.next();
                schedulex.setStartTime(simpleDateFormat.format(calendar0.getTime()) + ":" + schedulex.getStartTime());
                schedulex.setEndTime(simpleDateFormat.format(calendar0.getTime())  + ":" + schedulex.getEndTime());
            }
            schedulesx.addAll(schedules);
        }).flatMap((Function<List<Schedule>, ObservableSource<List<Schedule>>>) schedules ->
                getSchedules(today)).doOnNext(schedules -> {
                    Iterator var7 = schedules.iterator();
                    while (var7.hasNext()) {
                        Schedule schedulex = (Schedule) var7.next();
                        schedulex.setStartTime(simpleDateFormat.format(Calendar.getInstance().getTime()) + ":" + schedulex.getStartTime());
                        schedulex.setEndTime(simpleDateFormat.format(Calendar.getInstance().getTime())  + ":" + schedulex.getEndTime());
                    }
                    schedulesx.addAll(schedules);
                }).flatMap((Function<List<Schedule>, ObservableSource<List<Schedule>>>) schedules ->
                getSchedules(tomorrow)).subscribe(schedules -> {
                    Iterator var7 = schedules.iterator();
                    while (var7.hasNext()) {
                        Schedule schedulex = (Schedule) var7.next();
                        schedulex.setStartTime(simpleDateFormat.format(calendar1.getTime()) + ":" + schedulex.getStartTime());
                        schedulex.setEndTime(simpleDateFormat.format(calendar1.getTime())  + ":" + schedulex.getEndTime());
                    }
                    schedulesx.addAll(schedules);
                    if(!CollectionUtils.isEmpty(schedulesx)){

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
                        XmPlayerManager.getInstance(mContext).playSchedule(schedulesx, -1);
                    }else {
                        Schedule schedule = ModelUtil.radioToSchedule(radio);
                        if (schedule == null) {
                            return;
                        }
                        schedulesx.add(schedule);
                        XmPlayerManager.getInstance(mContext).playSchedule(schedulesx, -1);
                    }
                }, e->e.printStackTrace());
    }

    /**
     * 获取节目列表
     *
     * @param specificParams
     * @return
     */
    public static Observable<List<Schedule>> getSchedules(Map<String, String> specificParams) {
        return Observable.create(new ObservableOnSubscribe<List<Schedule>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Schedule>> emitter) throws Exception {
                CommonRequest.getSchedules(specificParams,
                        new IDataCallBack<ScheduleList>() {
                            @Override
                            public void onSuccess(@Nullable ScheduleList scheduleList) {
                                if (CollectionUtils.isEmpty(scheduleList.getmScheduleList())) {
                                    emitter.onError(new Exception("节目列表为空"));
                                } else {
                                    emitter.onNext(scheduleList.getmScheduleList());
                                    emitter.onComplete();
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                emitter.onError(new CustException(String.valueOf(i), s));
                            }
                        });
            }
        }).compose(RxAdapter.exceptionTransformer());
    }

    public static void fillData(List<Schedule> schedulesx,Radio radio) {
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
}
