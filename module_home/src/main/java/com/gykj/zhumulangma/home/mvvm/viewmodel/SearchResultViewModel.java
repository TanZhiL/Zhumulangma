package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Author: Thomas.
 * Date: 2019/8/13 15:12
 * Email: 1071931588@qq.com
 * Description:
 */
public class SearchResultViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<Album>> mAlbumSingleLiveEvent;
    private SingleLiveEvent<List<Track>> mTrackSingleLiveEvent;
    private SingleLiveEvent<List<Announcer>> mAnnouncerSingleLiveEvent;
    private SingleLiveEvent<List<Radio>> mRadioSingleLiveEvent;

    private int curAlbumPage = 1;
    private int curTrackPage = 1;
    private int curAnnouncerPage = 1;
    private int curRadioPage = 1;

    public SearchResultViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void searchAlbums(String keyword) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curAlbumPage));
        mModel.getSearchedAlbums(map)
                .subscribe(albumList -> {
                    curAlbumPage++;
                    getAlbumSingleLiveEvent().postValue(albumList.getAlbums());
                }, e->e.printStackTrace());
    }
    public void searchAnnouncer(String keyword) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curAnnouncerPage));
        mModel.getSearchAnnouncers(map)
                .subscribe(announcerList -> {
                    curAnnouncerPage++;
                    getAnnouncerSingleLiveEvent().postValue(announcerList.getAnnouncerList());
                }, e->e.printStackTrace());
    }
    public void searchRadios(String keyword) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curRadioPage));
        mModel.getSearchedRadios(map)
                .subscribe(radioList -> {
                    curRadioPage++;
                    getRadioSingleLiveEvent().postValue(radioList.getRadios());
                }, e->e.printStackTrace());
    }




    public SingleLiveEvent<List<Album>> getAlbumSingleLiveEvent() {
        return mAlbumSingleLiveEvent=createLiveData(mAlbumSingleLiveEvent);
    }

    public SingleLiveEvent<List<Track>> getTrackSingleLiveEvent() {
        return mTrackSingleLiveEvent=createLiveData(mTrackSingleLiveEvent);
    }

    public SingleLiveEvent<List<Announcer>> getAnnouncerSingleLiveEvent() {
        return mAnnouncerSingleLiveEvent=createLiveData(mAnnouncerSingleLiveEvent);
    }

    public SingleLiveEvent<List<Radio>> getRadioSingleLiveEvent() {
        return mRadioSingleLiveEvent=createLiveData(mRadioSingleLiveEvent);
    }
}
