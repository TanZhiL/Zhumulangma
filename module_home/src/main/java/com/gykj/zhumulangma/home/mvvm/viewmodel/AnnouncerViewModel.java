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
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackListV2;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IS_PAID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.SCOPE;

public class AnnouncerViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Announcer> {

    private SingleLiveEvent<List<BannerBean>> mBannerV2Event;
    private SingleLiveEvent<List<Announcer>> mInitAnnouncerEvent;
    private int curPage = 1;

    public AnnouncerViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        //获取banner
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(3 + new Random().nextInt(5)));
        map.put(SCOPE, "2");
        map.put(IS_PAID, "0");
        mModel.getBanners(map)
                .doOnNext(bannerV2List ->
                        getBannerV2Event().setValue(bannerV2List.getBanners()))
                //获取主播列表
                .flatMap((Function<BannerDTO, ObservableSource<AnnouncerList>>) bannerV2List -> {
                    Map<String, String> map2 = new HashMap<String, String>();
                    map2.put(DTransferConstants.VCATEGORY_ID, "0");
                    map2.put(DTransferConstants.CALC_DIMENSION, "1");
                    map2.put(DTransferConstants.PAGE, String.valueOf(curPage));
                    return mModel.getAnnouncerList(map2);
                })
                //刷新时需要恢复刷新状态
                .doFinally(()->super.onViewRefresh())
                .subscribe(announcerList -> {
                    getClearStatusEvent().call();
                    if (!CollectionUtils.isEmpty(announcerList.getAnnouncerList())) {
                        curPage++;
                    }
                    getInitAnnouncerEvent().setValue(announcerList.getAnnouncerList());
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    @Override
    public void onViewRefresh() {
        curPage = 1;
        init();
    }

    @Override
    public void onViewLoadmore() {
        getMoreAnnouncers();
    }

    private void getMoreAnnouncers() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.VCATEGORY_ID, "0");
        map.put(DTransferConstants.CALC_DIMENSION, "1");
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAnnouncerList(map).subscribe(announcerList -> {
            if (!CollectionUtils.isEmpty(announcerList.getAnnouncerList())) {
                curPage++;
            }
            getFinishLoadmoreEvent().setValue(announcerList.getAnnouncerList());
        },e -> {
            getFinishLoadmoreEvent().call();
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


    public SingleLiveEvent<List<BannerBean>> getBannerV2Event() {
        return mBannerV2Event = createLiveData(mBannerV2Event);
    }

    public SingleLiveEvent<List<Announcer>> getInitAnnouncerEvent() {
        return mInitAnnouncerEvent = createLiveData(mInitAnnouncerEvent);
    }


}
