package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.FavoriteBean;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.dao.FavoriteBeanDao;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/20 10:01
 * Email: 1071931588@qq.com
 * Description:
 */
public class FavoriteViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<FavoriteBean>> mFavoritesSingleLiveEvent;
    private SingleLiveEvent<Track> mLikeSingleLiveEvent;
    private SingleLiveEvent<Track> mUnLikeSingleLiveEvent;
    private static final int PAGESIZE = 20;
    private int curPage = 1;

    public FavoriteViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void unlike(Track track){
        mModel.remove(FavoriteBean.class,track.getDataId()).subscribe(aBoolean ->
                getUnLikeSingleLiveEvent().postValue(track), e->e.printStackTrace());

    }
    public void like(Track track){
        mModel.insert(new FavoriteBean(track.getDataId(),track,System.currentTimeMillis()))
                .subscribe(subscribeBean -> getLikeSingleLiveEvent().postValue(track), e->e.printStackTrace());
    }

    public void getFavorites() {
        mModel.listDesc(FavoriteBean.class, curPage, PAGESIZE, FavoriteBeanDao.Properties.Datetime)
                .subscribe(favoriteBeans ->
                {
                    if (favoriteBeans.size() > 0)
                        curPage++;
                    getFavoritesSingleLiveEvent().postValue(favoriteBeans);
                }, e -> e.printStackTrace());
    }


    public void refresh() {
        curPage = 1;
        getFavorites();
    }

    long trackId=-1;
    public void play(long albumId,long trackId) {

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        mModel.getLastPlayTracks(map)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d ->  getShowLoadingViewEvent().postValue(""))
                .doFinally(() -> getShowLoadingViewEvent().postValue(null))
                .subscribe(trackList -> {
                    for (int i = 0; i < trackList.getTracks().size(); i++) {
                        if(trackList.getTracks().get(i).getDataId()==trackId){
                            XmPlayerManager.getInstance(getApplication()).playList(trackList,i);
                            break;
                        }
                    }
                    Object navigation = ARouter.getInstance()
                            .build(AppConstants.Router.Home.F_PLAY_TRACK).navigation();
                    if (null != navigation) {
                        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                                new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK,
                                        (ISupportFragment) navigation)));
                    }
                }, e -> e.printStackTrace());
    }


    public SingleLiveEvent<List<FavoriteBean>> getFavoritesSingleLiveEvent() {
        return mFavoritesSingleLiveEvent = createLiveData(mFavoritesSingleLiveEvent);
    }
    public SingleLiveEvent<Track> getLikeSingleLiveEvent() {
        return mLikeSingleLiveEvent = createLiveData(mLikeSingleLiveEvent);
    }

    public SingleLiveEvent<Track> getUnLikeSingleLiveEvent() {
        return mUnLikeSingleLiveEvent= createLiveData(mUnLikeSingleLiveEvent);
    }
}
