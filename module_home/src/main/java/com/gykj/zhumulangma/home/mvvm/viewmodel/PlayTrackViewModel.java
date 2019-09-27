package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.FavoriteBean;
import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.dao.FavoriteBeanDao;
import com.gykj.zhumulangma.common.dao.SubscribeBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * Date: 2019/8/28 8:27
 * Email: 1071931588@qq.com
 * Description:
 */
public class PlayTrackViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<List<Album>> mAlbumSingleLiveEvent;
    private SingleLiveEvent<Boolean> mSubscribeSingleLiveEvent;
    private SingleLiveEvent<Boolean> mFavoriteSingleLiveEvent;


    public PlayTrackViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void unlike(Track track){
        mModel.remove(FavoriteBean.class,track.getDataId()).subscribe(aBoolean ->
                getFavoriteSingleLiveEvent().setValue(false), e->e.printStackTrace());

    }
    public void like(Track track){
        mModel.insert(new FavoriteBean(track.getDataId(),track,System.currentTimeMillis()))
                .subscribe(subscribeBean -> getFavoriteSingleLiveEvent().setValue(true), e->e.printStackTrace());
    }
    public void getFavorite(String trackId){
        mModel.list(FavoriteBean.class, FavoriteBeanDao.Properties.TrackId.eq(trackId))
                .subscribe(favoriteBeans ->
                        getFavoriteSingleLiveEvent().setValue(favoriteBeans.size() > 0), e->e.printStackTrace());
    }

    public void unsubscribe(long albumId){
        mModel.remove(SubscribeBean.class,albumId).subscribe(aBoolean ->
                getSubscribeSingleLiveEvent().setValue(false), e->e.printStackTrace());

    }
    public void subscribe(String albumId){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_IDS, albumId);
        mModel.getBatch(map)
                .flatMap((Function<BatchAlbumList, ObservableSource<SubscribeBean>>) albumList ->
                        mModel.insert(new SubscribeBean(albumList.getAlbums().get(0).getId(),
                        albumList.getAlbums().get(0),System.currentTimeMillis())))
                .subscribe(subscribeBean -> getSubscribeSingleLiveEvent().setValue(true), e->e.printStackTrace());
    }

    public void getSubscribe(String albumId){
        mModel.list(SubscribeBean.class, SubscribeBeanDao.Properties.AlbumId.eq(albumId))
                .subscribe(subscribeBeans ->
                        getSubscribeSingleLiveEvent().setValue(subscribeBeans.size() > 0), e->e.printStackTrace());
    }
    public void getRelativeAlbums(String trackId){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TRACKID, trackId);
        mModel.getRelativeAlbumsUseTrackId(map)
                .subscribe(relativeAlbums -> getAlbumSingleLiveEvent().setValue(
                        relativeAlbums.getRelativeAlbumList()), e->e.printStackTrace());
    }

    public SingleLiveEvent<List<Album>> getAlbumSingleLiveEvent() {
        return mAlbumSingleLiveEvent=createLiveData(mAlbumSingleLiveEvent);
    }

    public SingleLiveEvent<Boolean> getSubscribeSingleLiveEvent() {
        return mSubscribeSingleLiveEvent = createLiveData(mSubscribeSingleLiveEvent);
    }
    public SingleLiveEvent<Boolean> getFavoriteSingleLiveEvent() {
        return mFavoriteSingleLiveEvent = createLiveData(mFavoriteSingleLiveEvent);
    }

}
