package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackListV2;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.ISupportFragment;

public class AnnouncerListViewModel extends BaseRefreshViewModel<ZhumulangmaModel,Announcer> {

    private SingleLiveEvent<List<Announcer>> mInitAnnouncersEvent;
    private int curPage = 1;
    private long mCategoryId;

    public AnnouncerListViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    @Override
    public void onViewLoadmore() {
        getMoreAnnouncers();
    }

    public void init() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.VCATEGORY_ID, String.valueOf(mCategoryId));
        map.put(DTransferConstants.CALC_DIMENSION, "1");
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAnnouncerList(map)
                .doOnSubscribe(d->getShowInitViewEvent().call())
                .subscribe(announcerList -> {
                    if (CollectionUtils.isEmpty(announcerList.getAnnouncerList())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getClearStatusEvent().call();
                    getInitAnnouncersEvent().setValue(announcerList.getAnnouncerList());

                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private void getMoreAnnouncers() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.VCATEGORY_ID, String.valueOf(mCategoryId));
        map.put(DTransferConstants.CALC_DIMENSION, "1");
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getAnnouncerList(map)
                .subscribe(announcerList -> {
                    if (!CollectionUtils.isEmpty(announcerList.getAnnouncerList())) {
                        curPage++;
                    }
                    getFinishLoadmoreEvent().setValue(announcerList.getAnnouncerList());
                }, e -> {
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
                        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
    }

    public void setCategoryId(long categoryId) {
        this.mCategoryId = categoryId;
    }

    public SingleLiveEvent<List<Announcer>> getInitAnnouncersEvent() {
        return mInitAnnouncersEvent =createLiveData(mInitAnnouncersEvent);
    }
}
