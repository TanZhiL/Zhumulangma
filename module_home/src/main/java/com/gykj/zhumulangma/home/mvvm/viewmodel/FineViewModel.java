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
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
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

public class FineViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<List<BannerV2>> mBannerV2SingleLiveEvent;
    private SingleLiveEvent<List<Album>> mDailySingleLiveEvent;
    private SingleLiveEvent<List<Album>> mBookSingleLiveEvent;
    private SingleLiveEvent<List<Album>> mClassRoomSingleLiveEvent;
    private SingleLiveEvent<Void> mRefreshSingleLiveEvent;


    private int totalDailyPage = 1;
    private int totalBookPage = 1;
    private int totalClassRoomPage = 1;

    private int curDailyPage = 1;
    private int curBookPage = 1;
    private int curClassRoomPage = 1;

    public FineViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }


    public void init(){
        getBannerListObservable()
                .flatMap((Function<BannerV2List, ObservableSource<AlbumList>>) bannerV2List -> getDailyListObservable())
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getBookListObservable())
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getClassRoomListObservable())
                .doFinally(()->getRefreshSingleLiveEvent().call())
                .subscribe(r->{},e->e.printStackTrace());
    }

    public void getBannerList() {
        getBannerListObservable().subscribe(r -> {
        }, e -> e.printStackTrace());
    }

    private Observable<BannerV2List> getBannerListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "4");
        map.put(DTransferConstants.IMAGE_SCALE, "2");
        //是否需要输出付费内容：true-是；false-否；（默认不输出付费内容）
//        map.put(DTransferConstants.CONTAINS_PAID,"true");
        return mModel.getCategoryBannersV2(map)
                .doOnNext(bannerV2List ->
                {
                    List<BannerV2> bannerV2s = bannerV2List.getBannerV2s();
                    Iterator<BannerV2> iterator = bannerV2s.iterator();
                    while (iterator.hasNext()) {
                        BannerV2 next = iterator.next();
                        if (next.getBannerContentType() == 5 || next.getBannerContentType() == 6) {
                            iterator.remove();
                        }
                    }
                    getBannerV2SingleLiveEvent().postValue(bannerV2s);
                });
    }

    public void getDailyList() {
        getDailyListObservable().subscribe(r -> {
        }, e -> e.printStackTrace());
    }

    private Observable<AlbumList> getDailyListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curDailyPage = curDailyPage >= totalDailyPage ? 1 : curDailyPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curDailyPage++));
        return mModel.getPaidAlbumByTag(map)
                .doOnNext(albumList -> {
                    totalDailyPage = albumList.getTotalPage();
                    getDailySingleLiveEvent().postValue(albumList.getAlbums());
                });
    }

    public void getBookList() {
        getBookListObservable().subscribe(r -> {
        }, e -> e.printStackTrace());
    }

    private Observable<AlbumList> getBookListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TAG_NAME, "有声书");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curBookPage = curBookPage >= totalBookPage ? 1 : curBookPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curBookPage++));
        return mModel.getPaidAlbumByTag(map)
                .doOnNext(albumList -> {
                    totalBookPage = albumList.getTotalPage();
                    getBookSingleLiveEvent().postValue(albumList.getAlbums());
                });
    }

    public void getClassRoomList() {
        getClassRoomListObservable().subscribe(r -> {
        }, e -> e.printStackTrace());
    }

    private Observable<AlbumList> getClassRoomListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TAG_NAME, "精品-小课");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curClassRoomPage = curClassRoomPage >= totalClassRoomPage ? 1 : curClassRoomPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curClassRoomPage++));
        return mModel.getPaidAlbumByTag(map)
                .doOnNext(albumList -> {
                    totalClassRoomPage = albumList.getTotalPage();
                    getClassRoomSingleLiveEvent().postValue(albumList.getAlbums());
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
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
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
    public SingleLiveEvent<Void> getRefreshSingleLiveEvent() {
        return mRefreshSingleLiveEvent = createLiveData(mRefreshSingleLiveEvent);
    }
}
