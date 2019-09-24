package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.AnnouncerListByIds;
import com.ximalaya.ting.android.opensdk.model.track.AnnouncerTrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/10 17:37
 * Email: 1071931588@qq.com
 * Description:
 */
public class AnnouncerDetailViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<Announcer> mAnnouncerSingleLiveEvent;
    private SingleLiveEvent<AlbumList> mAlbumListSingleLiveEvent;
    private SingleLiveEvent<AnnouncerTrackList> mTrackListSingleLiveEvent;

    public AnnouncerDetailViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getDetail(String announcerId) {
        Map<String, String> map = new HashMap<>();
        map.put("ids", announcerId);
        mModel.getAnnouncersBatch(map)
                .doOnNext(announcerListByIds -> getAnnouncerSingleLiveEvent().postValue(announcerListByIds.getAnnouncers().get(0)))
                .flatMap((Function<AnnouncerListByIds, ObservableSource<AlbumList>>) announcerListByIds -> {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put(DTransferConstants.AID, announcerId);
                    map1.put(DTransferConstants.PAGE, String.valueOf(1));
                    map1.put(DTransferConstants.PAGE_SIZE, String.valueOf(5));
                    return mModel.getAlbumsByAnnouncer(map1);
                }).doOnNext(albumList -> getAlbumListSingleLiveEvent().postValue(albumList))
                .flatMap((Function<AlbumList, ObservableSource<AnnouncerTrackList>>) albumList -> {
                    Map<String, String> map12 = new HashMap<>();
                    map12.put(DTransferConstants.AID, announcerId);
                    map12.put(DTransferConstants.PAGE, String.valueOf(1));
                    map12.put(DTransferConstants.PAGE_SIZE, String.valueOf(5));
                    return mModel.getTracksByAnnouncer(map12);
                })
                .doOnSubscribe(d->  postShowLoadingViewEvent(""))
                .doFinally(()-> postShowLoadingViewEvent(null))
                .subscribe(trackList -> getTrackListSingleLiveEvent().postValue(trackList), e -> e.printStackTrace());
    }
    public void play(long albumId,long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d ->  postShowLoadingViewEvent(""))
                .doFinally(() -> postShowLoadingViewEvent(null))
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
    public SingleLiveEvent<Announcer> getAnnouncerSingleLiveEvent() {
        return mAnnouncerSingleLiveEvent = createLiveData(mAnnouncerSingleLiveEvent);
    }

    public SingleLiveEvent<AlbumList> getAlbumListSingleLiveEvent() {
        return mAlbumListSingleLiveEvent = createLiveData(mAlbumListSingleLiveEvent);
    }

    public SingleLiveEvent<AnnouncerTrackList> getTrackListSingleLiveEvent() {
        return mTrackListSingleLiveEvent = createLiveData(mTrackListSingleLiveEvent);
    }
}
