package com.gykj.zhumulangma.common.mvvm.model;

import android.app.Application;
import android.support.annotation.Nullable;

import com.gykj.zhumulangma.common.net.http.ResponseThrowable;
import com.gykj.zhumulangma.common.net.http.RxAdapter;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
import com.ximalaya.ting.android.opensdk.model.column.ColumnList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;

import java.util.Map;

import io.reactivex.Observable;

public class ZhumulangmaModel extends BaseModel {
    public ZhumulangmaModel(Application application) {
        super(application);
    }

    public Observable<BannerV2List> getCategoryBannersV2(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getCategoryBannersV2(specificParams,
                new IDataCallBack<BannerV2List>() {
                    @Override
                    public void onSuccess(@Nullable BannerV2List bannerV2List) {
                        emitter.onNext(bannerV2List);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    public Observable<GussLikeAlbumList> getGuessLikeAlbum(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getGuessLikeAlbum(specificParams,
                new IDataCallBack<GussLikeAlbumList>() {
                    @Override
                    public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                        emitter.onNext(gussLikeAlbumList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    public Observable<AlbumList> getAlbumList(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getAlbumList(specificParams,
                new IDataCallBack<AlbumList>() {
                    @Override
                    public void onSuccess(@Nullable AlbumList albumList) {
                        emitter.onNext(albumList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    public Observable<RadioList> getRadios(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getRadios(specificParams,
                new IDataCallBack<RadioList>() {
                    @Override
                    public void onSuccess(@Nullable RadioList radioList) {
                        emitter.onNext(radioList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    public Observable<ColumnList> getColumnList(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getColumnList(specificParams,
                new IDataCallBack<ColumnList>() {
                    @Override
                    public void onSuccess(@Nullable ColumnList columnList) {
                        emitter.onNext(columnList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }


    public Observable<AlbumList> getPaidAlbumByTag(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getPaidAlbumByTag(specificParams,
                new IDataCallBack<AlbumList>() {
                    @Override
                    public void onSuccess(@Nullable AlbumList albumList) {
                        emitter.onNext(albumList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    public Observable<RadioList> getRadiosByCity(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getRadiosByCity(specificParams,
                new IDataCallBack<RadioList>() {
                    @Override
                    public void onSuccess(@Nullable RadioList radioList) {
                        emitter.onNext(radioList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }


    public Observable<RadioList> getRankRadios(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getRankRadios(specificParams,
                new IDataCallBack<RadioList>() {
                    @Override
                    public void onSuccess(@Nullable RadioList radioList) {
                        emitter.onNext(radioList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }


}
