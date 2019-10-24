package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/11 11:42
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class TrackListViewModel extends BaseRefreshViewModel<ZhumulangmaModel,Track> {
    private SingleLiveEvent<List<Track>> mInitTrackListEvent;
    private int curPage=1;
    private long mAnnouncerId;

    public TrackListViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }
    public void init(){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.AID, String.valueOf(mAnnouncerId));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getTracksByAnnouncer(map)
                .doOnSubscribe(d->getShowInitViewEvent().call())
                .subscribe(trackList -> {
                    if (CollectionUtils.isEmpty(trackList.getTracks())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getClearStatusEvent().call();
                    getInitTrackListEvent().setValue(trackList.getTracks());

                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }
private void getMoreTracks(){
    Map<String, String> map = new HashMap<>();
    map.put(DTransferConstants.AID, String.valueOf(mAnnouncerId));
    map.put(DTransferConstants.PAGE, String.valueOf(curPage));
    mModel.getTracksByAnnouncer(map)
            .subscribe(trackList -> {
                if (!CollectionUtils.isEmpty(trackList.getTracks())) {
                    curPage++;
                }
                getFinishLoadmoreEvent().setValue(trackList.getTracks());
            }, e -> {
                getFinishLoadmoreEvent().call();
                e.printStackTrace();
            });
}

    @Override
    public void onViewLoadmore() {
        getMoreTracks();
    }

    public void play(long albumId, long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d ->   getShowLoadingViewEvent().call())
                .doFinally(() ->  getClearStatusEvent().call())
                .subscribe(trackList -> {
                    for (int i = 0; i < trackList.getTracks().size(); i++) {
                        if(trackList.getTracks().get(i).getDataId()==trackId){
                            XmPlayerManager.getInstance(getApplication()).playList(trackList,i);
                            break;
                        }
                    }
                    RouteUtil.navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }
    public SingleLiveEvent<List<Track>> getInitTrackListEvent() {
        return mInitTrackListEvent =createLiveData(mInitTrackListEvent);
    }

    public void setAnnouncerId(long announcerId) {
        mAnnouncerId = announcerId;
    }
}
