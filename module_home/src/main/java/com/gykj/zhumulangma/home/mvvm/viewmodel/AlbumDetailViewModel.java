package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.dao.SubscribeBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * Date: 2019/8/14 13:41
 * Email: 1071931588@qq.com
 * Description:
 */
public class AlbumDetailViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Track> {

    //专辑详情
    private SingleLiveEvent<Album> mAlbumSingleLiveEvent;
    //排序或分页后列表
    private SingleLiveEvent<TrackList> mTracksSortSingleLiveEvent;
    //继续播放
    private SingleLiveEvent<TrackList> mPlayTracksSingleLiveEvent;
    //上一次播放的声音
    private SingleLiveEvent<Track> mLastplaySingleLiveEvent;
    //是否订阅
    private SingleLiveEvent<Boolean> mSubscribeSingleLiveEvent;

    private CommonTrackList mCommonTrackList = CommonTrackList.newInstance();
    //上一页
    private int upTrackPage = 0;
    //下一页
    private int curTrackPage = 1;
    private String mSort = "time_desc";
    private String mAlbumId;

    public AlbumDetailViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void unsubscribe(Album album) {
        mModel.remove(SubscribeBean.class, album.getId()).subscribe(aBoolean ->
                getSubscribeSingleLiveEvent().postValue(false), e -> e.printStackTrace());

    }

    public void subscribe(Album album) {
        mModel.insert(new SubscribeBean(album.getId(), album, System.currentTimeMillis()))
                .subscribe(subscribeBean -> getSubscribeSingleLiveEvent().postValue(true), e -> e.printStackTrace());
    }

