package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.bean.SearchHistoryBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.dao.SearchHistoryBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.home.bean.SearchSuggestItem;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.AlbumResult;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/13 11:10
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class SearchViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<SearchHistoryBean> mInsertHistoryEvent;
    private SingleLiveEvent<List<HotWord>> mHotWordsEvent;
    private SingleLiveEvent<List<SearchHistoryBean>> mHistorySingleLiveEvent;
    private SingleLiveEvent<List<SearchSuggestItem>> mWordsSingleLiveEvent;
    private SingleLiveEvent<Track> mLastplaySingleLiveEvent;

    public SearchViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);

    }

    public void getHotWords() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, String.valueOf(20));
        mModel.getHotWords(map)
                .subscribe(hotWordList -> getHotWordsEvent().setValue(
                        hotWordList.getHotWordList()), e -> e.printStackTrace());
    }

    public void clearHistory() {

        mModel.clearAll(SearchHistoryBean.class)
                .subscribe(aBoolean -> {

                }, e -> e.printStackTrace());
    }

    public void insertHistory(SearchHistoryBean entity) {
        mModel.list(SearchHistoryBean.class)
                .filter(historyBeanList -> !historyBeanList.contains(entity))
                .flatMap((Function<List<SearchHistoryBean>, ObservableSource<SearchHistoryBean>>)
                        historyBeanList -> mModel.insert(entity))
                .subscribe(bean -> getInsertHistoryEvent().setValue(entity), e -> e.printStackTrace());
    }

    public void refreshHistory() {
        mModel.listDesc(SearchHistoryBean.class, 0, 0, SearchHistoryBeanDao.Properties.Datatime)
                .subscribe(searchHistoryBeans -> getHistorySingleLiveEvent().setValue(searchHistoryBeans));
    }

    public void getHistory() {

        mModel.listDesc(SearchHistoryBean.class, 0, 0, SearchHistoryBeanDao.Properties.Datatime)
                .doOnNext(searchHistoryBeans -> getHistorySingleLiveEvent().setValue(searchHistoryBeans))
                .flatMap((Function<List<SearchHistoryBean>, ObservableSource<HotWordList>>) searchHistoryBeans -> {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put(DTransferConstants.TOP, String.valueOf(20));
                    return mModel.getHotWords(map1);
                })
                .subscribe(hotWordList -> {
                    getClearStatusEvent().call();
                    getHotWordsEvent().setValue(hotWordList.getHotWordList());
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    public void getSuggestWord(String q) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, q);
        mModel.getSuggestWord(map)
                .map(suggestWords -> {
                    List<SearchSuggestItem> suggestItems = new ArrayList<>();
                    for (AlbumResult album : suggestWords.getAlbumList()) {
                        suggestItems.add(new SearchSuggestItem(album));
                    }
                    for (QueryResult queryResult : suggestWords.getKeyWordList()) {
                        suggestItems.add(new SearchSuggestItem(queryResult));
                    }
                    return suggestItems;
                })
                .subscribe(suggestItems -> getWordsSingleLiveEvent().setValue(suggestItems), e -> e.printStackTrace());
    }

    private long trackId = -1;

    public void play(String albumId) {
        trackId = -1;
        mModel.listDesc(PlayHistoryBean.class, 1, 1, PlayHistoryBeanDao.Properties.Datatime
                , PlayHistoryBeanDao.Properties.GroupId.eq(albumId),
                PlayHistoryBeanDao.Properties.Kind.eq(PlayableModel.KIND_TRACK)).doOnNext(playHistoryBeans -> {
            if (!CollectionUtils.isEmpty(playHistoryBeans))
                trackId = playHistoryBeans.get(0).getTrack().getDataId();
        }).flatMap((Function<List<PlayHistoryBean>, ObservableSource<TrackList>>) playHistoryBeans -> {
            if (-1 == trackId) {
                Map<String, String> map = new HashMap<>();
                map.put(DTransferConstants.ALBUM_ID, albumId);
                map.put(DTransferConstants.SORT, "time_desc");
                map.put(DTransferConstants.PAGE, String.valueOf(1));
                return mModel.getTracks(map);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put(DTransferConstants.ALBUM_ID, albumId);
                map.put(DTransferConstants.SORT, "time_desc");
                map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
                return mModel.getLastPlayTracks(map)
                        .map(lastPlayTrackList -> {
                            TrackList trackList = new TrackList();
                            trackList.cloneCommonTrackList(lastPlayTrackList);
                            return trackList;
                        });
            }
        }).subscribe(trackList -> {
            int playIndex = 0;
            if (trackId != -1) {
                for (int i = 0; i < trackList.getTracks().size(); i++) {
                    if (trackList.getTracks().get(i).getDataId() == trackId) {
                        playIndex = i;
                        break;
                    }
                }
            }
            XmPlayerManager.getInstance(getApplication()).playList(trackList, playIndex);
        }, e -> e.printStackTrace());
    }


    public SingleLiveEvent<List<HotWord>> getHotWordsEvent() {
        return mHotWordsEvent = createLiveData(mHotWordsEvent);
    }

    public SingleLiveEvent<List<SearchHistoryBean>> getHistorySingleLiveEvent() {
        return mHistorySingleLiveEvent = createLiveData(mHistorySingleLiveEvent);
    }

    public SingleLiveEvent<List<SearchSuggestItem>> getWordsSingleLiveEvent() {
        return mWordsSingleLiveEvent = createLiveData(mWordsSingleLiveEvent);
    }

    public SingleLiveEvent<Track> getLastplaySingleLiveEvent() {
        return mLastplaySingleLiveEvent = createLiveData(mLastplaySingleLiveEvent);
    }

    public SingleLiveEvent<SearchHistoryBean> getInsertHistoryEvent() {
        return mInsertHistoryEvent = createLiveData(mInsertHistoryEvent);
    }

}
