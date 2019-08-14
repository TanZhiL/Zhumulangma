package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Author: Thomas.
 * Date: 2019/8/14 9:03
 * Email: 1071931588@qq.com
 * Description:
 */
public class RankViewModel extends BaseViewModel<ZhumulangmaModel> {
    SingleLiveEvent<List<Album>> mFreeSingleLiveEvent;
    SingleLiveEvent<List<Album>> mPaidSingleLiveEvent;
    private int freePage=1;
    private int paidPage=1;
    private String mCid;
    private static final int PAGESIZE = 20;
    public RankViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }
    public void getFreeRank(String cid){
        if(!cid.equals(mCid)){
            freePage=1;
            mCid=cid;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, cid);
        map.put(DTransferConstants.PAGE, String.valueOf(freePage));
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE,String.valueOf(PAGESIZE));
        mModel.getAlbumList(map)
                .subscribe(albumList -> {
                            freePage++;
                            getFreeSingleLiveEvent().postValue(albumList.getAlbums());
                        },
                        e->e.printStackTrace());
    }

    public void getPaidRank(){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE, String.valueOf(paidPage));
        map.put(DTransferConstants.PAGE_SIZE,String.valueOf(PAGESIZE));
        mModel.getPaidAlbumByTag(map)
                .subscribe(albumList -> {
                    paidPage++;
                            getPaidSingleLiveEvent().postValue(albumList.getAlbums());
                        },
                        e->e.printStackTrace());
    }


    public SingleLiveEvent<List<Album>> getFreeSingleLiveEvent() {
        return mFreeSingleLiveEvent=createLiveData(mFreeSingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getPaidSingleLiveEvent() {
        return mPaidSingleLiveEvent=createLiveData(mPaidSingleLiveEvent);
    }
}
