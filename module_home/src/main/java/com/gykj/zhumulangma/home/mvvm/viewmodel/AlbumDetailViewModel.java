package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * Date: 2019/8/14 13:41
 * Email: 1071931588@qq.com
 * Description:
 */
public class AlbumDetailViewModel extends BaseViewModel<ZhumulangmaModel> {


    private SingleLiveEvent<Album> mAlbumSingleLiveEvent;
    private SingleLiveEvent<List<Album>> mRelativeSingleLiveEvent;
    private SingleLiveEvent<TrackList> mTracksUpSingleLiveEvent;
    private SingleLiveEvent<TrackList> mTracksMoreSingleLiveEvent;
    private SingleLiveEvent<TrackList> mTracksSortSingleLiveEvent;
    private SingleLiveEvent<TrackList> mTracksInitSingleLiveEvent;
    private SingleLiveEvent<Track> mLastplaySingleLiveEvent;
    private CommonTrackList mCommonTrackList = CommonTrackList.newInstance();


    private int upTrackPage = 0;
    private int curTrackPage = 1;
    private String mSort = "asc";

    public AlbumDetailViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getAlbumDetail(String albumId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_IDS, albumId);
        mModel.getBatch(map)
                .doOnSubscribe(disposable -> postShowInitLoadViewEvent(true))
                .doOnNext(batchAlbumList -> getAlbumSingleLiveEvent().postValue(
                        batchAlbumList.getAlbums().get(0)))
                .flatMap((Function<BatchAlbumList, ObservableSource<TrackList>>) batchAlbumList -> getTrackInitObservable(albumId))
                .doFinally(() -> postShowInitLoadViewEvent(false))
                .subscribe(trackList -> {
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getTracksMoreSingleLiveEvent().postValue(
                            trackList);
                }, e -> e.printStackTrace());
    }

    public void getTrackList(String albumId) {
        getTrackInitObservable(albumId)
                .doOnSubscribe(disposable -> postShowInitLoadViewEvent(true))
                .doFinally(() -> postShowInitLoadViewEvent(false))
                .subscribe(trackList -> {
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getTracksInitSingleLiveEvent().postValue(
                            trackList);
                }, e -> e.printStackTrace());
    }

    private Observable<TrackList> getTrackInitObservable(String albumId) {
        Observable<TrackList> trackListObservable = mModel.listDesc(PlayHistoryBean.class, 1, 1, PlayHistoryBeanDao.Properties.Datatime,
                PlayHistoryBeanDao.Properties.AlbumId.eq(albumId)).doOnNext(playHistoryBeans -> {
            if (!CollectionUtils.isEmpty(playHistoryBeans))
                getLastplaySingleLiveEvent().setValue(playHistoryBeans.get(0).getTrack());
        }).flatMap((Function<List<PlayHistoryBean>, ObservableSource<TrackList>>) playHistoryBeans -> {
            if (null == getLastplaySingleLiveEvent().getValue()) {

                Map<String, String> map = new HashMap<>();
                map.put(DTransferConstants.ALBUM_ID, albumId);
                map.put(DTransferConstants.PAGE, String.valueOf(1));
                return mModel.getTracks(map).doOnNext(trackList -> {
                    curTrackPage = 1;
                    upTrackPage = 0;
                });
            } else {
                Map<String, String> map = new HashMap<>();
                map.put(DTransferConstants.ALBUM_ID, albumId);
                map.put(DTransferConstants.TRACK_ID, String.valueOf(getLastplaySingleLiveEvent()
                        .getValue().getDataId()));
                return mModel.getLastPlayTracks(map)
                        .doOnNext(lastPlayTrackList -> {
                            curTrackPage = lastPlayTrackList.getPageid();
                            upTrackPage = lastPlayTrackList.getPageid() - 1;
                        })
                        .map(lastPlayTrackList -> {
                    TrackList trackList = new TrackList();
//                    trackList.setAlbumId(Integer.parseInt(albumId));
                    trackList.cloneCommonTrackList(lastPlayTrackList);
                    return trackList;
                });
            }
        });
        return trackListObservable;
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

    public void getTrackList(String albumId,int page) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        mModel.getTracks(map)
                .observeOn(Schedulers.io())
                .subscribe(trackList -> {
                    upTrackPage = page-1;
                    curTrackPage=page+1;
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

    public SingleLiveEvent<Album> getAlbumSingleLiveEvent() {
        return mAlbumSingleLiveEvent = createLiveData(mAlbumSingleLiveEvent);
    }

    public SingleLiveEvent<TrackList> getTracksInitSingleLiveEvent() {
        return mTracksInitSingleLiveEvent = createLiveData(mTracksInitSingleLiveEvent);
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

    public SingleLiveEvent<Track> getLastplaySingleLiveEvent() {
        return mLastplaySingleLiveEvent = createLiveData(mLastplaySingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getRelativeSingleLiveEvent() {
        return mRelativeSingleLiveEvent=createLiveData(mRelativeSingleLiveEvent);
    }

    public CommonTrackList getCommonTrackList() {
        return mCommonTrackList;
    }

    public int getCurTrackPage() {
        return curTrackPage;
    }

    public int getUpTrackPage() {
        return upTrackPage;
    }
}
