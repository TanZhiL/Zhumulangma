package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackListV2;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;

public class FineViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Album> {

    private SingleLiveEvent<List<BannerV2>> mBannerV2Event;
    private SingleLiveEvent<List<Album>> mDailysEvent;
    private SingleLiveEvent<List<Album>> mBooksEvent;
    private SingleLiveEvent<List<Album>> mClassRoomsEvent;


    private int totalDailyPage = 1;
    private int totalBookPage = 1;
    private int totalClassRoomPage = 1;

    private int curDailyPage = 1;
    private int curBookPage = 1;
    private int curClassRoomPage = 1;

    public FineViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    @Override
    public void onViewRefresh() {
        curDailyPage = 1;
        curBookPage = 1;
        curClassRoomPage = 1;
        init();
    }

    public void init() {
        //获取banner
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "4");
        map.put(DTransferConstants.IMAGE_SCALE, "2");
        //是否需要输出付费内容：true-是；false-否；（默认不输出付费内容）
//        map.put(DTransferConstants.CONTAINS_PAID,"true");
        mModel.getCategoryBannersV2(map)
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
                    getBannerV2Event().setValue(bannerV2s);
                })
                //每日优选
                .flatMap((Function<BannerV2List, ObservableSource<AlbumList>>) bannerV2List -> getDailyListObservable())
                //有声书
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getBookListObservable())
                //小课堂
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getClassRoomListObservable())
                .doFinally(() -> super.onViewRefresh())
                .subscribe(r -> getClearStatusEvent().call(), e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    public void getDailyList() {
        getDailyListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
    }

    private Observable<AlbumList> getDailyListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curDailyPage = curDailyPage >= totalDailyPage ? 1 : curDailyPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curDailyPage));
        return mModel.getPaidAlbumByTag(map)
                .doOnNext(albumList -> {
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curDailyPage++;
                    }
                    totalDailyPage = albumList.getTotalPage();
                    getDailysEvent().setValue(albumList.getAlbums());
                });
    }

    public void getBookList() {
        getBookListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
    }

    private Observable<AlbumList> getBookListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TAG_NAME, "有声书");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curBookPage = curBookPage >= totalBookPage ? 1 : curBookPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curBookPage));
        return mModel.getPaidAlbumByTag(map)
                .doOnNext(albumList -> {
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curBookPage++;
                    }
                    totalBookPage = albumList.getTotalPage();
                    getBooksEvent().setValue(albumList.getAlbums());
                });
    }

    public void getClassRoomList() {
        getClassRoomListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
    }

    private Observable<AlbumList> getClassRoomListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TAG_NAME, "精品-小课");
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curClassRoomPage = curClassRoomPage >= totalClassRoomPage ? 1 : curClassRoomPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curClassRoomPage));
        return mModel.getPaidAlbumByTag(map)
                .doOnNext(albumList -> {
                    if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                        curClassRoomPage++;
                    }
                    totalClassRoomPage = albumList.getTotalPage();
                    getClassRoomsEvent().setValue(albumList.getAlbums());
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
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(trackList -> {
                    for (int i = 0; i < trackList.getTracks().size(); i++) {
                        if (trackList.getTracks().get(i).getDataId() == trackId) {
                            XmPlayerManager.getInstance(getApplication()).playList(trackList, i);
                            break;
                        }
                    }
                    RouterUtil.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }

    public SingleLiveEvent<List<BannerV2>> getBannerV2Event() {
        return mBannerV2Event = createLiveData(mBannerV2Event);
    }

    public SingleLiveEvent<List<Album>> getDailysEvent() {
        return mDailysEvent = createLiveData(mDailysEvent);
    }

    public SingleLiveEvent<List<Album>> getBooksEvent() {
        return mBooksEvent = createLiveData(mBooksEvent);
    }

    public SingleLiveEvent<List<Album>> getClassRoomsEvent() {
        return mClassRoomsEvent = createLiveData(mClassRoomsEvent);
    }

}
