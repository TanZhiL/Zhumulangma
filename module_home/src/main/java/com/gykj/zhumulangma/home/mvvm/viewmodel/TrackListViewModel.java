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
import com.ximalaya.ting.android.opensdk.model.track.AnnouncerTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/11 11:42
 * Email: 1071931588@qq.com
 * Description:
 */
public class TrackListViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<Track>> mTrackListSingleLiveEvent;
    private int curPage=1;
    public TrackListViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }
    public void  getTrack(long announcerId){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.AID, String.valueOf(announcerId));
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getTracksByAnnouncer(map).doOnSubscribe(d->postShowInitLoadViewEvent(curPage==1))
                .subscribe(new Consumer<AnnouncerTrackList>() {
                    @Override
                    public void accept(AnnouncerTrackList trackList) throws Exception {
                        curPage++;
                        postShowInitLoadViewEvent(false);
                        getTrackListSingleLiveEvent().postValue(trackList.getTracks());
                    }
                },e->e.printStackTrace());
    }
    public void play(long albumId,long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> postShowInitLoadViewEvent(true))
                .doFinally(() -> postShowInitLoadViewEvent(false))
                .subscribe(trackList -> {
                    for (int i = 0; i < trackList.getTracks().size(); i++) {
                        if(trackList.getTracks().get(i).getDataId()==trackId){
                            XmPlayerManager.getInstance(getApplication()).playList(trackList,i);
                            break;
                        }
                    }
                    Object navigation = ARouter.getInstance()
                            .build(AppConstants.Router.Home.F_PLAY_TRACK).navigation();
                    if (null != navigation) {
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
    }
    public SingleLiveEvent<List<Track>> getTrackListSingleLiveEvent() {
        return mTrackListSingleLiveEvent=createLiveData(mTrackListSingleLiveEvent);
    }
}
