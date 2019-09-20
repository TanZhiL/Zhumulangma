package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.dao.SubscribeBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * Date: 2019/8/28 8:27
 * Email: 1071931588@qq.com
 * Description:
 */
public class PlayTrackViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<List<Album>> mAlbumSingleLiveEvent;
    private SingleLiveEvent<TrackList> mTracksInitSingleLiveEvent;
    private SingleLiveEvent<TrackList> mTracksUpSingleLiveEvent;
    private SingleLiveEvent<TrackList> mTracksMoreSingleLiveEvent;
    private SingleLiveEvent<TrackList> mTracksSortSingleLiveEvent;
    private SingleLiveEvent<Boolean> mSubscribeSingleLiveEvent;
    private CommonTrackList mCommonTrackList = CommonTrackList.newInstance();

    private int upTrackPage = 0;
    private int curTrackPage = 1;
    private String mSort = "asc";

    public PlayTrackViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void unsubscribe(long albumId){
        mModel.remove(SubscribeBean.class,albumId).subscribe(aBoolean ->
                getSubscribeSingleLiveEvent().postValue(false), e->e.printStackTrace());

    }
    public void subscribe(String albumId){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_IDS, albumId);
        mModel.getBatch(map)
                .flatMap((Function<BatchAlbumList, ObservableSource<SubscribeBean>>) albumList ->
                        mModel.insert(new SubscribeBean(albumList.getAlbums().get(0).getId(),
                        albumList.getAlbums().get(0),System.currentTimeMillis())))
                .subscribe(subscribeBean -> getSubscribeSingleLiveEvent().postValue(true), e->e.printStackTrace());
    }

    public void getSubscribe(String albumId){
        mModel.list(SubscribeBean.class, SubscribeBeanDao.Properties.AlbumId.eq(albumId))
                .subscribe(subscribeBeans ->
                        getSubscribeSingleLiveEvent().postValue(subscribeBeans.size() > 0), e->e.printStackTrace());
    }
    public void getRelativeAlbums(String trackId){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TRACKID, trackId);
        mModel.getRelativeAlbumsUseTrackId(map)
                .subscribe(relativeAlbums -> getAlbumSingleLiveEvent().postValue(
                        relativeAlbums.getRelativeAlbumList()), e->e.printStackTrace());
    }
    public void _getLastPlayTracks(Track track){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID,String.valueOf(track.getAlbum().getAlbumId()));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(track.getDataId()));
        mModel.getLastPlayTracks(map)
                .doOnNext(lastPlayTrackList -> {
                    curTrackPage = lastPlayTrackList.getPageid();
                    upTrackPage = lastPlayTrackList.getPageid() - 1;
                })
                .map(lastPlayTrackList -> {
                    TrackList trackList = new TrackList();
                    trackList.cloneCommonTrackList(lastPlayTrackList);
                    mCommonTrackList.cloneCommonTrackList(lastPlayTrackList);
                    return trackList;
                }).subscribe(trackList -> {
                    curTrackPage++;
                    getTracksInitSingleLiveEvent().postValue(trackList);
                }, e->e.printStackTrace());
    }

    public void getTrackList(String albumId, String sort) {
        if(!sort.equals(mSort)){
            curTrackPage = 1;
            mSort = sort;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(curTrackPage));
        mModel.getTracks(map)
                .observeOn(Schedulers.io())
                .subscribe(trackList -> {
                    upTrackPage = 0;
                    curTrackPage ++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getTracksSortSingleLiveEvent().postValue(
                            trackList);
                }, e -> e.printStackTrace());

    }

    public void getTrackList(String albumId, boolean isUp) {
        int page;
        if (isUp) {
            page = upTrackPage;
            if (0 == page) {
                getTracksUpSingleLiveEvent().call();
                return;
            }
        } else {
            page = curTrackPage;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        mModel.getTracks(map)
                .observeOn(Schedulers.io())
                .subscribe(trackList -> {
                    if (isUp) {
                        upTrackPage--;
                        mCommonTrackList.updateCommonTrackList(0, trackList);
                        getTracksUpSingleLiveEvent().postValue(trackList);
                    } else {
                        curTrackPage++;
                        mCommonTrackList.updateCommonTrackList(mCommonTrackList.getTracks().size(), trackList);
                        getTracksMoreSingleLiveEvent().postValue(trackList);
                    }
                }, e -> e.printStackTrace());

    }

    public SingleLiveEvent<List<Album>> getAlbumSingleLiveEvent() {
        return mAlbumSingleLiveEvent=createLiveData(mAlbumSingleLiveEvent);
    }

    public SingleLiveEvent<TrackList> getTracksInitSingleLiveEvent() {
        return mTracksInitSingleLiveEvent=createLiveData(mTracksInitSingleLiveEvent);
    }
    public SingleLiveEvent<TrackList> getTracksUpSingleLiveEvent() {
        return mTracksUpSingleLiveEvent = createLiveData(mTracksUpSingleLiveEvent);
    }

    public SingleLiveEvent<TrackList> getTracksMoreSingleLiveEvent() {
        return mTracksMoreSingleLiveEvent = createLiveData(mTracksMoreSingleLiveEvent);
    }

    public SingleLiveEvent<TrackList> getTracksSortSingleLiveEvent() {
        return mTracksSortSingleLiveEvent = createLiveData(mTracksSortSingleLiveEvent);
    }
    public SingleLiveEvent<Boolean> getSubscribeSingleLiveEvent() {
        return mSubscribeSingleLiveEvent = createLiveData(mSubscribeSingleLiveEvent);
    }

    public CommonTrackList getCommonTrackList() {
        return mCommonTrackList;
    }
}
