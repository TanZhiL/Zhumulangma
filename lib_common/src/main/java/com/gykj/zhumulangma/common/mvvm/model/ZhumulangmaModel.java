package com.gykj.zhumulangma.common.mvvm.model;

import android.app.Application;
import android.support.annotation.Nullable;

import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.bean.SearchHistoryBean;
import com.gykj.zhumulangma.common.dao.SearchHistoryBeanDao;
import com.gykj.zhumulangma.common.net.http.ResponseThrowable;
import com.gykj.zhumulangma.common.net.http.RxAdapter;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
import com.ximalaya.ting.android.opensdk.model.column.ColumnList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class ZhumulangmaModel extends BaseModel {
    public ZhumulangmaModel(Application application) {
        super(application);
    }

    /**
     * 获取焦点图
     * @param specificParams
     * @return
     */
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

    /**
     * 获取猜你喜欢
     * @param specificParams
     * @return
     */
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

    /**
     * 获取所有付费专辑
     * @param specificParams
     * @return
     */
    public Observable<AlbumList> getAllPaidAlbums(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getAllPaidAlbums(specificParams,
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

    /**
     * 获取专辑列表
     * @param specificParams
     * @return
     */
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

    /**
     * 获取电台
     * @param specificParams
     * @return
     */
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

    /**
     * 获取精品听单内容
     * @param specificParams
     * @return
     */
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

    /**
     * 根据tag获取付费专辑
     * @param specificParams
     * @return
     */
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

    /**
     * 根据位置获取电台
     * @param specificParams
     * @return
     */
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

    /**
     * 获取电台排行
     * @param specificParams
     * @return
     */
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

    /**
     * 获取热词
     * @param specificParams
     * @return
     */
    public Observable<HotWordList> getHotWords(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getHotWords(specificParams,
                new IDataCallBack<HotWordList>() {
                    @Override
                    public void onSuccess(@Nullable HotWordList hotWordList) {
                        emitter.onNext(hotWordList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 搜索专辑
     * @param specificParams
     * @return
     */
    public Observable<SearchAlbumList> getSearchedAlbums(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getSearchedAlbums(specificParams,
                new IDataCallBack<SearchAlbumList>() {
                    @Override
                    public void onSuccess(@Nullable SearchAlbumList albumList) {
                        emitter.onNext(albumList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 搜索声音
     * @param specificParams
     * @return
     */
    public Observable<SearchTrackList> getSearchedTracks(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getSearchedTracks(specificParams,
                new IDataCallBack<SearchTrackList>() {
                    @Override
                    public void onSuccess(@Nullable SearchTrackList trackList) {
                        emitter.onNext(trackList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 搜索电台
     * @param specificParams
     * @return
     */
    public Observable<RadioList> getSearchedRadios(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getSearchedRadios(specificParams,
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

    /**
     * 搜索主播
     * @param specificParams
     * @return
     */
    public Observable<AnnouncerList> getSearchAnnouncers(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getSearchAnnouncers(specificParams,
                new IDataCallBack<AnnouncerList>() {
                    @Override
                    public void onSuccess(@Nullable AnnouncerList announcerList) {
                        emitter.onNext(announcerList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     *  批量获取专辑列表
     * @param specificParams
     * @return
     */
    public Observable<BatchAlbumList> getBatch(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getBatch(specificParams,
                new IDataCallBack<BatchAlbumList>() {
                    @Override
                    public void onSuccess(@Nullable BatchAlbumList batchAlbumList) {
                        emitter.onNext(batchAlbumList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 专辑浏览，根据专辑ID获取专辑下的声音列表
     * @param specificParams
     * @return
     */
    public Observable<TrackList> getTracks(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getTracks(specificParams,
                new IDataCallBack<TrackList>() {
                    @Override
                    public void onSuccess(@Nullable TrackList trackList) {
                        emitter.onNext(trackList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }



    /**
     * 条件查询
     * @param cls
     * @param <T>
     * @return
     */
    public <T> Observable<List<T>> list(Class<T> cls, int page, int pagesize, Property asc,Property desc, WhereCondition cond, WhereCondition... condMore){

        return  Observable.create(emitter -> {
            List<T> list = new ArrayList<>();
            try {
                QueryBuilder<T> builder = App.getDaoSession().queryBuilder(cls);
                if(page!=0&&pagesize!=0){
                    builder=builder.offset((page-1)*pagesize).limit(pagesize);
                }
                if(cond!=null){
                    builder=builder.where(cond,condMore);
                }
                if(asc!=null){
                    builder=builder.orderAsc(asc);
                }
                if(desc!=null){
                    builder=builder.orderDesc(desc);
                }
                list=builder.list();
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onNext(list);
            emitter.onComplete();
        }).compose(RxAdapter.schedulersTransformer());

    }

    public <T> Observable<List<T>> list(Class<T> cls){

        return list(cls,0,0,null,null,null);

    }

    public <T> Observable<List<T>> list(Class<T> cls, int page, int pagesize){

        return list(cls,page,pagesize,null,null,null);

    }
    public <T> Observable<List<T>> listAsc(Class<T> cls, int page, int pagesize, Property asc){

        return list(cls,page,pagesize,asc,null,null);

    }
    public <T> Observable<List<T>> listDesc(Class<T> cls, int page, int pagesize, Property desc){

        return list(cls,page,pagesize,null,desc,null);

    }
    public <T> Observable<List<T>> list(Class<T> cls,  WhereCondition cond, WhereCondition... condMore){

        return list(cls,0,0,null,null,cond,condMore);

    }
    /**
     * 清空所有记录
     * @param cls
     * @param <T>
     * @return
     */
    public <T> Observable<Boolean> clear(Class<T> cls){
        return  Observable.create(emitter -> {
            try {
                App.getDaoSession().deleteAll(cls);
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onNext(true);
            emitter.onComplete();
        }).compose(RxAdapter.schedulersTransformer());


    }

    /**
     * 插入一条记录
     * @param entity
     * @param <T>
     * @return
     */
    public <T> Observable<T> insert(T entity){

        return  Observable.create(emitter -> {
            try {
                App.getDaoSession().insertOrReplace(entity);
            } catch (Exception e) {
                emitter.onError(e);
            }

            emitter.onNext(entity);
            emitter.onComplete();

        }).compose(RxAdapter.schedulersTransformer());

    }
}
