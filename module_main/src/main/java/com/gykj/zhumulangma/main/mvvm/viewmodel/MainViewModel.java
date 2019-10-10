package com.gykj.zhumulangma.main.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.net.API;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.main.mvvm.model.MainModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.ISupportFragment;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;

/**
 * Author: Thomas.
 * Date: 2019/9/10 8:23
 * Email: 1071931588@qq.com
 * Description:
 */
public class MainViewModel extends BaseViewModel<MainModel> {

    private SingleLiveEvent<PlayHistoryBean> mHistorySingleLiveEvent;
    private SingleLiveEvent<String> mCoverSingleLiveEvent;

    public MainViewModel(@NonNull Application application, MainModel model) {
        super(application, model);
    }

    public void getLastSound() {
        mModel.listDesc(PlayHistoryBean.class, 0, 0, PlayHistoryBeanDao.Properties.Datatime, null)
                .subscribe(historyBeans -> {
                    if (!CollectionUtils.isEmpty(historyBeans)) {
                        getHistorySingleLiveEvent().postValue(historyBeans.get(0));
                    }
                }, e -> e.printStackTrace());
    }

    public SingleLiveEvent<PlayHistoryBean> getHistorySingleLiveEvent() {
        return mHistorySingleLiveEvent = createLiveData(mHistorySingleLiveEvent);
    }

    public void play(PlayHistoryBean historyBean) {
        switch (historyBean.getKind()) {
            case PlayableModel.KIND_TRACK:
                play(historyBean.getGroupId(), historyBean.getTrack().getDataId());
                break;
            case PlayableModel.KIND_SCHEDULE:
                play(String.valueOf(historyBean.getGroupId()));
                break;
        }
    }

    public void play(long albumId, long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(trackList -> {
                    for (int i = 0; i < trackList.getTracks().size(); i++) {
                        if (trackList.getTracks().get(i).getDataId() == trackId) {
                            String coverUrlSmall = trackList.getTracks().get(i).getCoverUrlSmall();
                            getCoverSingleLiveEvent().postValue(TextUtils.isEmpty(coverUrlSmall)
                                    ? trackList.getTracks().get(i).getAlbum().getCoverUrlLarge() : coverUrlSmall);
                            XmPlayerManager.getInstance(getApplication()).playList(trackList, i);
                            break;
                        }
                    }
                    Object navigation = ARouter.getInstance()
                            .build(AppConstants.Router.Home.F_PLAY_TRACK).navigation();
                    if (null != navigation) {
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
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
                    fillData(schedulesx);

                    if (!CollectionUtils.isEmpty(schedulesx)) {
                        getCoverSingleLiveEvent().postValue(radio.getCoverUrlSmall());
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

    private BingBean bingBean;

    public void _getBing() {

        mModel.getBing("js", "1")
                .flatMap((Function<BingBean, ObservableSource<ResponseBody>>) bean -> {
                    if (bean.getImages().get(0).getCopyrightlink().equals(SPUtils.getInstance().getString(AppConstants.SP.AD_URL))) {
                        return Observable.just(new RealResponseBody("", 0, null));
                    }
                    bingBean = bean;
                    return mModel.getCommonBody(API.BING_HOST + bean.getImages().get(0).getUrl());
                })
                .observeOn(Schedulers.io())
                .subscribe(body -> {
                    if (body.contentLength() != 0) {
                        FileIOUtils.writeFileFromIS(getApplication().getFilesDir().getAbsolutePath()
                                + AppConstants.Default.AD_NAME, body.byteStream());
                        SPUtils.getInstance().put(AppConstants.SP.AD_LABEL, bingBean.getImages().get(0).getCopyright());
                        SPUtils.getInstance().put(AppConstants.SP.AD_URL, bingBean.getImages().get(0).getCopyrightlink());
                    }
                }, e -> e.printStackTrace());
    }

    public SingleLiveEvent<String> getCoverSingleLiveEvent() {
        return mCoverSingleLiveEvent = createLiveData(mCoverSingleLiveEvent);
    }
}
