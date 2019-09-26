package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.home.fragment.AlbumListFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Thomas.
 * Date: 2019/8/14 10:21
 * Email: 1071931588@qq.com
 * Description:
 */
public class AlbumListViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Album> {

    private int curPage = 1;
    private static final int PAGESIZE = 20;
    private int type;
    private long mAnnouncerId;

    public AlbumListViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    @Override
    public void onViewLoadmore() {
        if (type == AlbumListFragment.PAID) {
            _getPaidList();
        } else if (type == AlbumListFragment.ANNOUNCER) {
            _getAlbumList(mAnnouncerId);
        } else {
            _getAlbumList(String.valueOf(type));
        }
    }

    public void _getGuessLikeAlbum() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, "50");
        map.put(DTransferConstants.PAGE, String.valueOf(1));
        mModel.getGuessLikeAlbum(map)
                .doOnSubscribe(disposable -> getShowLoadingViewEvent().postValue(""))
                .subscribe(gussLikeAlbumList -> {
                    if (CollectionUtils.isEmpty(gussLikeAlbumList.getAlbumList())) {
                        getShowEmptyViewEvent().postValue(true);
                        return;
                    }
                    getClearStatusEvent().call();
                    getFinishLoadmoreEvent().postValue(gussLikeAlbumList.getAlbumList());
                }, e -> {
                    getShowErrorViewEvent().postValue(true);
                    e.printStackTrace();
                });
    }

    public void _getAlbumList(String type) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, String.valueOf(type));
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAlbumList(map)
                .doOnSubscribe(disposable -> getShowLoadingViewEvent().postValue(curPage == 1 ? "" : null))
                .subscribe(albumList -> {
                    //第一页
                    if (CollectionUtils.isEmpty(albumList.getAlbums()) && curPage == 1) {
                        getShowEmptyViewEvent().postValue(true);
                        return;
                    }
                    //后续页
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curPage++;
                    }
                    getClearStatusEvent().call();
                    getFinishLoadmoreEvent().postValue(albumList.getAlbums());

                }, e -> {
                    getFinishLoadmoreEvent().call();
                    getShowErrorViewEvent().postValue(curPage == 1);
                    e.printStackTrace();
                });
    }

    public void _getAlbumList(long announcerId) {
        mAnnouncerId = announcerId;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.AID, String.valueOf(announcerId));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAlbumsByAnnouncer(map)
                .doOnSubscribe(disposable -> getShowLoadingViewEvent().postValue(curPage == 1 ? "" : null))
                .subscribe(albumList -> {
                    //第一页
                    if (CollectionUtils.isEmpty(albumList.getAlbums()) && curPage == 1) {
                        getShowEmptyViewEvent().postValue(true);
                        return;
                    }
                    //后续页
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curPage++;
                    }
                    getClearStatusEvent().call();
                    getFinishLoadmoreEvent().postValue(albumList.getAlbums());
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    getShowErrorViewEvent().postValue(curPage == 1);
                    e.printStackTrace();
                });
    }

    public void _getPaidList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIZE));
        mModel.getAllPaidAlbums(map)
                .doOnSubscribe(disposable -> getShowLoadingViewEvent().postValue(curPage == 1 ? "" : null))
                .subscribe(albumList -> {
                    //第一页
                    if (CollectionUtils.isEmpty(albumList.getAlbums()) && curPage == 1) {
                        getShowEmptyViewEvent().postValue(true);
                        return;
                    }
                    //后续页
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curPage++;
                    }
                    getClearStatusEvent().call();
                    getFinishLoadmoreEvent().postValue(albumList.getAlbums());

                }, e -> {
                    getFinishLoadmoreEvent().call();
                    getShowErrorViewEvent().postValue(curPage == 1);
                    e.printStackTrace();
                });
    }
    public void setType(int type) {
        this.type = type;
    }
}
