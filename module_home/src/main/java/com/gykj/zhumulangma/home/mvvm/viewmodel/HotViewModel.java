package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
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

public class HotViewModel extends BaseRefreshViewModel<ZhumulangmaModel,Album> {
    private SingleLiveEvent<List<BannerV2>> mBannerV2Event;
    private SingleLiveEvent<List<Album>> mLikesEvent;
    private SingleLiveEvent<List<Album>> mStorysEvent;
    private SingleLiveEvent<List<Album>> mBadysEvent;
    private SingleLiveEvent<List<Album>> mMusicsEvent;
    private SingleLiveEvent<List<Radio>> mRadiosEvent;

    private int totalStoryPage = 1;
    private int totalBabyPage = 1;
    private int totalMusicPage = 1;
    private int totalRadioPage = 1;

    private int curStoryPage = 1;
    private int curBabyPage = 1;
    private int curMusicPage = 1;
    private int curRadioPage = 1;

    public HotViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    @Override
    public void onViewRefresh() {
        curStoryPage = 1;
        curBabyPage = 1;
        curMusicPage = 1;
        curRadioPage = 1;
        init();
    }
    public void init() {
        //获取banner
        getBannerListObervable()
                //猜你喜欢
                .flatMap((Function<BannerV2List, ObservableSource<GussLikeAlbumList>>) bannerV2List ->
                        getGussLikeListObservable())
                //有声书
                .flatMap((Function<GussLikeAlbumList, ObservableSource<AlbumList>>) gussLikeAlbumList ->
                        getHotStoryListObservable())
                //宝贝最爱
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getHotBabyListObservable())
                //音乐
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getHotMusicListObservable())
                //电台
                .flatMap((Function<AlbumList, ObservableSource<RadioList>>) albumList -> getRadioListObservable())
                .doFinally(()->super.onViewRefresh())
                .subscribe(r-> {}, e->
                {   getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
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
                    getBannerV2Event().setValue(bannerV2s);
                });
    }

    private Observable<GussLikeAlbumList> getGussLikeListObservable() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, "6");
        map.put(DTransferConstants.PAGE, String.valueOf(2));
        return mModel.getGuessLikeAlbum(map)
                .doOnNext(gussLikeAlbumList ->
                        getLikesEvent().setValue(gussLikeAlbumList.getAlbumList()));
    }


    public void getHotStoryList() {
        getHotStoryListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(r -> {
                }, e -> e.printStackTrace());
    }

    private Observable<AlbumList> getHotStoryListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "3");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curStoryPage = curStoryPage >= totalStoryPage ? 1 : curStoryPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curStoryPage));
        return mModel.getAlbumList(map)
                .doOnNext(albumList -> {
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curStoryPage++;
                    }
                    totalStoryPage = albumList.getTotalPage();
                    getStorysEvent().setValue(albumList.getAlbums());
                });
    }

    public void getHotBabyList() {
        getHotBabyListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(r -> {
                }, e -> e.printStackTrace());

    }

    private Observable<AlbumList> getHotBabyListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "6");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curBabyPage = curBabyPage >= totalBabyPage ? 1 : curBabyPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curBabyPage));
        return mModel.getAlbumList(map)
                .doOnNext(albumList -> {
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curBabyPage++;
                    }
                    totalBabyPage = albumList.getTotalPage();
                    getBadysEvent().setValue(albumList.getAlbums());
                });

    }

    public void getHotMusicList() {
        getHotMusicListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(r -> {
                }, e -> e.printStackTrace());
    }

    private Observable<AlbumList> getHotMusicListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "2");
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE_SIZE, "6");
        curMusicPage = curMusicPage >= totalMusicPage ? 1 : curMusicPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curMusicPage));
        return mModel.getAlbumList(map)
                .doOnNext(albumList -> {
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curMusicPage++;
                    }
                    totalMusicPage = albumList.getTotalPage();
                    getMusicsEvent().setValue(albumList.getAlbums());
                });
    }

    public void getRadioList() {

        getRadioListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(r -> {
                }, e -> e.printStackTrace());

    }

    private Observable<RadioList> getRadioListObservable() {

        Map<String, String> map = new HashMap<String, String>();
        //电台类型：1-国家台，2-省市台，3-网络台
        map.put(DTransferConstants.RADIOTYPE, "3");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curRadioPage = curRadioPage >= totalRadioPage ? 1 : curRadioPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curRadioPage));
        return mModel.getRadios(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getRadios())) {
                        curRadioPage++;
                    }
                    totalRadioPage = radioList.getTotalPage();
                    getRadiosEvent().setValue(radioList.getRadios());
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
                .doOnSubscribe(d ->   getShowLoadingViewEvent().call())
                .doFinally(() ->  getClearStatusEvent().call())
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
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
    }

    public SingleLiveEvent<List<BannerV2>> getBannerV2Event() {
        return mBannerV2Event = createLiveData(mBannerV2Event);
    }

    public SingleLiveEvent<List<Album>> getLikesEvent() {
        return mLikesEvent = createLiveData(mLikesEvent);
    }

    public SingleLiveEvent<List<Album>> getStorysEvent() {
        return mStorysEvent = createLiveData(mStorysEvent);
    }

    public SingleLiveEvent<List<Album>> getBadysEvent() {
        return mBadysEvent = createLiveData(mBadysEvent);
    }


    public SingleLiveEvent<List<Album>> getMusicsEvent() {
        return mMusicsEvent = createLiveData(mMusicsEvent);
    }

    public SingleLiveEvent<List<Radio>> getRadiosEvent() {
        return mRadiosEvent = createLiveData(mRadiosEvent);
    }

}
