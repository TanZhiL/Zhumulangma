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
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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
    private SingleLiveEvent<List<Track>> mTracksSingleLiveEvent;
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
                .subscribe(batchAlbumList -> getAlbumSingleLiveEvent().postValue(
                        batchAlbumList.getAlbums().get(0)), e -> e.printStackTrace());
    }


    public void getTrackList(String albumId) {

        mModel.listDesc(PlayHistoryBean.class, 1, 1, PlayHistoryBeanDao.Properties.Datatime,
                PlayHistoryBeanDao.Properties.AlbumId.eq(albumId)).doOnNext(playHistoryBeans -> {
            if (!CollectionUtils.isEmpty(playHistoryBeans))
                getLastplaySingleLiveEvent().setValue(playHistoryBeans.get(0).getTrack());
        }).flatMap((Function<List<PlayHistoryBean>, ObservableSource<Integer>>) playHistoryBeans -> {
            if (null == getLastplaySingleLiveEvent().getValue()) {
                return Observable.just(1);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put(DTransferConstants.ALBUM_ID, albumId);
                map.put(DTransferConstants.TRACK_ID, String.valueOf(getLastplaySingleLiveEvent().getValue().getDataId()));
                return mModel.getLastPlayTracks(map).map(lastPlayTrackList -> lastPlayTrackList.getPageid());
            }
        }).flatMap((Function<Integer, ObservableSource<TrackList>>) integer -> {
            curTrackPage=integer;
            upTrackPage=integer-1;
            Map<String, String> map = new HashMap<>();
            map.put(DTransferConstants.ALBUM_ID, albumId);
            map.put(DTransferConstants.PAGE, String.valueOf(integer));
            return mModel.getTracks(map);
        }).observeOn(Schedulers.io())
                .subscribe(trackList -> {
                    curTrackPage++;
                    mCommonTrackList.updateCommonTrackList(mCommonTrackList.getTracks().size(), trackList);
                    getTracksSingleLiveEvent().postValue(
                            trackList.getTracks());
                }, e -> e.printStackTrace());
    }

    public void getTrackList(String albumId, String sort,boolean isUp) {
        int page;
        if (!mSort.equals(sort)) {
            upTrackPage=0;
            curTrackPage = 1;
            mSort = sort;
        }
        if(isUp){
            page=upTrackPage;
            if(0 == page){
                getTracksSingleLiveEvent().call();
                return;
            }
        }else {
            page=curTrackPage;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, sort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        mModel.getTracks(map)
                .observeOn(Schedulers.io())
                .subscribe(trackList -> {
                    if(isUp){
                        upTrackPage--;
                        mCommonTrackList.updateCommonTrackList(0, trackList);
                    }else {
                        curTrackPage++;
                        mCommonTrackList.updateCommonTrackList(mCommonTrackList.getTracks().size(), trackList);
                    }
                    getTracksSingleLiveEvent().postValue(
                            trackList.getTracks());
                }, e -> e.printStackTrace());

    }

    public SingleLiveEvent<Album> getAlbumSingleLiveEvent() {
        return mAlbumSingleLiveEvent = createLiveData(mAlbumSingleLiveEvent);
    }

    public SingleLiveEvent<List<Track>> getTracksSingleLiveEvent() {
        return mTracksSingleLiveEvent = createLiveData(mTracksSingleLiveEvent);
    }

    public SingleLiveEvent<Track> getLastplaySingleLiveEvent() {
        return mLastplaySingleLiveEvent =createLiveData(mLastplaySingleLiveEvent);
    }

    public CommonTrackList getCommonTrackList() {
        return mCommonTrackList;
    }
}
