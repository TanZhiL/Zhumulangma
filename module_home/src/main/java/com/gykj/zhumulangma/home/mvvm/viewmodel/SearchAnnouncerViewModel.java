package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
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
 * Date: 2019/8/13 15:12
 * Email: 1071931588@qq.com
 * Description:
 */
public class SearchAnnouncerViewModel extends BaseRefreshViewModel<ZhumulangmaModel,Announcer> {
    private SingleLiveEvent<List<Announcer>> mInitAnnouncersEvent;

    private int curPage = 1;
    private String mKeyword;

    public SearchAnnouncerViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, mKeyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curPage));
        mModel.getSearchAnnouncers(map)
                .doOnSubscribe(d->getShowInitViewEvent().call())
                .subscribe(announcerList -> {
                    if (CollectionUtils.isEmpty(announcerList.getAnnouncerList())) {
                        getShowEmptyViewEvent().call();
                        return;
                    }
                    curPage++;
                    getClearStatusEvent().call();
                    getInitAnnouncersEvent().setValue(announcerList.getAnnouncerList());

                }, e -> {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private void getMoreAnnouncers() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, mKeyword);
        map.put(DTransferConstants.PAGE,String.valueOf(curPage));
        mModel.getSearchAnnouncers(map)
                .subscribe(announcerList -> {
                    if (!CollectionUtils.isEmpty(announcerList.getAnnouncerList())) {
                        curPage++;
                    }
                    getFinishLoadmoreEvent().setValue(announcerList.getAnnouncerList());
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });
    }


    @Override
    public void onViewLoadmore() {
        getMoreAnnouncers();
    }

    public SingleLiveEvent<List<Announcer>> getInitAnnouncersEvent() {
        return mInitAnnouncersEvent =createLiveData(mInitAnnouncersEvent);
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }
}
