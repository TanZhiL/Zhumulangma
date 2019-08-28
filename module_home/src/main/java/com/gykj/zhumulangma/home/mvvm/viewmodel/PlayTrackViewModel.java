package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Author: Thomas.
 * Date: 2019/8/28 8:27
 * Email: 1071931588@qq.com
 * Description:
 */
public class PlayTrackViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<List<Album>> mAlbumSingleLiveEvent;
    public PlayTrackViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getRelativeAlbums(String trackId){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TRACKID, trackId);
        mModel.getRelativeAlbumsUseTrackId(map)
                .subscribe(relativeAlbums -> getAlbumSingleLiveEvent().postValue(
                        relativeAlbums.getRelativeAlbumList()), e->e.printStackTrace());
    }

    public SingleLiveEvent<List<Album>> getAlbumSingleLiveEvent() {
        return mAlbumSingleLiveEvent=createLiveData(mAlbumSingleLiveEvent);
    }
}
