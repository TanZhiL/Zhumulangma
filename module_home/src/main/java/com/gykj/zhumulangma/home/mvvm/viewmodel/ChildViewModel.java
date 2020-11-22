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
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
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

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_DHSJ_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_GXJD_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_JDGS_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_JZZQ_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_QZEG_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IDS;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IS_PAID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.OPERATION_CATEGORY_ID;

public class ChildViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Album> {

    private SingleLiveEvent<List<BannerBean>> mBannerV2Event;
    private SingleLiveEvent<List<Album>> mJDGSEvent;
    private SingleLiveEvent<List<Album>> mDHSJEvent;
    private SingleLiveEvent<List<Album>> mGXJDEvent;
    private SingleLiveEvent<List<Album>> mSingEvent;
    private SingleLiveEvent<List<Album>> mParentEvent;
    private SingleLiveEvent<String> mJDGSNameEvent;
    private SingleLiveEvent<String> mDHSJNameEvent;
    private SingleLiveEvent<String> mGXJDNameEvent;
    private SingleLiveEvent<String> mQZEGNameEvent;
    private SingleLiveEvent<String> mJZZQNameEvent;


    private int totalJDGSPage = 1;
    private int totalDHSJPage = 1;
    private int totalGXJDPage = 1;
    private int totalSingPage = 1;
    private int totalParentPage = 1;

    private int curJDGSPage = 1;
    private int curDHSJPage = 1;
    private int curGXJDPage = 1;
    private int curSingPage = 1;
    private int curParentPage = 1;

    public ChildViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    @Override
    public void onViewRefresh() {
        curJDGSPage = 1;
        curDHSJPage = 1;
        curGXJDPage = 1;
        curSingPage = 1;
        curParentPage = 1;
        init();
    }

    public void init() {
        //获取banner
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(3 + new Random().nextInt(5)));
        map.put(OPERATION_CATEGORY_ID, "6");
        map.put(IS_PAID, "0");
        mModel.getBanners(map)
                .doOnNext((BannerDTO bannerV2List) -> getBannerV2Event().setValue(bannerV2List.getBanners()))
                .flatMap((Function<BannerDTO, ObservableSource<ColumnDetailDTO<Album>>>) bannerV2List -> getJDGSObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnDetailDTO<Album>>>) albumList -> getDHSJObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnDetailDTO<Album>>>) albumList -> getGXJDObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnDetailDTO<Album>>>) albumList -> getSingObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnDetailDTO<Album>>>) albumList -> getParentObservable())
                .flatMap((Function<ColumnDetailDTO<Album>,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getJDGSNameObservable())
                .flatMap((Function<ColumnInfoDTO,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getDHSJNameObservable())
                .flatMap((Function<ColumnInfoDTO,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getGXJDNameObservable())
                .flatMap((Function<ColumnInfoDTO,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getQZEGNameObservable())
                .flatMap((Function<ColumnInfoDTO,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getJZZQNameObservable())
                .doFinally(() -> super.onViewRefresh())
                .subscribe(r -> getClearStatusEvent().call(), e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
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
                    RouteHelper.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }

    public void getJDGSList() {
        getJDGSObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }


    private Observable<ColumnInfoDTO> getJDGSNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CHILD_JDGS_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getJDGSNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }
    

    private Observable<ColumnInfoDTO> getDHSJNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CHILD_DHSJ_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getDHSJNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }
    private Observable<ColumnInfoDTO> getGXJDNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CHILD_GXJD_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getGXJDNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }
    private Observable<ColumnInfoDTO> getQZEGNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CHILD_QZEG_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getQZEGNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }

    private Observable<ColumnInfoDTO> getJZZQNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CHILD_JZZQ_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getJZZQNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }

    private Observable<ColumnDetailDTO<Album>> getJDGSObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CHILD_JDGS_ID);
        map.put(DTransferConstants.PAGE_SIZE, "6");
        curJDGSPage = curJDGSPage >= totalJDGSPage ? 1 : curJDGSPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curJDGSPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curJDGSPage++;
                    }
                    totalJDGSPage = radioList.getTotalPage();
                    getJDGSEvent().setValue(radioList.getColumns());
                });

    }
    public void getDHSJList() {
        getDHSJObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getDHSJObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CHILD_DHSJ_ID);
        map.put(DTransferConstants.PAGE_SIZE, "6");
        curDHSJPage = curDHSJPage >= totalDHSJPage ? 1 : curDHSJPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curDHSJPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curDHSJPage++;
                    }
                    totalDHSJPage = radioList.getTotalPage();
                    getDHSJEvent().setValue(radioList.getColumns());
                });

    }
    public void getGXJDList() {
        getGXJDObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getGXJDObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CHILD_GXJD_ID);
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curGXJDPage = curGXJDPage >= totalGXJDPage ? 1 : curGXJDPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curGXJDPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curGXJDPage++;
                    }
                    totalGXJDPage = radioList.getTotalPage();
                    getGXJDEvent().setValue(radioList.getColumns());
                });

    }
    public void getSingList() {
        getSingObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getSingObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CHILD_QZEG_ID);
        map.put(DTransferConstants.PAGE_SIZE, "6");
        curSingPage = curSingPage >= totalSingPage ? 1 : curSingPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curSingPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curSingPage++;
                    }
                    totalSingPage = radioList.getTotalPage();
                    getSingEvent().setValue(radioList.getColumns());
                });

    } 
    public void getParentList() {
        getParentObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getParentObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CHILD_JZZQ_ID);
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curParentPage = curParentPage >= totalParentPage ? 1 : curParentPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curParentPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curParentPage++;
                    }
                    totalParentPage = radioList.getTotalPage();
                    getParentEvent().setValue(radioList.getColumns());
                });

    }
    public SingleLiveEvent<List<BannerBean>> getBannerV2Event() {
        return mBannerV2Event = createLiveData(mBannerV2Event);
    }

    public SingleLiveEvent<List<Album>> getJDGSEvent() {
        return mJDGSEvent = createLiveData(mJDGSEvent);
    }

    public SingleLiveEvent<List<Album>> getDHSJEvent() {
        return mDHSJEvent = createLiveData(mDHSJEvent);
    }

    public SingleLiveEvent<List<Album>> getGXJDEvent() {
        return mGXJDEvent = createLiveData(mGXJDEvent);
    }

    public SingleLiveEvent<List<Album>> getSingEvent() {
        return mSingEvent = createLiveData(mSingEvent);
    }
    public SingleLiveEvent<List<Album>> getParentEvent() {
        return mParentEvent = createLiveData(mParentEvent);
    }
    public SingleLiveEvent<String> getDHSJNameEvent() {
        return mDHSJNameEvent = createLiveData(mDHSJNameEvent);
    }
    public SingleLiveEvent<String> getJDGSNameEvent() {
        return mJDGSNameEvent = createLiveData(mJDGSNameEvent);
    }
    public SingleLiveEvent<String> getGXJDNameEvent() {
        return mGXJDNameEvent = createLiveData(mGXJDNameEvent);
    }
    public SingleLiveEvent<String> getQZEGNameEvent() {
        return mQZEGNameEvent = createLiveData(mQZEGNameEvent);
    }
    public SingleLiveEvent<String> getJZZQNameEvent() {
        return mJZZQNameEvent = createLiveData(mJZZQNameEvent);
    }
}
