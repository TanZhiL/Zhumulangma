package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.home.mvvm.model.HomeModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
import com.ximalaya.ting.android.opensdk.model.column.Column;
import com.ximalaya.ting.android.opensdk.model.column.ColumnList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class HotViewModel extends BaseViewModel<HomeModel> {
    SingleLiveEvent<List<BannerV2>> mBannerV2SingleLiveEvent;
    SingleLiveEvent<List<Album>> mLikeSingleLiveEvent;
    SingleLiveEvent<List<Album>> mStorySingleLiveEvent;
    SingleLiveEvent<List<Album>> mBadySingleLiveEvent;
    SingleLiveEvent<List<Column>> mTopicSingleLiveEvent;
    SingleLiveEvent<List<Album>> mMusicSingleLiveEvent;
    SingleLiveEvent<List<Radio>> mRadioSingleLiveEvent;

    private int totalStoryPage = 1;
    private int totalBabyPage = 1;
    private int totalTopicPage = 1;
    private int totalMusicPage = 1;
    private int totalRadioPage = 1;

    private int curStoryPage = 1;
    private int curBabyPage = 1;
    private int curTopicPage = 1;
    private int curMusicPage = 1;
    private int curRadioPage = 1;

    public HotViewModel(@NonNull Application application, HomeModel model) {
        super(application, model);
    }

    public void getBannerList() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, "0");
        map.put(DTransferConstants.IMAGE_SCALE, "2");
        mModel.getCategoryBannersV2(map)
                .subscribe(bannerV2List ->
                                getBannerV2SingleLiveEvent().postValue(bannerV2List.getBannerV2s())
                        , e -> e.printStackTrace());
    }

    public void getGussLikeList() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, "6");
        map.put(DTransferConstants.PAGE, String.valueOf(2));
        mModel.getGuessLikeAlbum(map)
                .subscribe(gussLikeAlbumList ->
                                getLikeSingleLiveEvent().postValue(gussLikeAlbumList.getAlbumList())
                        , e -> e.printStackTrace());
    }


    public void getHotStoryList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "3");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curStoryPage = curStoryPage >= totalStoryPage ? 1 : curStoryPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curStoryPage++));
        mModel.getAlbumList(map)
                .subscribe(albumList -> {
                            totalStoryPage = albumList.getTotalPage();
                            getStorySingleLiveEvent().postValue(albumList.getAlbums());
                        }
                        , e -> e.printStackTrace());
    }


    public void getHotBabyList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "6");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curBabyPage = curBabyPage >= totalBabyPage ? 1 : curBabyPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curBabyPage++));
        mModel.getAlbumList(map)
                .subscribe(albumList -> {
                            totalBabyPage = albumList.getTotalPage();
                            getBadySingleLiveEvent().postValue(albumList.getAlbums());
                        }
                        , e -> e.printStackTrace());

    }

    public void getHotMusicList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "2");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curMusicPage = curMusicPage >= totalMusicPage ? 1 : curMusicPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curMusicPage++));
        mModel.getAlbumList(map)
                .subscribe(albumList -> {
                            totalMusicPage = albumList.getTotalPage();
                            getMusicSingleLiveEvent().postValue(albumList.getAlbums());
                        }
                        , e -> e.printStackTrace());
    }

    public void getRadioList() {

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, "3");
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curRadioPage = curRadioPage >= totalRadioPage ? 1 : curRadioPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curRadioPage++));
        mModel.getRadios(map)
                .subscribe(radioList -> {
                            totalRadioPage = radioList.getTotalPage();
                            getRadioSingleLiveEvent().postValue(radioList.getRadios());
                        }
                        , e -> e.printStackTrace());

    }

    public void getTopicList() {
        Map<String, String> map = new HashMap<String, String>();
        curTopicPage = curTopicPage >= totalTopicPage ? 1 : curTopicPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curTopicPage++));
        mModel.getColumnList(map)
                .subscribe(columnList -> {
                            totalTopicPage = columnList.getTotalPage();
                            getTopicSingleLiveEvent().postValue(columnList.getColumns());
                        }
                        , e -> e.printStackTrace());
    }

    public SingleLiveEvent<List<BannerV2>> getBannerV2SingleLiveEvent() {
        return mBannerV2SingleLiveEvent = createLiveData(mBannerV2SingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getLikeSingleLiveEvent() {
        return mLikeSingleLiveEvent = createLiveData(mLikeSingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getStorySingleLiveEvent() {
        return mStorySingleLiveEvent = createLiveData(mStorySingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getBadySingleLiveEvent() {
        return mBadySingleLiveEvent = createLiveData(mBadySingleLiveEvent);
    }

    public SingleLiveEvent<List<Column>> getTopicSingleLiveEvent() {
        return mTopicSingleLiveEvent = createLiveData(mTopicSingleLiveEvent);
    }

    public SingleLiveEvent<List<Album>> getMusicSingleLiveEvent() {
        return mMusicSingleLiveEvent = createLiveData(mMusicSingleLiveEvent);
    }

    public SingleLiveEvent<List<Radio>> getRadioSingleLiveEvent() {
        return mRadioSingleLiveEvent = createLiveData(mRadioSingleLiveEvent);
    }
}
