package com.gykj.zhumulangma.main.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.db.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.extra.RxField;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.net.Constans;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.main.mvvm.model.MainModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;
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

    private SingleLiveEvent<PlayHistoryBean> mHistoryEvent;
    private SingleLiveEvent<String> mCoverEvent;
    private SingleLiveEvent<Void> mShowAdEvent;

    public MainViewModel(@NonNull Application application, MainModel model) {
        super(application, model);
    }

    public void getLastSound() {
        mModel.listDesc(PlayHistoryBean.class, 0, 0, PlayHistoryBeanDao.Properties.Datatime, null)
                .subscribe(historyBeans -> {
                    if (!CollectionUtils.isEmpty(historyBeans)) {
                        getHistoryEvent().postValue(historyBeans.get(0));
                    }
                }, Throwable::printStackTrace);
    }

    public SingleLiveEvent<PlayHistoryBean> getHistoryEvent() {
        return mHistoryEvent = createLiveData(mHistoryEvent);
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
                            getCoverEvent().postValue(TextUtils.isEmpty(coverUrlSmall)
                                    ? trackList.getTracks().get(i).getAlbum().getCoverUrlLarge() : coverUrlSmall);
                            XmPlayerManager.getInstance(getApplication()).playList(trackList, i);
                            break;
                        }
                    }
                    RouteHelper.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }

    public void playRadio(String radioId) {
       /* mModel.getSchedulesSource(radioId)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(schedules ->
                {
                    XmPlayerManager.getInstance(getApplication()).playSchedule(schedules, -1);
                    RouterUtil.navigateTo(Constants.Router.Home.F_PLAY_RADIIO);
                }, Throwable::printStackTrace);*/

    }

    public void getBing() {
        RxField<BingBean> bingBean = new RxField<>();
        //获取最新Bing数据
        mModel.getBing("js", "1")
                .doOnSubscribe(this)
                //查询本地广告URL
                .flatMap((Function<BingBean, ObservableSource<String>>) bean -> {
                    bingBean.set(bean);
                    return mModel.getSPString(Constants.SP.AD_URL);
                })
                //判断本地广告是否一致
                .flatMap((Function<String, ObservableSource<ResponseBody>>) s -> {
                    // 如果一致则直接跳过,不重新下载
                    if (bingBean.get().getImages().get(0).getCopyrightlink().equals(s)) {
                        return Observable.just(new RealResponseBody("", 0, null));
                    }
                    //否则下载图片文件
                    return mModel.getCommonBody(Constans.BING_HOST + bingBean.get().getImages().get(0).getUrl());
                })
               // 如果一致则直接跳过,不重新下载
                .filter(responseBody -> responseBody.contentLength()!=0)
                //保存图片文件
                .observeOn(Schedulers.io())
                .map(responseBody -> FileIOUtils.writeFileFromIS(getApplication().getFilesDir().getAbsolutePath()
                        + Constants.Default.AD_NAME, responseBody.byteStream()))
                //判断是否保存成
                .filter(aBoolean -> aBoolean)
                //如果保存成功,则更新本地广告信息
                .flatMap((Function<Boolean, ObservableSource<String>>) aBoolean ->
                        mModel.putSP(Constants.SP.AD_LABEL, bingBean.get().getImages().get(0).getCopyright()))
                .flatMap((Function<String, ObservableSource<String>>) s ->
                        mModel.putSP(Constants.SP.AD_URL, bingBean.get().getImages().get(0).getCopyrightlink()))
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
    }

    public SingleLiveEvent<String> getCoverEvent() {
        return mCoverEvent = createLiveData(mCoverEvent);
    }

    public SingleLiveEvent<Void> getShowAdEvent() {
        return mShowAdEvent = createLiveData(mShowAdEvent);
    }

    public void initAd() {
        mModel.getSPLong(Constants.SP.AD_TIME, 0)
                .doOnSubscribe(this)
                .subscribe(aLong -> {
                    if (System.currentTimeMillis() - aLong > 5 * 60 * 1000
                            && new File(App.getInstance().getFilesDir().getAbsolutePath() + Constants.Default.AD_NAME).exists()) {
                        getShowAdEvent().call();
                    } else {
                        getBing();
                    }
                }, Throwable::printStackTrace);
    }

    public void adDissmiss() {
        mModel.putSP(Constants.SP.AD_TIME, System.currentTimeMillis())
                .doOnSubscribe(this)
                .subscribe(aLong -> getBing(), Throwable::printStackTrace);
    }
}
