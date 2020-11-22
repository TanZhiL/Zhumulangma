package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.net.dto.BannerDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnDetailDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnInfoDTO;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackListV2;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.HOT_COLUMN_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IDS;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IS_PAID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.OPERATION_CATEGORY_ID;

public class HotViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Album> {
    private SingleLiveEvent<List<BannerBean>> mBannerEvent;
    private SingleLiveEvent<List<Album>> mColumnEvent;
    private SingleLiveEvent<List<Album>> mLikesEvent;
    private SingleLiveEvent<List<Album>> mStorysEvent;
    private SingleLiveEvent<List<Album>> mBadysEvent;
    private SingleLiveEvent<List<Album>> mMusicsEvent;
    private SingleLiveEvent<List<Radio>> mRadiosEvent;
    private SingleLiveEvent<String> mColumnNameEvent;

    private int totalStoryPage = 1;
    private int totalBabyPage = 1;
    private int totalMusicPage = 1;
    private int totalColumnPage = 1;

    private int curStoryPage = 1;
    private int curBabyPage = 1;
    private int curMusicPage = 1;
    private int curColumnPage = 1;

    public HotViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    @Override
    public void onViewRefresh() {
        curStoryPage = 1;
        curBabyPage = 1;
        curMusicPage = 1;
        curColumnPage = 1;
        init();
    }

    public void init() {
        //获取banner
        getBannerListObervable()
                //猜你喜欢
                .flatMap((Function<BannerDTO, ObservableSource<GussLikeAlbumList>>) bannerV2List ->
                        getGussLikeListObservable())
                //有声书
                .flatMap((Function<GussLikeAlbumList, ObservableSource<AlbumList>>) gussLikeAlbumList ->
                        getHotStoryListObservable())
                //宝贝最爱
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getHotBabyListObservable())
                //音乐
                .flatMap((Function<AlbumList, ObservableSource<AlbumList>>) albumList -> getHotMusicListObservable())
                .flatMap((Function<AlbumList, ObservableSource<ColumnDetailDTO<Album>>>) albumList -> getColumnListObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnInfoDTO>>) albumList -> getColumnInfoObservable())
                .doFinally(() -> super.onViewRefresh())
                .subscribe(r -> getClearStatusEvent().call(), e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private Observable<BannerDTO> getBannerListObervable() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(3 + new Random().nextInt(5)));
        map.put(OPERATION_CATEGORY_ID, "1");
        map.put(IS_PAID, "0");
        return mModel.getBanners(map)
                .doOnNext((BannerDTO bannerV2List) -> {
                    getBannerEvent().setValue(bannerV2List.getBanners());
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
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
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
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

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
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
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
    public void getColumnList() {
        getColumnListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }
    private Observable<ColumnDetailDTO<Album>> getColumnListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, HOT_COLUMN_ID);
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curColumnPage = curColumnPage >= totalColumnPage ? 1 : curColumnPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curColumnPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curColumnPage++;
                    }
                    totalColumnPage = radioList.getTotalPage();
                    getColumnEvent().setValue(radioList.getColumns());
                });

    }
    private Observable<ColumnInfoDTO> getColumnInfoObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, HOT_COLUMN_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getColumnNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }
    public void playTrack(long trackId) {
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

    public void playRadio(Radio radio) {
        /*mModel.getSchedulesSource(radio)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(schedules ->
                {
                 //   XmPlayerManager.getInstance(getApplication()).playSchedule(schedules, -1);
                    RouterUtil.navigateTo(Constants.Router.Home.F_PLAY_RADIIO);
                }, Throwable::printStackTrace);*/
    }

    public SingleLiveEvent<List<BannerBean>> getBannerEvent() {
        return mBannerEvent = createLiveData(mBannerEvent);
    }
    public SingleLiveEvent<List<Album>> getColumnEvent() {
        return mColumnEvent = createLiveData(mColumnEvent);
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

    public SingleLiveEvent<String> getColumnNameEvent() {
        return mColumnNameEvent = createLiveData(mColumnNameEvent);
    }
}
