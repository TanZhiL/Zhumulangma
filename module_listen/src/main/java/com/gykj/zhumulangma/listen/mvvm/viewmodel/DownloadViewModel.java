package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.TrackDownloadBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.column.Column;
import com.ximalaya.ting.android.opensdk.model.column.ColumnList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Author: Thomas.
 * Date: 2019/8/21 9:36
 * Email: 1071931588@qq.com
 * Description:
 */
public class DownloadViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<Column>> mColumnSingleLiveEvent;
    private int curPage = 1;
    public DownloadViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void clearAlbum(long albumId){
        List<Track> downloadTrackInAlbum = XmDownloadManager.getInstance().getDownloadTrackInAlbum(albumId, true);
        for (int i = 0; i < downloadTrackInAlbum.size(); i++) {
            mModel.clear(TrackDownloadBean.class,downloadTrackInAlbum.get(i).getDataId()).subscribe();
        }
    }
    public void clearTrack(long trackId){
        mModel.clear(TrackDownloadBean.class,trackId).subscribe();
    }
    public void getRecommend(){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CALC_DIMENSION, "0");
        map.put(DTransferConstants.PAGE, String.valueOf(curPage));
        mModel.getColumnList(map)
                .subscribe(columnList -> {
                    curPage++;
                    getColumnSingleLiveEvent().postValue(columnList.getColumns());
                }, e->e.printStackTrace());
    }

    public SingleLiveEvent<List<Column>> getColumnSingleLiveEvent() {
        return mColumnSingleLiveEvent=createLiveData(mColumnSingleLiveEvent);
    }
}
