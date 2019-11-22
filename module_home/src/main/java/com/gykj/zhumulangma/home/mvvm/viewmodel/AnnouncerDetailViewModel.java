package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.AnnouncerListByIds;
import com.ximalaya.ting.android.opensdk.model.track.AnnouncerTrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 17:37
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class AnnouncerDetailViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Object> {

    private SingleLiveEvent<Announcer> mAnnouncerEvent;
    private SingleLiveEvent<AlbumList> mAlbumListEvent;
    private SingleLiveEvent<AnnouncerTrackList> mTrackListEvent;
    private long mAnnouncerId;

    public AnnouncerDetailViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        Map<String, String> map = new HashMap<>();
        map.put("ids", String.valueOf(mAnnouncerId));
        //主播详情
        mModel.getAnnouncersBatch(map)
                .doOnNext(announcerListByIds -> getAnnouncerEvent().setValue(announcerListByIds.getAnnouncers().get(0)))
                //主播专辑列表
                .flatMap((Function<AnnouncerListByIds, ObservableSource<AlbumList>>) announcerListByIds -> {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put(DTransferConstants.AID, String.valueOf(mAnnouncerId));
                    map1.put(DTransferConstants.PAGE, String.valueOf(1));
                    map1.put(DTransferConstants.PAGE_SIZE, String.valueOf(5));
                    return mModel.getAlbumsByAnnouncer(map1);
                }).doOnNext(albumList -> getAlbumListEvent().setValue(albumList))
                //主播声音列表
                .flatMap((Function<AlbumList, ObservableSource<AnnouncerTrackList>>) albumList -> {
                    Map<String, String> map12 = new HashMap<>();
                    map12.put(DTransferConstants.AID, String.valueOf(mAnnouncerId));
                    map12.put(DTransferConstants.PAGE, String.valueOf(1));
                    map12.put(DTransferConstants.PAGE_SIZE, String.valueOf(5));
                    return mModel.getTracksByAnnouncer(map12);
                })
                .subscribe(trackList -> {
                    getClearStatusEvent().call();
                    getTrackListEvent().setValue(trackList);
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    public void playTrack(long albumId, long trackId) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(trackList -> {
                    for (int i = 0; i < trackList.getTracks().size(); i++) {
                        if (trackList.getTracks().get(i).getDataId() == trackId) {
                            XmPlayerManager.getInstance(getApplication()).playList(trackList, i);
                            break;
                        }
                    }
                    RouterUtil.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }

    public SingleLiveEvent<Announcer> getAnnouncerEvent() {
        return mAnnouncerEvent = createLiveData(mAnnouncerEvent);
    }

    public SingleLiveEvent<AlbumList> getAlbumListEvent() {
        return mAlbumListEvent = createLiveData(mAlbumListEvent);
    }

    public SingleLiveEvent<AnnouncerTrackList> getTrackListEvent() {
        return mTrackListEvent = createLiveData(mTrackListEvent);
    }

    public void setAnnouncerId(long announcerId) {
        mAnnouncerId = announcerId;
    }
}
