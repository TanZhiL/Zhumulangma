package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.home.activity.AlbumListActivity;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 10:21
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class AlbumListViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Album> {

    private SingleLiveEvent<List<Album>> mInitAlbumsEvent;
    private int curPage = 1;
    private int mType;
    private long mAnnouncerId;

    public AlbumListViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        Map<String, String> map = new HashMap<String, String>();
        switch (mType) {
            case AlbumListActivity.LIKE:
                //猜你喜欢
                map.put(DTransferConstants.LIKE_COUNT, "50");
                map.put(DTransferConstants.PAGE, String.valueOf(1));
                mModel.getGuessLikeAlbum(map)
                        .subscribe(gussLikeAlbumList -> {
                            if (CollectionUtils.isEmpty(gussLikeAlbumList.getAlbumList())) {
                                getShowEmptyViewEvent().call();
                                return;
                            }
                            getClearStatusEvent().call();
                            getInitAlbumsEvent().setValue(gussLikeAlbumList.getAlbumList());
                            //下拉显示没有更多数据
                            super.onViewLoadmore();
                        }, e -> {
                            getShowErrorViewEvent().call();
                            e.printStackTrace();
                        });
                break;
            case AlbumListActivity.PAID:
                //付费精品
                map.put(DTransferConstants.PAGE, String.valueOf(curPage));
                mModel.getAllPaidAlbums(map)
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
                break;
            case AlbumListActivity.ANNOUNCER:
                //主播专辑
                map.put(DTransferConstants.AID, String.valueOf(mAnnouncerId));
                map.put(DTransferConstants.PAGE, String.valueOf(curPage));
                mModel.getAlbumsByAnnouncer(map)
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
                break;
            default:
                //分类专辑
                map.put(DTransferConstants.CATEGORY_ID, String.valueOf(mType));
                map.put(DTransferConstants.CALC_DIMENSION, "3");
                map.put(DTransferConstants.PAGE, String.valueOf(curPage));
                mModel.getAlbumList(map)
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
    }


    private void getMoreAlbumsByType() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, String.valueOf(mType));
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAlbumList(map)
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

    private void getMoreAlbumsByAnnouncer() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.AID, String.valueOf(mAnnouncerId));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAlbumsByAnnouncer(map)
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

    private void getMorePaids() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAllPaidAlbums(map)
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
        if (mType == AlbumListActivity.PAID) {
            getMorePaids();
        } else if (mType == AlbumListActivity.ANNOUNCER) {
            getMoreAlbumsByAnnouncer();
        } else {
            getMoreAlbumsByType();
        }
    }

    public SingleLiveEvent<List<Album>> getInitAlbumsEvent() {
        return mInitAlbumsEvent = createLiveData(mInitAlbumsEvent);
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setAnnouncerId(long announcerId) {
        mAnnouncerId = announcerId;
    }
}
