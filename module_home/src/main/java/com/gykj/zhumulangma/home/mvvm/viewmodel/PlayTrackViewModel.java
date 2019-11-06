package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.FavoriteBean;
import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.db.FavoriteBeanDao;
import com.gykj.zhumulangma.common.db.SubscribeBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/28 8:27
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class PlayTrackViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<List<Album>> mAlbumsEvent;
    private SingleLiveEvent<Boolean> mSubscribeEvent;
    private SingleLiveEvent<Announcer> mAnnouncerEvent;
    private SingleLiveEvent<Boolean> mFavoriteEvent;


    public PlayTrackViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void unlike(Track track){
        mModel.remove(FavoriteBean.class,track.getDataId()).subscribe(aBoolean ->
                getFavoriteEvent().setValue(false), e->e.printStackTrace());

    }
    public void like(Track track){
        mModel.insert(new FavoriteBean(track.getDataId(),track,System.currentTimeMillis()))
                .subscribe(subscribeBean -> getFavoriteEvent().setValue(true), e->e.printStackTrace());
    }
    public void getFavorite(String trackId){
        mModel.list(FavoriteBean.class, FavoriteBeanDao.Properties.TrackId.eq(trackId))
                .subscribe(favoriteBeans ->
                        getFavoriteEvent().setValue(favoriteBeans.size() > 0), e->e.printStackTrace());
    }

    public void unsubscribe(long albumId){
        mModel.remove(SubscribeBean.class,albumId).subscribe(aBoolean ->
                getSubscribeEvent().setValue(false), e->e.printStackTrace());

    }
    public void subscribe(String albumId){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_IDS, albumId);
        mModel.getBatch(map)
                .flatMap((Function<BatchAlbumList, ObservableSource<SubscribeBean>>) albumList ->
                        mModel.insert(new SubscribeBean(albumList.getAlbums().get(0).getId(),
                        albumList.getAlbums().get(0),System.currentTimeMillis())))
                .subscribe(subscribeBean -> getSubscribeEvent().setValue(true), e->e.printStackTrace());
    }

    public void getSubscribe(String albumId){
        mModel.list(SubscribeBean.class, SubscribeBeanDao.Properties.AlbumId.eq(albumId))
                .subscribe(subscribeBeans ->
                        getSubscribeEvent().setValue(subscribeBeans.size() > 0), e->e.printStackTrace());
    }
    public void getRelativeAlbums(String trackId){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TRACKID, trackId);
        mModel.getRelativeAlbumsUseTrackId(map)
                .subscribe(relativeAlbums -> getAlbumsEvent().setValue(
                        relativeAlbums.getRelativeAlbumList()), e->e.printStackTrace());
    }
    public void getAnnouncer(long announcerId){
        Map<String, String> map = new HashMap<>();
        map.put("ids", String.valueOf(announcerId));
        //主播详情
        mModel.getAnnouncersBatch(map)
                .subscribe(announcerListByIds ->
                        getAnnouncerEvent().setValue(announcerListByIds.getAnnouncers().get(0)), e->e.printStackTrace());
    }
    public SingleLiveEvent<List<Album>> getAlbumsEvent() {
        return mAlbumsEvent =createLiveData(mAlbumsEvent);
    }

    public SingleLiveEvent<Announcer> getAnnouncerEvent() {
        return mAnnouncerEvent=createLiveData(mAnnouncerEvent);
    }

    public SingleLiveEvent<Boolean> getSubscribeEvent() {
        return mSubscribeEvent = createLiveData(mSubscribeEvent);
    }
    public SingleLiveEvent<Boolean> getFavoriteEvent() {
        return mFavoriteEvent = createLiveData(mFavoriteEvent);
    }

}
