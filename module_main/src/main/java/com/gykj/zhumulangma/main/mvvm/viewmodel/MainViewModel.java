package com.gykj.zhumulangma.main.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.extra.RxField;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.net.Constans;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.main.mvvm.model.MainModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
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
                }, Throwable::printStackTrace);
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
                playRadio(String.valueOf(historyBean.getGroupId()));
                break;
        }
    }

    public void play(long albumId, long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
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
                    RouteUtil.navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }

    public void playRadio(String radioId) {
        mModel.getSchedulesSource(radioId)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(schedules ->
                {
                    XmPlayerManager.getInstance(getApplication()).playSchedule(schedules, -1);
                    RouteUtil.navigateTo(AppConstants.Router.Home.F_PLAY_RADIIO);
                }, Throwable::printStackTrace);

    }

    public void getBing() {
        RxField<BingBean> bingBean=new RxField<>();
        mModel.getBing("js", "1")
                .doOnSubscribe(disposable ->accept(disposable))
                .flatMap((Function<BingBean, ObservableSource<ResponseBody>>) bean -> {
                    if (bean.getImages().get(0).getCopyrightlink().equals(SPUtils.getInstance().getString(AppConstants.SP.AD_URL))) {
                        return Observable.just(new RealResponseBody("", 0, null));
                    }
                    bingBean.set(bean);
                    return mModel.getCommonBody(Constans.BING_HOST + bean.getImages().get(0).getUrl());
                })
                .observeOn(Schedulers.io())
                .subscribe(body -> {
                    if (body.contentLength() != 0) {
                        FileIOUtils.writeFileFromIS(getApplication().getFilesDir().getAbsolutePath()
                                + AppConstants.Default.AD_NAME, body.byteStream());
                        SPUtils.getInstance().put(AppConstants.SP.AD_LABEL, bingBean.get().getImages().get(0).getCopyright());
                        SPUtils.getInstance().put(AppConstants.SP.AD_URL, bingBean.get().getImages().get(0).getCopyrightlink());
                    }
                }, Throwable::printStackTrace);
    }

    public SingleLiveEvent<String> getCoverSingleLiveEvent() {
        return mCoverSingleLiveEvent = createLiveData(mCoverSingleLiveEvent);
    }
}
