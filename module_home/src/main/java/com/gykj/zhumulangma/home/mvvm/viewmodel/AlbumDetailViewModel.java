package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.AlbumTrackBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CommonTrackList mCommonTrackList = CommonTrackList.newInstance();
    private int curTrackPage = 1;
    private String mSort = "";

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

    public void getTrackList(String albumId, String sort) {
        if (!mSort.equals(sort)) {
            curTrackPage = 1;
        }
        mSort = sort;

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, sort);
        map.put(DTransferConstants.PAGE, String.valueOf(curTrackPage));
        mModel.getTracks(map)
                .observeOn(Schedulers.io())
                .subscribe(trackList -> {
                    curTrackPage++;
                    mCommonTrackList.updateCommonTrackList(mCommonTrackList.getTracks().size(), trackList);
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

    public CommonTrackList getCommonTrackList() {
        return mCommonTrackList;
    }
}
