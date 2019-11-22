package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/13 15:12
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class SearchAlbumViewModel extends BaseRefreshViewModel<ZhumulangmaModel,Album> {
    private SingleLiveEvent<List<Album>> mInitAlbumsEvent;

    private int curPage = 1;
    private String mKeyword;

    public SearchAlbumViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, mKeyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curPage));
        mModel.getSearchedAlbums(map)
                .subscribe(albumList -> {
                    if (CollectionUtils.isEmpty(albumList.getAlbums())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getClearStatusEvent().call();
                    getInitAlbumsEvent().setValue(albumList.getAlbums());

                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private void getMoreAlbum() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, mKeyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curPage));
        mModel.getSearchedAlbums(map)
                .subscribe(albumList -> {
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curPage++;
                    }
                    getFinishLoadmoreEvent().setValue(albumList.getAlbums());
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });
    }


    @Override
    public void onViewLoadmore() {
        getMoreAlbum();
    }


    public SingleLiveEvent<List<Album>> getInitAlbumsEvent() {
        return mInitAlbumsEvent =createLiveData(mInitAlbumsEvent);
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }
}
