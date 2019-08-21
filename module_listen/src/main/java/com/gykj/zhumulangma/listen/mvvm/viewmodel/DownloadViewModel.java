package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.TrackDownloadBean;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import java.util.List;

/**
 * Author: Thomas.
 * Date: 2019/8/21 9:36
 * Email: 1071931588@qq.com
 * Description:
 */
public class DownloadViewModel extends BaseViewModel<ZhumulangmaModel> {

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
}
