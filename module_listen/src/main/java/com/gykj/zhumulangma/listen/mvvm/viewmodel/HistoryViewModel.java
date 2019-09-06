package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.entity.SectionEntity;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.util.DateUtil;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.gykj.zhumulangma.listen.adapter.HistoryAdapter;
import com.gykj.zhumulangma.listen.mvvm.model.HistoryModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class HistoryViewModel extends BaseViewModel<HistoryModel> {
    private SingleLiveEvent<List<PlayHistorySection>> mHistorysSingleLiveEvent;
    private static final int PAGESIZE = 20;
    private int curPage = 1;

    public HistoryViewModel(@NonNull Application application, HistoryModel model) {
        super(application, model);
    }

    public void _getHistory() {
        mModel.getHistory(curPage, PAGESIZE)
                .observeOn(Schedulers.io())
                .map(playHistoryBeans -> convertSections(playHistoryBeans))
                .doOnSubscribe(d->{
                    if(curPage==1){
                        postShowInitLoadViewEvent(true);
                    }
                })
                .subscribe(playHistorySections -> {
                    postShowInitLoadViewEvent(false);
                    curPage++;
                    getHistorySingleLiveEvent().postValue(playHistorySections);
                }, e -> {
                    e.printStackTrace();
                    postShowNoDataViewEvent(true);
                });
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
                            .build(AppConstants.Router.Home.F_PLAY_TRACK).navigation();
                    if (null != navigation) {
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
    }
    private List<PlayHistorySection> convertSections(List<PlayHistoryBean> beans){
        List<PlayHistorySection> sections=new ArrayList<>();
        Map<String,List<PlayHistoryBean>> map=new LinkedHashMap<>();

        for (PlayHistoryBean bean:beans) {
            List<PlayHistoryBean> playHistoryBeans = map.get(dateCovert(bean.getDatatime()));
            if(playHistoryBeans==null){
                playHistoryBeans=new ArrayList<>();
                map.put(dateCovert(bean.getDatatime()),playHistoryBeans);
            }
            playHistoryBeans.add(bean);
        }

        Iterator<Map.Entry<String, List<PlayHistoryBean>>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry entry = iterator.next();
            String key =(String)entry.getKey();
            sections.add(new PlayHistorySection(true,key));
            List<PlayHistoryBean> list = (List<PlayHistoryBean>) entry.getValue();
            for (PlayHistoryBean bean : list) {
                sections.add(new PlayHistorySection(bean));
            }
        }

        return sections;
    }
    public String dateCovert(long datetime){

        if(datetime > DateUtil.getDayBegin().getTime()){
            return "今天";
        }else if(datetime > DateUtil.getBeginDayOfYesterday().getTime()){
            return "昨天";
        }else {
            return "更早";
        }

    }
    public void clear() {
        mModel.clearAll(PlayHistoryBean.class).subscribe();
    }

    public SingleLiveEvent<List<PlayHistorySection>> getHistorySingleLiveEvent() {
        return mHistorysSingleLiveEvent = createLiveData(mHistorysSingleLiveEvent);
    }


    public class PlayHistorySection extends SectionEntity<PlayHistoryBean> {

        public PlayHistorySection(boolean isHeader, String header) {
            super(isHeader, header);
        }
        public PlayHistorySection(PlayHistoryBean bean) {
            super(bean);
        }
    }
}
