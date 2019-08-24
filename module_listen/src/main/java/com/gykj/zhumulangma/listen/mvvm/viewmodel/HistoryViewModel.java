package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/8/20 13:56
 * Email: 1071931588@qq.com
 * Description:
 */
public class HistoryViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<PlayHistoryBean>> mHistorysSingleLiveEvent;
    private static final int PAGESIZE = 20;
    private int curPage = 1;

    public HistoryViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getHistory() {
        mModel.listDesc(PlayHistoryBean.class, curPage, PAGESIZE, PlayHistoryBeanDao.Properties.Datatime)
                .subscribe(playHistoryBeans -> {
                    curPage++;
                    getHistorySingleLiveEvent().postValue(playHistoryBeans);
                }, e -> e.printStackTrace());
    }

    public void play(String albumId, Track track) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.TRACK_ID, String.valueOf(track.getDataId()));
        mModel.getLastPlayTracks(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackList -> {
                    XmPlayerManager.getInstance(getApplication()).playList(trackList,
                            trackList.getTracks().indexOf(track));
                    Object navigation = ARouter.getInstance()
                            .build(AppConstants.Router.Player.F_PLAY_TRACK).navigation();
                    if (null != navigation) {
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Player.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
    }


    public void clear() {
        mModel.clearAll(PlayHistoryBean.class).subscribe();
    }

    public SingleLiveEvent<List<PlayHistoryBean>> getHistorySingleLiveEvent() {
        return mHistorysSingleLiveEvent = createLiveData(mHistorysSingleLiveEvent);
    }
}
