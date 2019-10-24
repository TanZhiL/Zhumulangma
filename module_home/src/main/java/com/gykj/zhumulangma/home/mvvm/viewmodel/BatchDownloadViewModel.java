package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class BatchDownloadViewModel extends BaseRefreshViewModel<ZhumulangmaModel, Track> {

    private SingleLiveEvent<TrackList> mInitTracksEvent;

    private CommonTrackList mCommonTrackList = CommonTrackList.newInstance();

    private int upTrackPage = 0;
    private int curTrackPage = 1;
    public static final int PAGESIEZ = 50;
    private String mSort;
    private long mAlbumId;

    public BatchDownloadViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(mAlbumId));
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE, String.valueOf(curTrackPage));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIEZ));
        mModel.getTracks(map)
                .doOnSubscribe(disposable -> getShowInitViewEvent().call())
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
     * 分页
     * @param page
     */
    public void getTrackList(int page) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(mAlbumId));
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        map.put(DTransferConstants.SORT, mSort);
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIEZ));
        mModel.getTracks(map)
                .doOnSubscribe(d -> getShowLoadingViewEvent().call())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(trackList -> {
                    upTrackPage = page;
                    curTrackPage = page;
                    setOrder(trackList);
                    upTrackPage--;
                    curTrackPage++;
                    mCommonTrackList.cloneCommonTrackList(trackList);
                    getInitTracksEvent().setValue(
                            trackList);
                }, Throwable::printStackTrace);

    }

    /**
     * 上拉或者下拉
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
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(PAGESIEZ));

        mModel.getTracks(map)
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

    @Override
    public void onViewRefresh() {
        getTrackList(true);
    }

    @Override
    public void onViewLoadmore() {
        getTrackList(false);
    }

    /**
     * 向下插入序号
     *
     * @param trackList
     */
    private void setOrder(TrackList trackList) {
        List<Track> tracks = trackList.getTracks();
        for (int i = 0; i < tracks.size(); i++) {
            tracks.get(i).setOrderPositionInAlbum(trackList.getTotalCount() - ((curTrackPage - 1) * PAGESIEZ + i) - 1);
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
            tracks.get(i).setOrderPositionInAlbum(trackList.getTotalCount() - ((upTrackPage - 1) * PAGESIEZ + i) - 1);
        }
    }

    public SingleLiveEvent<TrackList> getInitTracksEvent() {
        return mInitTracksEvent = createLiveData(mInitTracksEvent);
    }

    public int getCurTrackPage() {
        return curTrackPage;
    }

    public int getUpTrackPage() {
        return upTrackPage;
    }

    public void setSort(String sort) {
        mSort = sort;
    }

    public void setAlbumId(long albumId) {
        mAlbumId = albumId;
    }
}
