package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Thomas.
 * Date: 2019/8/14 10:21
 * Email: 1071931588@qq.com
 * Description:
 */
public class AlbumListViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<Album>> mLikeSingleLiveEvent;
    private SingleLiveEvent<List<Album>> mAlbumSingleLiveEvent;
    private int curPage = 1;
    private static final int PAGESIZE = 20;

    public AlbumListViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void _getGuessLikeAlbum() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, "50");
        map.put(DTransferConstants.PAGE, String.valueOf(1));
        mModel.getGuessLikeAlbum(map)
                .doOnSubscribe(disposable ->  getShowLoadingViewEvent().postValue(""))
                .subscribe(gussLikeAlbumList -> {
                    getShowLoadingViewEvent().postValue(null);
                    getLikeSingleLiveEvent().postValue(
                            gussLikeAlbumList.getAlbumList());
                }, e -> e.printStackTrace());
    }

    public void _getAlbumList(String type) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, String.valueOf(type));
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAlbumList(map)
                .doOnSubscribe(disposable -> {
                        getShowLoadingViewEvent().postValue(curPage == 1?"":null);
                })
                .subscribe(albumList -> {
                    getShowLoadingViewEvent().postValue(null);
                    curPage++;
                    getAlbumSingleLiveEvent().postValue(albumList.getAlbums());
                }, e -> e.printStackTrace());
    }

    public void _getAlbumList(long announcerId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.AID, String.valueOf(announcerId));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAlbumsByAnnouncer(map)
                .doOnSubscribe(disposable -> getShowLoadingViewEvent().postValue(curPage == 1?"":null))
                .subscribe(albumList -> {
                    getShowLoadingViewEvent().postValue(null);
                    curPage++;
                    getAlbumSingleLiveEvent().postValue(albumList.getAlbums());
                }, e -> e.printStackTrace());
    }

    public void _getPaidList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        mModel.getAllPaidAlbums(map)
                .doOnSubscribe(disposable -> {
                    if (curPage == 1)
                         getShowLoadingViewEvent().postValue("");
                })
                .subscribe(albumList -> {
                    getShowLoadingViewEvent().postValue(null);
                    curPage++;
                    getAlbumSingleLiveEvent().postValue(albumList.getAlbums());
                }, e -> e.printStackTrace());
    }

    public SingleLiveEvent<List<Album>> getLikeSingleLiveEvent() {
        return mLikeSingleLiveEvent = createLiveData(mLikeSingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getAlbumSingleLiveEvent() {
        return mAlbumSingleLiveEvent = createLiveData(mAlbumSingleLiveEvent);
    }
}
