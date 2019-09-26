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
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackListV2;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.ISupportFragment;

public class AnnouncerViewModel extends BaseRefreshViewModel<ZhumulangmaModel,Announcer> {

    private static final String PAGESIZE = "20";
    private SingleLiveEvent<List<BannerV2>> mBannerV2SingleLiveEvent;
    private SingleLiveEvent<List<Announcer>> mAnnouncerSingleLiveEvent;
    private SingleLiveEvent<List<Announcer>> mTopSingleLiveEvent;
    private SingleLiveEvent<Void> mRefreshSingleLiveEvent;


    private int curPage = 1;

    public AnnouncerViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }
    public void init(){
        curPage = 1;
        getBannerListObservable()
                .flatMap((Function<BannerV2List, ObservableSource<AnnouncerList>>) bannerV2List -> getAnnouncerListObservable())
                .doOnSubscribe(d->getShowLoadingViewEvent().postValue(""))
                .doFinally(()->getFinishRefreshEvent().postValue(new ArrayList<>()))
                .subscribe(r-> getClearStatusEvent().call(), e->
                {   getShowErrorViewEvent().postValue(true);
                    e.printStackTrace();
                });
    }

    @Override
    public void onViewRefresh() {
        init();
    }

    @Override
    public void onViewLoadmore() {
        getAnnouncerList();
    }

    private Observable<BannerV2List> getBannerListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "0");
        map.put(DTransferConstants.IMAGE_SCALE, "2");
        map.put(DTransferConstants.CONTAINS_PAID, "true");
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

    public void getTopList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.VCATEGORY_ID, "0");
        map.put(DTransferConstants.CALC_DIMENSION, "1");
        map.put(DTransferConstants.PAGE_SIZE, "3");
        mModel.getAnnouncerList(map).subscribe(announcerList ->
                getTopSingleLiveEvent().postValue(announcerList.getAnnouncerList()), e -> e.printStackTrace());
    }

    public void getAnnouncerList() {
        getAnnouncerListObservable().subscribe(r->{},e->e.printStackTrace());
    }
    private Observable<AnnouncerList> getAnnouncerListObservable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.VCATEGORY_ID, "0");
        map.put(DTransferConstants.CALC_DIMENSION, "1");
        map.put(DTransferConstants.PAGE_SIZE, PAGESIZE);
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
       return mModel.getAnnouncerList(map).doOnNext(announcerList -> {
           if(!CollectionUtils.isEmpty(announcerList.getAnnouncerList())){
               curPage++;
           }
            getFinishLoadmoreEvent().postValue(announcerList.getAnnouncerList());
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
                .doOnSubscribe(d ->  getShowLoadingViewEvent().postValue(""))
                .doFinally(() -> getShowLoadingViewEvent().postValue(null))
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

    public SingleLiveEvent<List<Announcer>> getAnnouncerSingleLiveEvent() {
        return mAnnouncerSingleLiveEvent = createLiveData(mAnnouncerSingleLiveEvent);
    }

    public SingleLiveEvent<List<Announcer>> getTopSingleLiveEvent() {
        return mTopSingleLiveEvent = createLiveData(mTopSingleLiveEvent);
    }

    public SingleLiveEvent<Void> getRefreshSingleLiveEvent() {
        return mRefreshSingleLiveEvent = createLiveData(mRefreshSingleLiveEvent);
    }
}
