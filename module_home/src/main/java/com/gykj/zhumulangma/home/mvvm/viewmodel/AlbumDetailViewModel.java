package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.db.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.db.SubscribeBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.AnnouncerListByIds;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class AlbumDetailViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Track> {

    //专辑详情
    private SingleLiveEvent<Album> mAlbumEvent;
    //初始化时声音列表
    private SingleLiveEvent<TrackList> mInitTracksEvent;
    //排序或分页后列表
    private SingleLiveEvent<TrackList> mTracksSortEvent;
    //继续播放
    private SingleLiveEvent<TrackList> mPlayTracksEvent;
    //上一次播放的声音
    private SingleLiveEvent<Track> mLastplayEvent;
    //是否订阅
    private SingleLiveEvent<Boolean> mSubscribeEvent;

    private SingleLiveEvent<Announcer> mAnnouncerEvent;
    //播放列表
    private CommonTrackList mCommonTrackList = CommonTrackList.newInstance();
    //上一页页码
    private int upTrackPage = 0;
    //下一页页码
    private int curTrackPage = 1;
    //排序号
    private String mSort;
    //专辑Id
    private long mAlbumId;

    public AlbumDetailViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }


    public void setArguments(long albumId,String sort) {
        mAlbumId = albumId;
        mSort = sort;
    }

    /**
     * 初始化数据
     */
    public void init() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_IDS, String.valueOf(mAlbumId));
        //查询是否订阅
        mModel.list(SubscribeBean.class, SubscribeBeanDao.Properties.AlbumId.eq(mAlbumId))
                .doOnNext(subscribeBeans -> getSubscribeEvent().setValue(subscribeBeans.size() > 0))
                //获取专辑详情
                .flatMap((Function<List<SubscribeBean>, ObservableSource<BatchAlbumList>>) subscribeBeans -> mModel.getBatch(map))
                .doOnNext(batchAlbumList -> getAlbumEvent().setValue(batchAlbumList.getAlbums().get(0)))
                //获取主播
                .flatMap((Function<BatchAlbumList, ObservableSource<AnnouncerListByIds>>) albumList ->
                        getAnnouncer(albumList.getAlbums().get(0).getAnnouncer().getAnnouncerId()))
                .doOnNext(announcerListByIds ->
                        getAnnouncerEvent().setValue(announcerListByIds.getAnnouncers().get(0)))
                //获取第一页声音
                .flatMap((Function<Object, ObservableSource<TrackList>>) o ->
                        AlbumDetailViewModel.this.getTrackInitObservable())
                .subscribe(trackList -> {
                    if (CollectionUtils.isEmpty(trackList.getTracks())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    getClearStatusEvent().call();
                    setOrder(trackList);
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getInitTracksEvent().setValue(trackList);

                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    /**
     * 继续播放操作
     */
    public void getPlayTrackList() {
        getTrackInitObservable()
                .doOnSubscribe(disposable -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(trackList -> {
                    setOrder(trackList);
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getPlayTracksEvent().setValue(trackList);
                }, Throwable::printStackTrace);
    }

    /**
     * 排序
     *
     * @param sort
     */
    public void getTrackList(String sort) {
        if (!sort.equals(mSort)) {
            curTrackPage = 1;
            mSort = sort;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(mAlbumId));
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(curTrackPage));
        mModel.getTracks(map)
                .flatMap(insertHistory())
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(trackList -> {
                    setOrder(trackList);
                    upTrackPage = 0;
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getTracksSortEvent().setValue(trackList);
                }, Throwable::printStackTrace);

    }


    /**
     * 分页查询
     *
     * @param page
     */
    public void getTrackList(int page) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(mAlbumId));
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        mModel.getTracks(map)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .flatMap(insertHistory())
                .subscribe(trackList -> {
                    upTrackPage = page;
                    curTrackPage = page;
                    setOrder(trackList);
                    upTrackPage--;
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getTracksSortEvent().setValue(trackList);
                }, Throwable::printStackTrace);

    }

    /**
     * 上拉,下拉更多
     *
     * @param isUp
     */
    private void getTrackList(boolean isUp) {
        int page;
        if (isUp) {
            page = upTrackPage;
            if (0 == page) {
                super.onViewRefresh();
                return;
            }
        } else {
            page = curTrackPage;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(mAlbumId));
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        mModel.getTracks(map)
                .flatMap(insertHistory())
                .subscribe(trackList -> {
                    if (isUp) {
                        setUpOrder(trackList);
                        upTrackPage--;
                        mCommonTrackList.updateCommonTrackList(0, trackList);
                        getFinishRefreshEvent().setValue(trackList.getTracks());
                    } else {
                        setOrder(trackList);
                        curTrackPage++;
                        mCommonTrackList.updateCommonTrackList(mCommonTrackList.getTracks().size(), trackList);
                        getFinishLoadmoreEvent().setValue(trackList.getTracks());
                    }
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    getFinishRefreshEvent().call();
                    e.printStackTrace();
                });

    }

    /**
     * 获取主播信息
     *
     * @param announcerId
     * @return
     */
    private Observable<AnnouncerListByIds> getAnnouncer(long announcerId) {
        Map<String, String> map = new HashMap<>();
        map.put("ids", String.valueOf(announcerId));
        //主播详情
        return mModel.getAnnouncersBatch(map);
    }

    /**
     * 获取默认展示页声音数据源
     *
     * @return
     */
    private Observable<TrackList> getTrackInitObservable() {
        //查询播放历史
        return mModel.listDesc(PlayHistoryBean.class, 1, 1, PlayHistoryBeanDao.Properties.Datatime
                , PlayHistoryBeanDao.Properties.GroupId.eq(mAlbumId), PlayHistoryBeanDao.Properties.Kind.eq(PlayableModel.KIND_TRACK))
                .doOnNext(playHistoryBeans -> {
                    if (!CollectionUtils.isEmpty(playHistoryBeans)) {
                        getLastplayEvent().setValue(playHistoryBeans.get(0).getTrack());
                    }
                })
                .flatMap((Function<List<PlayHistoryBean>, ObservableSource<TrackList>>) playHistoryBeans -> {
                    if (null == getLastplayEvent().getValue()) {
                        //没有历史记录
                        Map<String, String> map = new HashMap<>();
                        map.put(DTransferConstants.ALBUM_ID, String.valueOf(mAlbumId));
                        map.put(DTransferConstants.SORT, mSort);
                        map.put(DTransferConstants.PAGE, String.valueOf(1));
                        return mModel.getTracks(map).doOnNext(trackList -> {
                            curTrackPage = 1;
                            upTrackPage = 0;
                        });
                    } else {
                        //有历史记录
                        Map<String, String> map = new HashMap<>();
                        map.put(DTransferConstants.ALBUM_ID, String.valueOf(mAlbumId));
                        map.put(DTransferConstants.SORT, mSort);
                        map.put(DTransferConstants.TRACK_ID, String.valueOf(getLastplayEvent()
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
                })
                .flatMap(insertHistory());
    }


    /**
     * 获取历史播放进度,利用source字段保存
     *
     * @return
     */
    @NonNull
    private Function<TrackList, ObservableSource<TrackList>> insertHistory() {
        return trackList -> {
            if (CollectionUtils.isEmpty(trackList.getTracks())) {
                return Observable.just(trackList);
            }
            return (ObservableSource<TrackList>) Observable.just(trackList)
                    //先扩散
                    .flatMapIterable((Function<TrackList, Iterable<Track>>) CommonTrackList::getTracks)
                    .flatMap((Function<Track, ObservableSource<List<PlayHistoryBean>>>) track -> {
                        //初始化数据
                        track.setSource(0);
                        return mModel.list(PlayHistoryBean.class, PlayHistoryBeanDao.Properties.SoundId.eq(track.getDataId()),
                                PlayHistoryBeanDao.Properties.Kind.eq(track.getKind()));
                    })
                    .observeOn(Schedulers.io())
                    .doOnNext(historyBeans -> {
                        if (!CollectionUtils.isEmpty(historyBeans)) {
                            CollectionUtils.find(trackList.getTracks(), item ->
                                    item.getDataId() == historyBeans.get(0).getSoundId())
                                    .setSource(historyBeans.get(0).getPercent());
                        }
                    })
                    //合并后发射
                    .buffer(trackList.getTracks().size())
                    .map(lists -> trackList)
                    .observeOn(AndroidSchedulers.mainThread());
        };
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
    /**
     * 取消订阅
     *
     * @param album
     */
    public void unsubscribe(Album album) {
        mModel.remove(SubscribeBean.class, album.getId()).subscribe(aBoolean ->
                getSubscribeEvent().setValue(false), Throwable::printStackTrace);

    }

    /**
     * 订阅
     *
     * @param album
     */
    public void subscribe(Album album) {
        mModel.insert(new SubscribeBean(album.getId(), album, System.currentTimeMillis()))
                .subscribe(subscribeBean -> getSubscribeEvent().setValue(true), Throwable::printStackTrace);
    }

    @Override
    public void onViewRefresh() {
        getTrackList(true);
    }

    @Override
    public void onViewLoadmore() {
        getTrackList(false);
    }


    public SingleLiveEvent<Album> getAlbumEvent() {
        return mAlbumEvent = createLiveData(mAlbumEvent);
    }

    public SingleLiveEvent<TrackList> getInitTracksEvent() {
        return mInitTracksEvent = createLiveData(mInitTracksEvent);
    }

    public SingleLiveEvent<TrackList> getPlayTracksEvent() {
        return mPlayTracksEvent = createLiveData(mPlayTracksEvent);
    }

    public SingleLiveEvent<TrackList> getTracksSortEvent() {
        return mTracksSortEvent = createLiveData(mTracksSortEvent);
    }

    public SingleLiveEvent<Track> getLastplayEvent() {
        return mLastplayEvent = createLiveData(mLastplayEvent);
    }

    public SingleLiveEvent<Boolean> getSubscribeEvent() {
        return mSubscribeEvent = createLiveData(mSubscribeEvent);
    }

    public SingleLiveEvent<Announcer> getAnnouncerEvent() {
        return mAnnouncerEvent = createLiveData(mAnnouncerEvent);
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
