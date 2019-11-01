package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.FavoriteBean;
import com.gykj.zhumulangma.common.dao.FavoriteBeanDao;
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
 * <br/>Date: 2019/9/20 10:01
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class FavoriteViewModel extends BaseRefreshViewModel<ZhumulangmaModel, FavoriteBean> {
    private SingleLiveEvent<List<FavoriteBean>> mInitFavoritesEvent;
    private SingleLiveEvent<Track> mLikeEvent;
    private SingleLiveEvent<Track> mUnLikeEvent;
    private static final int PAGESIZE = 20;
    private int curPage = 1;

    public FavoriteViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void unlike(Track track) {
        mModel.remove(FavoriteBean.class, track.getDataId()).subscribe(aBoolean ->
                getUnLikeEvent().setValue(track), Throwable::printStackTrace);

    }

    public void like(Track track) {
        mModel.insert(new FavoriteBean(track.getDataId(), track, System.currentTimeMillis()))
                .subscribe(subscribeBean -> getLikeEvent().setValue(track), Throwable::printStackTrace);
    }

    public void init() {
        mModel.listDesc(FavoriteBean.class, curPage, PAGESIZE, FavoriteBeanDao.Properties.Datetime)
                .doFinally(super::onViewRefresh)
                .subscribe(favoriteBeans ->
                {
                    getClearStatusEvent().call();
                    if (CollectionUtils.isEmpty(favoriteBeans)) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getInitFavoritesEvent().setValue(favoriteBeans);
                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }


    private void getMoreFavorites() {
        mModel.listDesc(FavoriteBean.class, curPage, PAGESIZE, FavoriteBeanDao.Properties.Datetime)
                .subscribe(favoriteBeans ->
                {
                    if (!CollectionUtils.isEmpty(favoriteBeans)) {
                        curPage++;
                    }
                    getFinishLoadmoreEvent().setValue(favoriteBeans);
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });
    }

    @Override
    public void onViewRefresh() {
        curPage = 1;
        init();
    }

    @Override
    public void onViewLoadmore() {
        getMoreFavorites();
    }


    public void play(long albumId, long trackId) {

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
                    RouteUtil.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
                }, Throwable::printStackTrace);
    }


    public SingleLiveEvent<List<FavoriteBean>> getInitFavoritesEvent() {
        return mInitFavoritesEvent = createLiveData(mInitFavoritesEvent);
    }

    public SingleLiveEvent<Track> getLikeEvent() {
        return mLikeEvent = createLiveData(mLikeEvent);
    }

    public SingleLiveEvent<Track> getUnLikeEvent() {
        return mUnLikeEvent = createLiveData(mUnLikeEvent);
    }
}
