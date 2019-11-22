package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.db.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.ComparatorUtil;

import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/21 9:36
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class DownloadViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<Track>> mTracksEvent;

    public DownloadViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }


    public void getDownloadTracks() {
        List<Track> tracks = XmDownloadManager.getInstance().getDownloadTracks(true);

        Collections.sort(tracks, ComparatorUtil.comparatorByUserSort(true));
        Completable.fromObservable(Observable.fromIterable(tracks)
                .flatMap((Function<Track, ObservableSource<List<PlayHistoryBean>>>) track -> {
                    track.setSource(0);
                    return mModel.list(PlayHistoryBean.class, PlayHistoryBeanDao.Properties.SoundId.eq(track.getDataId()),
                            PlayHistoryBeanDao.Properties.Kind.eq(track.getKind()));
                })
                .observeOn(Schedulers.io())
                .doOnNext(historyBeans -> {
                    if (!CollectionUtils.isEmpty(historyBeans)) {
                        CollectionUtils.find(tracks, item ->
                                item.getDataId() == historyBeans.get(0).getSoundId())
                                .setSource(historyBeans.get(0).getPercent());
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> getClearStatusEvent().call())
                .subscribe(() -> getTracksEvent().setValue(tracks), Throwable::printStackTrace);
    }

    public SingleLiveEvent<List<Track>> getTracksEvent() {
        return mTracksEvent = createLiveData(mTracksEvent);
    }
}
