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
 * <br/>Date: 2019/8/14 9:03
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class RankViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Album> {
    SingleLiveEvent<List<Album>> mInitFreeEvent;
    SingleLiveEvent<List<Album>> mPaidSingleLiveEvent;
    private int curFreePage = 1;
    private int paidPage = 1;
    private String mCid;

    public RankViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, mCid);
        map.put(DTransferConstants.PAGE, String.valueOf(curFreePage));
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        mModel.getAlbumList(map)
                .subscribe(albumList -> {
                    if (CollectionUtils.isEmpty(albumList.getAlbums())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curFreePage++;
                    getClearStatusEvent().call();
                    getInitFreeEvent().setValue(albumList.getAlbums());

                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private void getMoreFreeRanks() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, mCid);
        map.put(DTransferConstants.PAGE, String.valueOf(curFreePage));
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        mModel.getAlbumList(map)
                .subscribe(albumList -> {
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curFreePage++;
                    }
                    getFinishLoadmoreEvent().setValue(albumList.getAlbums());
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });
    }

    @Override
    public void onViewLoadmore() {
        getMoreFreeRanks();
    }

    public void getPaidRank() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE, String.valueOf(paidPage));
        mModel.getPaidAlbumByTag(map)
                .subscribe(albumList -> {
                            paidPage++;
                            getPaidSingleLiveEvent().setValue(albumList.getAlbums());
                        },
                        Throwable::printStackTrace);
    }


    public SingleLiveEvent<List<Album>> getInitFreeEvent() {
        return mInitFreeEvent = createLiveData(mInitFreeEvent);
    }

    public SingleLiveEvent<List<Album>> getPaidSingleLiveEvent() {
        return mPaidSingleLiveEvent = createLiveData(mPaidSingleLiveEvent);
    }

    public void setCid(String cid) {
        if(!cid.equals(mCid)){
            curFreePage=1;
        }
        mCid = cid;
    }
}
