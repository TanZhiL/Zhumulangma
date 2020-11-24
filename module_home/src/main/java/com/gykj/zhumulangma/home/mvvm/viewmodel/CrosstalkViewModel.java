package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.amap.api.location.AMapLocation;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.net.dto.BannerDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnDetailDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnInfoDTO;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.mvvm.model.RadioModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
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

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CROSSTALK_DJJX_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CROSSTALK_PSLB_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CROSSTALK_RMTJ_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CROSSTALK_XPHC_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CROSSTALK_XSJX_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IDS;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IS_PAID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.OPERATION_CATEGORY_ID;

public class CrosstalkViewModel extends BaseRefreshViewModel<RadioModel, Album> {

    private SingleLiveEvent<List<BannerBean>> mBannerEvent;
    private SingleLiveEvent<List<PlayHistoryBean>> mHistorysEvent;
    private SingleLiveEvent<List<Radio>> mLocalsEvent;
    private SingleLiveEvent<List<Radio>> mTopsEvent;
    private SingleLiveEvent<String> mCityNameEvent;
    private SingleLiveEvent<String> mTitleEvent;
    private SingleLiveEvent<Void> mStartLocationEvent;

    private String mCityCode;

    private SingleLiveEvent<List<Album>> mDailyEvent;
    private SingleLiveEvent<List<Album>> mDajiaEvent;
    private SingleLiveEvent<List<Album>> mZhangguiEvent;
    private SingleLiveEvent<List<Album>> mYoungEvent;
    private SingleLiveEvent<List<Album>> mDujiaEvent;
    private SingleLiveEvent<String> mDajiaNameEvent;
    private SingleLiveEvent<String> mDailyNameEvent;
    private SingleLiveEvent<String> mZhangguiNameEvent;
    private SingleLiveEvent<String> mYoungNameEvent;
    private SingleLiveEvent<String> mDujiaNameEvent;

    private int totalDajiaPage = 1;
    private int totalZhangguiPage = 1;
    private int totalYoungPage = 1;
    private int totalDailyPage = 1;
    private int totalDujiaPage = 1;

    private int curDajiaPage = 1;
    private int curZhangguiPage = 1;
    private int curYoungPage = 1;
    private int curDailyPage = 1;
    private int curDujiaPage = 1;

    public CrosstalkViewModel(@NonNull Application application, RadioModel model) {
        super(application, model);
    }

    @Override
    public void onViewRefresh() {
        curDajiaPage = 1;
        curDailyPage = 1;
        curYoungPage = 1;
        curZhangguiPage = 1;
        curDujiaPage = 1;
        init();
    }

    public void getHistory() {
        mModel.getHistory(1, 5)
                .subscribe(historyBeans -> getHistorysEvent().setValue(historyBeans), Throwable::printStackTrace);
    }

    public void init(String cityCode) {
        mCityCode = cityCode;
     /*   mModel.getHistory(1, 5)
                .doOnNext(historyBeans -> getHistorysEvent().setValue(historyBeans))
                .flatMap((Function<List<PlayHistoryBean>, ObservableSource<RadioList>>) historyBeans -> getLocalListObservable(mCityCode))
                .flatMap((Function<RadioList, ObservableSource<RadioList>>) radioList -> getTopListObservable())
                .doFinally(() -> super.onViewRefresh())
                .subscribe(r -> getClearStatusEvent().call(), e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });*/
    }