    /**
     * 初始化数据
     *
     * @param albumId
     */
    public void getAlbumDetail(String albumId) {
        mAlbumId = albumId;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_IDS, albumId);
        //查询是否订阅
        mModel.list(SubscribeBean.class, SubscribeBeanDao.Properties.AlbumId.eq(albumId))
                .doOnNext(subscribeBeans -> getSubscribeSingleLiveEvent().postValue(subscribeBeans.size() > 0))
                //获取专辑详情
                .flatMap((Function<List<SubscribeBean>, ObservableSource<BatchAlbumList>>) subscribeBeans -> mModel.getBatch(map))
                .doOnNext(batchAlbumList -> getAlbumSingleLiveEvent().postValue(
                        batchAlbumList.getAlbums().get(0)))
                //获取第一页声音
                .flatMap((Function<BatchAlbumList, ObservableSource<TrackList>>) batchAlbumList ->
                        getTrackInitObservable(albumId))
                .doOnSubscribe(disposable -> getShowLoadingViewEvent().postValue(""))
                .subscribe(trackList -> {
                    if (CollectionUtils.isEmpty(trackList.getTracks())) {
                        getShowEmptyViewEvent().postValue(true);
                        return;
                    }
                    getClearStatusEvent().call();
                    setOrder(trackList);
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getFinishLoadmoreEvent().postValue(trackList.getTracks());

                }, e -> {
                    getShowErrorViewEvent().postValue(true);
                    e.printStackTrace();
                });
    }

    /**
     * 继续播放操作
     *
     * @param albumId
     */
    public void getPlayTrackList(String albumId) {
        getTrackInitObservable(albumId)
                .doOnSubscribe(disposable -> getShowLoadingViewEvent().postValue(""))
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(trackList -> {
                    setOrder(trackList);
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getPlayTracksSingleLiveEvent().postValue(
                            trackList);
                }, e -> e.printStackTrace());
    }

    /**
     * 排序
     *
     * @param albumId
     * @param sort
     */
    public void getTrackList(String albumId, String sort) {
        if (!sort.equals(mSort)) {
            curTrackPage = 1;
            mSort = sort;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(curTrackPage));
        mModel.getTracks(map)
                .doOnSubscribe(d -> getShowLoadingViewEvent().postValue(""))
                .doFinally(() -> getClearStatusEvent().call())
                .observeOn(Schedulers.io())
                .subscribe(trackList -> {
                    setOrder(trackList);
                    upTrackPage = 0;
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getTracksSortSingleLiveEvent().postValue(
                            trackList);
                }, e -> e.printStackTrace());

    }

    /**
     * 分页查询
     *
     * @param albumId
     * @param page
     */
    public void getTrackList(String albumId, int page) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        mModel.getTracks(map)
                .observeOn(Schedulers.io())
                .doOnSubscribe(d -> getShowLoadingViewEvent().postValue(""))
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(trackList -> {
                    upTrackPage = page;
                    curTrackPage = page;
                    setOrder(trackList);
                    upTrackPage--;
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getTracksSortSingleLiveEvent().postValue(
                            trackList);
                }, e -> e.printStackTrace());

    }

    /**
     * 上拉,下拉更多
     *
     * @param albumId
     * @param isUp
     */
    public void getTrackList(String albumId, boolean isUp) {
        int page;
        if (isUp) {
            page = upTrackPage;
            if (0 == page) {
                getFinishRefreshEvent().postValue(new ArrayList<>());
                return;
            }
        } else {
            page = curTrackPage;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        mModel.getTracks(map)
                .observeOn(Schedulers.io())
                .subscribe(trackList -> {
                    if (isUp) {
                        setUpOrder(trackList);
                        upTrackPage--;
                        mCommonTrackList.updateCommonTrackList(0, trackList);
                        getFinishRefreshEvent().postValue(trackList.getTracks());
                    } else {
                        setOrder(trackList);
                        curTrackPage++;
                        mCommonTrackList.updateCommonTrackList(mCommonTrackList.getTracks().size(), trackList);
                        getFinishLoadmoreEvent().postValue(trackList.getTracks());
                    }
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    getFinishRefreshEvent().call();
                    e.printStackTrace();
                });

    }

    /**
     * 获取默认展示页声音
     *
     * @param albumId
     * @return
     */
    private Observable<TrackList> getTrackInitObservable(String albumId) {
        Observable<TrackList> trackListObservable = mModel.listDesc(PlayHistoryBean.class, 1, 1, PlayHistoryBeanDao.Properties.Datatime
                , PlayHistoryBeanDao.Properties.GroupId.eq(albumId),
                PlayHistoryBeanDao.Properties.Kind.eq(PlayableModel.KIND_TRACK)).doOnNext(playHistoryBeans -> {
            if (!CollectionUtils.isEmpty(playHistoryBeans))
                getLastplaySingleLiveEvent().setValue(playHistoryBeans.get(0).getTrack());
        }).flatMap((Function<List<PlayHistoryBean>, ObservableSource<TrackList>>) playHistoryBeans -> {
            if (null == getLastplaySingleLiveEvent().getValue()) {
                Map<String, String> map = new HashMap<>();
                map.put(DTransferConstants.ALBUM_ID, albumId);
                map.put(DTransferConstants.SORT, mSort);
                map.put(DTransferConstants.PAGE, String.valueOf(1));
                return mModel.getTracks(map).doOnNext(trackList -> {
                    curTrackPage = 1;
                    upTrackPage = 0;
                });
            } else {
                Map<String, String> map = new HashMap<>();
                map.put(DTransferConstants.ALBUM_ID, albumId);
                map.put(DTransferConstants.SORT, mSort);
                map.put(DTransferConstants.TRACK_ID, String.valueOf(getLastplaySingleLiveEvent()
                        .getValue().getDataId()));
                return mModel.getLastPlayTracks(map)
                        .doOnNext(lastPlayTrackList -> {
                            curTrackPage = lastPlayTrackList.getPageid();
                            upTrackPage = lastPlayTrackList.getPageid() - 1;
                        })
                        .map(lastPlayTrackList -> {
                            TrackList trackList = new TrackList();
                            trackList.cloneCommonTrackList(lastPlayTrackList);
                            return trackList;
                        });
            }
        });
        return trackListObservable;
    }

    /**
     * 向下插入序号
     *
     * @param trackList
     */
    private void setOrder(TrackList trackList) {
        List<Track> tracks = trackList.getTracks();
        for (int i = 0; i < tracks.size(); i++) {
            if (mSort.equals("time_desc")) {
                tracks.get(i).setOrderPositionInAlbum(trackList.getTotalCount() - ((curTrackPage - 1) * 20 + i) - 1);
            } else {
                tracks.get(i).setOrderPositionInAlbum((curTrackPage - 1) * 20 + i);
            }
        }
    }

    /**
     * 向上插入序号
     *
     * @param trackList
     */
    private void setUpOrder(TrackList trackList) {
        List<Track> tracks = trackList.getTracks();
        for (int i = 0; i < tracks.size(); i++) {
            if (mSort.equals("time_desc")) {
                tracks.get(i).setOrderPositionInAlbum(trackList.getTotalCount() - ((upTrackPage - 1) * 20 + i) - 1);
            } else {
                tracks.get(i).setOrderPositionInAlbum((upTrackPage - 1) * 20 + i);
            }
        }
    }

    @Override
    public void onViewRefresh() {
        getTrackList(mAlbumId, true);
    }

    @Override
    public void onViewLoadmore() {
        getTrackList(mAlbumId, false);
    }

    public SingleLiveEvent<Album> getAlbumSingleLiveEvent() {
        return mAlbumSingleLiveEvent = createLiveData(mAlbumSingleLiveEvent);
    }

    public SingleLiveEvent<TrackList> getPlayTracksSingleLiveEvent() {
        return mPlayTracksSingleLiveEvent = createLiveData(mPlayTracksSingleLiveEvent);
    }

    public SingleLiveEvent<TrackList> getTracksSortSingleLiveEvent() {
        return mTracksSortSingleLiveEvent = createLiveData(mTracksSortSingleLiveEvent);
    }

    public SingleLiveEvent<Track> getLastplaySingleLiveEvent() {
        return mLastplaySingleLiveEvent = createLiveData(mLastplaySingleLiveEvent);
    }

    public SingleLiveEvent<Boolean> getSubscribeSingleLiveEvent() {
        return mSubscribeSingleLiveEvent = createLiveData(mSubscribeSingleLiveEvent);
    }

    public CommonTrackList getCommonTrackList() {
        return mCommonTrackList;
    }

    public int getCurTrackPage() {
        return curTrackPage;
    }

    public int getUpTrackPage() {
        return upTrackPage;
    }
}
