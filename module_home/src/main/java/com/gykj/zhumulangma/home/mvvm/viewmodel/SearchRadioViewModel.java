package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/13 15:12
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class SearchRadioViewModel extends BaseRefreshViewModel<ZhumulangmaModel,Radio> {
    private SingleLiveEvent<List<Radio>> mInitRadiosEvent;

    private int curPage = 1;
    private String mKeyword;

    public SearchRadioViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, mKeyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curPage));
        mModel.getSearchedRadios(map)
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

    private void getMoreRadios() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, mKeyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curPage));
        mModel.getSearchedRadios(map)
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

    @Override
    public void onViewLoadmore() {
        getMoreRadios();
    }

    public void play(String albumId, Track track) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.TRACK_ID, String.valueOf(track.getDataId()));
        mModel.getLastPlayTracks(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackList -> {
                    XmPlayerManager.getInstance(getApplication()).playList(trackList,
                            trackList.getTracks().indexOf(track));
                    RouteUtil.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }

    public void playRadio(Radio radio) {
        mModel.getSchedulesSource(radio)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(schedules ->
                {
                    XmPlayerManager.getInstance(getApplication()).playSchedule(schedules, -1);
                    RouteUtil.navigateTo(Constants.Router.Home.F_PLAY_RADIIO);
                }, Throwable::printStackTrace);
    }
    public SingleLiveEvent<List<Radio>> getInitRadiosEvent() {
        return mInitRadiosEvent =createLiveData(mInitRadiosEvent);
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }
}