    private Observable<RadioList> getLocalListObservable(String cityCode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CITY_CODE, cityCode);
        map.put(DTransferConstants.PAGE_SIZE, "5");
        map.put(DTransferConstants.PAGE, String.valueOf(1));
        return mModel.getRadiosByCity(map)
                .doOnNext(radioList -> {
                    getLocalsEvent().setValue(radioList.getRadios());
                });
    }


    public void getTopList() {
        getTopListObservable().subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
    }

    private Observable<RadioList> getTopListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_COUNT, "5");

        return mModel.getRankRadios(map)
                .doOnNext(radioList -> getTopsEvent().setValue(radioList.getRadios()));
    }

    public void init() {
        getBannerListObervable()
                .flatMap((Function<BannerDTO, ObservableSource<ColumnDetailDTO<Album>>>) gussLikeAlbumList ->
                        getDailyListObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnDetailDTO<Album>>>) albumList ->
                        getDajiaListObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnDetailDTO<Album>>>) albumList ->
                        getZhangguiListObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnDetailDTO<Album>>>) albumList ->
                        getYoungListObservable())
                .flatMap((Function<ColumnDetailDTO<Album>, ObservableSource<ColumnDetailDTO<Album>>>) albumList ->
                        getDujiaListObservable())
                .flatMap((Function<ColumnDetailDTO<Album>,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getColumnNameObservable())
              /*  .flatMap((Function<ColumnInfoDTO,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getDajiaNameObservable())
                .flatMap((Function<ColumnInfoDTO,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getZhangguiNameObservable())
                .flatMap((Function<ColumnInfoDTO,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getYoungNameObservable())
                .flatMap((Function<ColumnInfoDTO,ObservableSource<ColumnInfoDTO>>) albumList ->
                        getDujiaNameObservable())*/
                .doFinally(() -> super.onViewRefresh())
                .subscribe(r -> getClearStatusEvent().call(), e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    /**
     * 保存定位结果
     *
     * @param aMapLocation
     */
    public void saveLocation(AMapLocation aMapLocation) {
        if (!TextUtils.isEmpty(aMapLocation.getAdCode()) && !mCityCode.equals(aMapLocation.getAdCode().substring(0, 4))) {
            String city = aMapLocation.getCity();
            String province = aMapLocation.getProvince();
            mModel.putSP(Constants.SP.CITY_CODE, aMapLocation.getAdCode().substring(0, 4))
                    .doOnSubscribe(this)
                    .flatMap((Function<String, ObservableSource<String>>) aBoolean ->
                            mModel.putSP(Constants.SP.CITY_NAME, city.substring(0, city.length() - 1)))
                    .flatMap((Function<String, ObservableSource<String>>) aBoolean ->
                            mModel.putSP(Constants.SP.PROVINCE_CODE, aMapLocation.getAdCode().substring(0, 3) + "000"))
                    .flatMap((Function<String, ObservableSource<String>>) aBoolean ->
                            mModel.putSP(Constants.SP.PROVINCE_NAME, province.substring(0, province.length() - 1)))
                    .flatMap((Function<String, ObservableSource<String>>) aBoolean ->
                            mModel.getSPString(Constants.SP.CITY_NAME))
                    .flatMap((Function<String, ObservableSource<String>>) s -> {
                        getTitleEvent().setValue(s);
                        return mModel.getSPString(Constants.SP.CITY_CODE);
                    })
                    .subscribe(this::init, Throwable::printStackTrace);
        }
    }

    private Observable<BannerDTO> getBannerListObervable() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(3 + new Random().nextInt(5)));
        map.put(OPERATION_CATEGORY_ID, "12");
        map.put(IS_PAID, "0");
        return mModel.getBanners(map)
                .doOnNext((BannerDTO bannerV2List) -> getBannerEvent().setValue(bannerV2List.getBanners()));
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
                    RouteHelper.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }


    private Observable<ColumnInfoDTO> getColumnNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        StringBuilder sb = new StringBuilder();
        sb.append(CROSSTALK_RMTJ_ID).append(",");
        sb.append(CROSSTALK_XSJX_ID).append(",");
        sb.append(CROSSTALK_PSLB_ID).append(",");
        sb.append(CROSSTALK_XPHC_ID).append(",");
        sb.append(CROSSTALK_DJJX_ID);
        map.put(IDS, sb.toString());
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> {
                    getDailyNameEvent().setValue(radioList.getColumns().get(0).getTitle());
                    getDajiaNameEvent().setValue(radioList.getColumns().get(1).getTitle());
                    getZhangguiNameEvent().setValue(radioList.getColumns().get(2).getTitle());
                    getYoungNameEvent().setValue(radioList.getColumns().get(3).getTitle());
                    getDujiaNameEvent().setValue(radioList.getColumns().get(4).getTitle());
                });

    }

  /*  private Observable<ColumnInfoDTO> getDajiaNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CROSSTALK_XSJX_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getDajiaNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }
    private Observable<ColumnInfoDTO> getZhangguiNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CROSSTALK_PSLB_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getZhangguiNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }

    private Observable<ColumnInfoDTO> getYoungNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CROSSTALK_XPHC_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getYoungNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }

    private Observable<ColumnInfoDTO> getDujiaNameObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(IDS, CROSSTALK_DJJX_ID);
        return mModel.getColumnInfo(map)
                .doOnNext(radioList -> getDujiaNameEvent().setValue(radioList.getColumns().get(0).getTitle()));

    }*/
    public void getDailyList() {
        getDailyListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getDailyListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CROSSTALK_RMTJ_ID);
        map.put(DTransferConstants.PAGE_SIZE, "6");
        curDailyPage = curDailyPage > totalDailyPage ? 1 : curDailyPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curDailyPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curDailyPage++;
                    }
                    totalDailyPage = radioList.getTotalPage();
                    getDailyEvent().setValue(radioList.getColumns());
                });

    }

    public void getDajiaList() {
        getDajiaListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getDajiaListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CROSSTALK_XSJX_ID);
        map.put(DTransferConstants.PAGE_SIZE, "6");
        curDajiaPage = curDajiaPage > totalDajiaPage ? 1 : curDajiaPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curDajiaPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curDajiaPage++;
                    }
                    totalDajiaPage = radioList.getTotalPage();
                    getDajiaEvent().setValue(radioList.getColumns());
                });

    }

    public void getZhangguiList() {
        getZhangguiListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getZhangguiListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CROSSTALK_PSLB_ID);
        map.put(DTransferConstants.PAGE_SIZE, "6");
        curZhangguiPage = curZhangguiPage > totalZhangguiPage ? 1 : curZhangguiPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curZhangguiPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curZhangguiPage++;
                    }
                    totalZhangguiPage = radioList.getTotalPage();
                    getZhangguiEvent().setValue(radioList.getColumns());
                });

    }

    public void getYoungList() {
        getYoungListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getYoungListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CROSSTALK_XPHC_ID);
        map.put(DTransferConstants.PAGE_SIZE, "5");
        curYoungPage = curYoungPage >= totalYoungPage ? 1 : curYoungPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curYoungPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curYoungPage++;
                    }
                    totalYoungPage = radioList.getTotalPage();
                    getYoungEvent().setValue(radioList.getColumns());
                });

    }
    
    public void getDujiaList() {
        getDujiaListObservable().doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);

    }

    private Observable<ColumnDetailDTO<Album>> getDujiaListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, CROSSTALK_DJJX_ID);
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curDujiaPage = curDujiaPage >= totalDujiaPage ? 1 : curDujiaPage;
        map.put(DTransferConstants.PAGE, String.valueOf(curDujiaPage));
        return mModel.getBrowseAlbumColumn(map)
                .doOnNext(radioList -> {
                    if (!CollectionUtils.isEmpty(radioList.getColumns())) {
                        curDujiaPage++;
                    }
                    totalDujiaPage = radioList.getTotalPage();
                    getDujiaEvent().setValue(radioList.getColumns());
                });

    }
    public SingleLiveEvent<List<BannerBean>> getBannerEvent() {
        return mBannerEvent = createLiveData(mBannerEvent);
    }

    public SingleLiveEvent<List<PlayHistoryBean>> getHistorysEvent() {
        return mHistorysEvent = createLiveData(mHistorysEvent);
    }

    public SingleLiveEvent<List<Album>> getZhangguiEvent() {
        return mZhangguiEvent = createLiveData(mZhangguiEvent);
    }

    public SingleLiveEvent<List<Radio>> getLocalsEvent() {
        return mLocalsEvent = createLiveData(mLocalsEvent);
    }

    public SingleLiveEvent<List<Radio>> getTopsEvent() {
        return mTopsEvent = createLiveData(mTopsEvent);
    }

    public SingleLiveEvent<String> getCityNameEvent() {
        return mCityNameEvent = createLiveData(mCityNameEvent);
    }

    public SingleLiveEvent<Void> getStartLocationEvent() {
        return mStartLocationEvent = createLiveData(mStartLocationEvent);
    }

    public SingleLiveEvent<String> getTitleEvent() {
        return mTitleEvent = createLiveData(mTitleEvent);
    }

    public SingleLiveEvent<List<Album>> getDailyEvent() {
        return mDailyEvent = createLiveData(mDailyEvent);
    }

    public SingleLiveEvent<List<Album>> getYoungEvent() {
        return mYoungEvent = createLiveData(mYoungEvent);
    }

    public SingleLiveEvent<List<Album>> getDajiaEvent() {
        return mDajiaEvent = createLiveData(mDajiaEvent);
    }
    public SingleLiveEvent<List<Album>> getDujiaEvent() {
        return mDujiaEvent = createLiveData(mDujiaEvent);
    }
    public SingleLiveEvent<String> getDajiaNameEvent() {
        return mDajiaNameEvent = createLiveData(mDajiaNameEvent);
    }
    public SingleLiveEvent<String> getDailyNameEvent() {
        return mDailyNameEvent = createLiveData(mDailyNameEvent);
    }
    public SingleLiveEvent<String> getZhangguiNameEvent() {
        return mZhangguiNameEvent = createLiveData(mZhangguiNameEvent);
    }
    public SingleLiveEvent<String> getYoungNameEvent() {
        return mYoungNameEvent = createLiveData(mYoungNameEvent);
    }
    public SingleLiveEvent<String> getDujiaNameEvent() {
        return mDujiaNameEvent = createLiveData(mDujiaNameEvent);
    }
}

