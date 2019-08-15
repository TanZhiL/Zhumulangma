package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConstants;

public class FineViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<List<BannerV2>> mBannerV2SingleLiveEvent;
    private SingleLiveEvent<List<Album>> mDailySingleLiveEvent;
    private SingleLiveEvent<List<Album>> mBookSingleLiveEvent;
    private SingleLiveEvent<List<Album>> mClassRoomSingleLiveEvent;


    private int totalDailyPage = 1;
    private int totalBookPage = 1;
    private int totalClassRoomPage = 1;

    private int curDailyPage = 1;
    private int curBookPage = 1;
    private int curClassRoomPage = 1;

    public FineViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getBannerList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "0");
        map.put(DTransferConstants.IMAGE_SCALE, "2");
        map.put(DTransferConstants.CONTAINS_PAID,"true");
        mModel.getCategoryBannersV2(map)
                .subscribe(bannerV2List ->
                                getBannerV2SingleLiveEvent().postValue(bannerV2List.getBannerV2s())
                        , throwable -> throwable.printStackTrace());
    }

    public void getDailyList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curDailyPage = curDailyPage >= totalDailyPage ? 1 : curDailyPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curDailyPage++));
        mModel.getPaidAlbumByTag(map)
                .subscribe(albumList -> {
                    totalDailyPage = albumList.getTotalPage();
                    getDailySingleLiveEvent().postValue(albumList.getAlbums());
                }, e -> e.printStackTrace());
    }


    public void getBookList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TAG_NAME, "有声书");
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curBookPage = curBookPage >= totalBookPage ? 1 : curBookPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curBookPage++));
        mModel.getPaidAlbumByTag(map)
                .subscribe(albumList -> {
                    totalBookPage = albumList.getTotalPage();
                    getBookSingleLiveEvent().postValue(albumList.getAlbums());
                }, e -> e.printStackTrace());
    }


    public void getClassRoomList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TAG_NAME, "精品-小课");
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curClassRoomPage = curClassRoomPage >= totalClassRoomPage ? 1 : curClassRoomPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curClassRoomPage++));
        mModel.getPaidAlbumByTag(map)
                .subscribe(albumList -> {
                    totalClassRoomPage = albumList.getTotalPage();
                    getClassRoomSingleLiveEvent().postValue(albumList.getAlbums());
                }, e -> e.printStackTrace());
    }

    public SingleLiveEvent<List<BannerV2>> getBannerV2SingleLiveEvent() {
        return mBannerV2SingleLiveEvent = createLiveData(mBannerV2SingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getDailySingleLiveEvent() {
        return mDailySingleLiveEvent = createLiveData(mDailySingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getBookSingleLiveEvent() {
        return mBookSingleLiveEvent = createLiveData(mBookSingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getClassRoomSingleLiveEvent() {
        return mClassRoomSingleLiveEvent = createLiveData(mClassRoomSingleLiveEvent);
    }

}