package com.gykj.zhumulangma.common.mvvm.model;

import android.app.Application;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.net.http.ResponseThrowable;
import com.gykj.zhumulangma.common.net.http.RxAdapter;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.AnnouncerListByIds;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategoryList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2List;
import com.ximalaya.ting.android.opensdk.model.column.ColumnList;
import com.ximalaya.ting.android.opensdk.model.download.RecommendDownload;
import com.ximalaya.ting.android.opensdk.model.live.program.ProgramList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListByCategory;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;
import com.ximalaya.ting.android.opensdk.model.track.AnnouncerTrackList;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackListV2;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class ZhumulangmaModel extends CommonModel {
    public ZhumulangmaModel(Application application) {
        super(application);
    }

    /**
     * 获取焦点图
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     * 批量获取专辑列表
     *
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
     *
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
     * 根据上一次所听声音的id，获取此声音所在那一页的声音
     *
     * @param specificParams
     * @return
     */
    public Observable<LastPlayTrackList> getLastPlayTracks(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getLastPlayTracks(specificParams,
                new IDataCallBack<LastPlayTrackList>() {
                    @Override
                    public void onSuccess(@Nullable LastPlayTrackList trackList) {
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
     * 获取某个分类下的主播列表
     *
     * @param specificParams
     * @return
     */
    public Observable<AnnouncerList> getAnnouncerList(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getAnnouncerList(specificParams,
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
     * 获取某个声音的相关推荐专辑
     *
     * @param specificParams
     * @return
     */
    public Observable<RelativeAlbums> getRelativeAlbumsUseTrackId(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getRelativeAlbumsUseTrackId(specificParams,
                new IDataCallBack<RelativeAlbums>() {
                    @Override
                    public void onSuccess(@Nullable RelativeAlbums relativeAlbums) {
                        emitter.onNext(relativeAlbums);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 获取某个专辑的相关推荐
     *
     * @param specificParams
     * @return
     */
    public Observable<RelativeAlbums> getRelativeAlbums(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getRelativeAlbums(specificParams,
                new IDataCallBack<RelativeAlbums>() {
                    @Override
                    public void onSuccess(@Nullable RelativeAlbums relativeAlbums) {
                        emitter.onNext(relativeAlbums);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 获取下载听模块的推荐下载专辑
     *
     * @param specificParams
     * @return
     */
    public Observable<RecommendDownload> getRecommendDownloadList(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getRecommendDownloadList(specificParams,
                new IDataCallBack<RecommendDownload>() {
                    @Override
                    public void onSuccess(@Nullable RecommendDownload recommendDownload) {
                        emitter.onNext(recommendDownload);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }
    /**
     * 获取直播节目详情
     *
     * @param specificParams
     * @return
     */
    public Observable<ProgramList> getProgram(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getProgram(specificParams,
                new IDataCallBack<ProgramList>() {
                    @Override
                    public void onSuccess(@Nullable ProgramList programList) {
                        emitter.onNext(programList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }
    /**
     * 批量获取电台接口
     *
     * @param specificParams
     * @return
     */
    public Observable<RadioListById> getRadiosByIds(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getRadiosByIds(specificParams,
                new IDataCallBack<RadioListById>() {
                    @Override
                    public void onSuccess(@Nullable RadioListById radioListById) {
                        emitter.onNext(radioListById);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 获取节目列表
     *
     * @param specificParams
     * @return
     */
    public  Observable<List<Schedule>> getSchedules(Map<String, String> specificParams) {
        return Observable.create((ObservableOnSubscribe<List<Schedule>>) emitter -> CommonRequest.getSchedules(specificParams,
                new IDataCallBack<ScheduleList>() {
                    @Override
                    public void onSuccess(@Nullable ScheduleList scheduleList) {
                        if (CollectionUtils.isEmpty(scheduleList.getmScheduleList())) {
                            emitter.onError(new Exception("节目列表为空"));
                        } else {
                            emitter.onNext(scheduleList.getmScheduleList());
                            emitter.onComplete();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }



    /**
     * 搜索声音，支持的筛选条件包括声音ID、标题、所属专辑ID、所属专辑标题、
     * 所属主播ID或昵称、标签、是否付费、分类ID或分类名等，并可指定排序字段。
     *
     * @param specificParams
     * @return
     */
    public Observable<SearchTrackListV2> searchTrackV2(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.searchTrackV2(specificParams,
                new IDataCallBack<SearchTrackListV2>() {
                    @Override
                    public void onSuccess(@Nullable SearchTrackListV2 searchTrackListV2) {
                        emitter.onNext(searchTrackListV2);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 根据电台分类获取直播电台数据。
     *
     * @param specificParams
     * @return
     */
    public Observable<RadioListByCategory> getRadiosByCategory(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getRadiosByCategory(specificParams,
                new IDataCallBack<RadioListByCategory>() {
                    @Override
                    public void onSuccess(@Nullable RadioListByCategory radioListByCategory) {
                        emitter.onNext(radioListByCategory);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 根据一批主播ID批量获取主播信息
     *
     * @param specificParams
     * @return
     */
    public Observable<AnnouncerListByIds> getAnnouncersBatch(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getAnnouncersBatch(specificParams,
                new IDataCallBack<AnnouncerListByIds>() {
                    @Override
                    public void onSuccess(@Nullable AnnouncerListByIds announcerListByIds) {
                        emitter.onNext(announcerListByIds);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

    /**
     * 获取主播的专辑列表
     *
     * @param specificParams
     * @return
     */
    public Observable<AlbumList> getAlbumsByAnnouncer(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getAlbumsByAnnouncer(specificParams,
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
     *  获取某个主播的声音列表
     *
     * @param specificParams
     * @return
     */
    public Observable<AnnouncerTrackList> getTracksByAnnouncer(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getTracksByAnnouncer(specificParams,
                new IDataCallBack<AnnouncerTrackList>() {
                    @Override
                    public void onSuccess(@Nullable AnnouncerTrackList trackList) {
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
     *  获取某个主播的声音列表
     *
     * @param specificParams
     * @return
     */
    public Observable<AnnouncerCategoryList> getAnnouncerCategoryList(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getAnnouncerCategoryList(specificParams,
                new IDataCallBack<AnnouncerCategoryList>() {
                    @Override
                    public void onSuccess(@Nullable AnnouncerCategoryList categoryList) {
                        emitter.onNext(categoryList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }


    /**
     *   获取某个关键词的联想词
     *
     * @param specificParams
     * @return
     */
    public Observable<SuggestWords> getSuggestWord(Map<String, String> specificParams) {
        return Observable.create(emitter -> CommonRequest.getSuggestWord(specificParams,
                new IDataCallBack<SuggestWords>() {
                    @Override
                    public void onSuccess(@Nullable SuggestWords suggestWords) {
                        emitter.onNext(suggestWords);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new ResponseThrowable(String.valueOf(i), s));
                    }
                })).compose(RxAdapter.exceptionTransformer());
    }

}
