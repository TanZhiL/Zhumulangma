package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
import com.ximalaya.ting.android.opensdk.model.column.Column;
import com.ximalaya.ting.android.opensdk.model.column.ColumnList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackListV2;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.ISupportFragment;

public class HotViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<BannerV2>> mBannerV2SingleLiveEvent;
    private SingleLiveEvent<List<Album>> mLikeSingleLiveEvent;
    private SingleLiveEvent<List<Album>> mStorySingleLiveEvent;
    private SingleLiveEvent<List<Album>> mBadySingleLiveEvent;
    private SingleLiveEvent<List<Column>> mTopicSingleLiveEvent;
    private SingleLiveEvent<List<Album>> mMusicSingleLiveEvent;
    private SingleLiveEvent<List<Radio>> mRadioSingleLiveEvent;
    private SingleLiveEvent<Void> mRefreshSingleLiveEvent;

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

    public HotViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getBannerList() {
        getBannerListObervable().subscribe(r -> {}, e -> e.printStackTrace());
    }

    public void init() {
        getBannerListObervable()
                .flatMap((Function<BannerV2List, ObservableSource<GussLikeAlbumList>>) bannerV2List ->
                        getGussLikeListObservable())
                .flatMap((Function<GussLikeAlbumList, ObservableSource<AlbumList>>) gussLikeAlbumList ->
                        getHotStoryListObservable())
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getHotBabyListObservable())
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getHotMusicListObservable())
                .flatMap((Function<AlbumList, ObservableSource<RadioList>>) albumList -> getRadioListObservable())
                .flatMap((Function<RadioList, ObservableSource<ColumnList>>) radioList -> getTopicListObservable())
                .doFinally(() -> getRefreshSingleLiveEvent().call())
                .subscribe(columnList -> {}, e -> e.printStackTrace());
    }

    private Observable<BannerV2List> getBannerListObervable() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, "1");
        map.put(DTransferConstants.IMAGE_SCALE, "2");
        return mModel.getCategoryBannersV2(map)
                .doOnNext(bannerV2List -> {
                    List<BannerV2> bannerV2s = bannerV2List.getBannerV2s();
                    Iterator<BannerV2> iterator = bannerV2s.iterator();
                    while (iterator.hasNext()) {
                        BannerV2 next = iterator.next();
                        //阉割听单
                        if (next.getBannerContentType() == 5 || next.getBannerContentType() == 6) {
                            iterator.remove();
                        }
                    }
                    HotViewModel.this.getBannerV2SingleLiveEvent().postValue(bannerV2s);
                });
    }


    public void getGussLikeList() {
        getGussLikeListObservable().subscribe(r -> {}, e -> e.printStackTrace());
    }

    private Observable<GussLikeAlbumList> getGussLikeListObservable() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, "6");
        map.put(DTransferConstants.PAGE, String.valueOf(2));
        return mModel.getGuessLikeAlbum(map)
                .doOnNext(gussLikeAlbumList ->
                        getLikeSingleLiveEvent().postValue(gussLikeAlbumList.getAlbumList()));
    }


    public void getHotStoryList() {
        getHotStoryListObservable().subscribe(r -> {}, e -> e.printStackTrace());
    }

    private Observable<AlbumList> getHotStoryListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "3");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curStoryPage = curStoryPage >= totalStoryPage ? 1 : curStoryPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curStoryPage++));
        return mModel.getAlbumList(map)
                .doOnNext(albumList -> {
                    totalStoryPage = albumList.getTotalPage();
                    getStorySingleLiveEvent().postValue(albumList.getAlbums());
                });
    }

    public void getHotBabyList() {
        getHotBabyListObservable().subscribe(r -> {}, e -> e.printStackTrace());

    }

    private Observable<AlbumList> getHotBabyListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "6");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curBabyPage = curBabyPage >= totalBabyPage ? 1 : curBabyPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curBabyPage++));
        return mModel.getAlbumList(map)
                .doOnNext(albumList -> {
                    totalBabyPage = albumList.getTotalPage();
                    getBadySingleLiveEvent().postValue(albumList.getAlbums());
                });

    }

    public void getHotMusicList() {
        getHotMusicListObservable().subscribe(r -> {}, e -> e.printStackTrace());
    }

    private Observable<AlbumList> getHotMusicListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "2");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "6");
        curMusicPage = curMusicPage >= totalMusicPage ? 1 : curMusicPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curMusicPage++));
        return mModel.getAlbumList(map)
                .doOnNext(albumList -> {
                    totalMusicPage = albumList.getTotalPage();
                    getMusicSingleLiveEvent().postValue(albumList.getAlbums());
                });
    }

    public void getRadioList() {

        getRadioListObservable().subscribe(r -> {}, e -> e.printStackTrace());

    }

    private Observable<RadioList> getRadioListObservable() {

        Map<String, String> map = new HashMap<String, String>();
        //电台类型：1-国家台，2-省市台，3-网络台
        map.put(DTransferConstants.RADIOTYPE, "3");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curRadioPage = curRadioPage >= totalRadioPage ? 1 : curRadioPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curRadioPage++));
        return mModel.getRadios(map)
                .doOnNext(radioList -> {
                    totalRadioPage = radioList.getTotalPage();
                    getRadioSingleLiveEvent().postValue(radioList.getRadios());
                });

    }

    public void getTopicList() {
        getTopicListObservable().subscribe(r -> {}, e -> e.printStackTrace());
    }

    public Observable<ColumnList> getTopicListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        curTopicPage = curTopicPage >= totalTopicPage ? 1 : curTopicPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curTopicPage++));
        return mModel.getColumnList(map)
                .doOnNext(columnList -> {
                    totalTopicPage = columnList.getTotalPage();
                    getTopicSingleLiveEvent().postValue(columnList.getColumns());
                });
    }

    public void play(long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ID, String.valueOf(trackId));
        mModel.searchTrackV2(map)
                .flatMap((Function<SearchTrackListV2, ObservableSource<LastPlayTrackList>>)
                        searchTrackListV2 -> {
                            Map<String, String> map1 = new HashMap<>();
                            map1.put(DTransferConstants.ALBUM_ID, String.valueOf(
                                    searchTrackListV2.getTracks().get(0).getAlbum().getAlbumId()));
                            map1.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
                            return mModel.getLastPlayTracks(map1);
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d ->  postShowLoadingViewEvent(""))
                .doFinally(() -> postShowLoadingViewEvent(null))
                .subscribe(trackList -> {
                    for (int i = 0; i < trackList.getTracks().size(); i++) {
                        if (trackList.getTracks().get(i).getDataId() == trackId) {
                            XmPlayerManager.getInstance(getApplication()).playList(trackList, i);
                            break;
                        }
                    }
                    Object navigation = ARouter.getInstance()
                            .build(AppConstants.Router.Home.F_PLAY_TRACK).navigation();
                    if (null != navigation) {
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
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

    public SingleLiveEvent<Void> getRefreshSingleLiveEvent() {
        return mRefreshSingleLiveEvent = createLiveData(mRefreshSingleLiveEvent);
    }
}
