package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.util.DateUtil;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.listen.bean.PlayHistoryItem;
import com.gykj.zhumulangma.listen.mvvm.model.HistoryModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/20 13:56
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class HistoryViewModel extends BaseRefreshViewModel<HistoryModel, PlayHistoryItem> {

    private SingleLiveEvent<List<PlayHistoryItem>> mInitHistorysEvent;
    private static final int PAGESIZE = 20;
    private int curPage = 1;

    public HistoryViewModel(@NonNull Application application, HistoryModel model) {
        super(application, model);
    }


    public void init() {

        mModel.getHistory(curPage, PAGESIZE)
                .map(playHistoryBeans -> convertSections(playHistoryBeans))
                .subscribe(historyItems -> {
                    if (CollectionUtils.isEmpty(historyItems)) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getClearStatusEvent().call();
                    getInitHistorysEvent().setValue(historyItems);
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private void getMoreHistory() {
        mModel.getHistory(curPage, PAGESIZE)
                .map(playHistoryBeans -> convertSections(playHistoryBeans))
                .subscribe(playHistorySections -> {
                    if (!CollectionUtils.isEmpty(playHistorySections)) {
                        curPage++;
                    }
                    getFinishLoadmoreEvent().setValue(playHistorySections);
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });
    }

    @Override
    public void onViewLoadmore() {
        getMoreHistory();
    }

    public void playRadio(long albumId, long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
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
                    RouteUtil.navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }

    public void playRadio(String radioId) {
        mModel.getSchedulesSource(radioId)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(schedules ->
                {
                    XmPlayerManager.getInstance(getApplication()).playSchedule(schedules, -1);
                    RouteUtil.navigateTo(AppConstants.Router.Home.F_PLAY_RADIIO);
                }, Throwable::printStackTrace);

    }

    private List<PlayHistoryItem> convertSections(List<PlayHistoryBean> beans) {
        List<PlayHistoryItem> sections = new ArrayList<>();
        Map<String, List<PlayHistoryBean>> map = new LinkedHashMap<>();

        for (PlayHistoryBean bean : beans) {
            List<PlayHistoryBean> playHistoryBeans = map.get(dateCovert(bean.getDatatime()));
            if (playHistoryBeans == null) {
                playHistoryBeans = new ArrayList<>();
                map.put(dateCovert(bean.getDatatime()), playHistoryBeans);
            }
            playHistoryBeans.add(bean);
        }

        Iterator<Map.Entry<String, List<PlayHistoryBean>>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String key = (String) entry.getKey();
            sections.add(new PlayHistoryItem(PlayHistoryItem.HEADER, key));
            List<PlayHistoryBean> list = (List<PlayHistoryBean>) entry.getValue();
            for (PlayHistoryBean bean : list) {
                sections.add(new PlayHistoryItem(bean.getKind().equals(PlayableModel.KIND_TRACK) ?
                        PlayHistoryItem.TRACK : PlayHistoryItem.SCHEDULE, bean));
            }
        }

        return sections;
    }

    public String dateCovert(long datetime) {

        if (datetime > DateUtil.getDayBegin().getTime()) {
            return "今天";
        } else if (datetime > DateUtil.getBeginDayOfYesterday().getTime()) {
            return "昨天";
        } else {
            return "更早";
        }

    }

    public void clear() {
        mModel.clearAll(PlayHistoryBean.class).subscribe();
    }

    public SingleLiveEvent<List<PlayHistoryItem>> getInitHistorysEvent() {
        return mInitHistorysEvent = createLiveData(mInitHistorysEvent);
    }
}
