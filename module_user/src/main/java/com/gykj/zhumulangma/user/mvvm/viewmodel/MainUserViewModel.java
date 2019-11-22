package com.gykj.zhumulangma.user.mvvm.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.net.dto.GitHubDTO;
import com.gykj.zhumulangma.user.mvvm.model.MainUserModel;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.model.user.XmBaseUserInfo;

import java.util.HashMap;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/10 8:52
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class MainUserViewModel extends BaseRefreshViewModel<MainUserModel,Object> {
    private SingleLiveEvent<GitHubDTO> mGitHubEvent;
    private SingleLiveEvent<XmBaseUserInfo> mBaseUserInfoEvent;
    public MainUserViewModel(@NonNull Application application, MainUserModel model) {
        super(application, model);
    }

    public void init(){
        if(AccessTokenManager.getInstanse().hasLogin()){
            mModel.getGitHub()
                    .doFinally(super::onViewRefresh)
                    .doOnNext(gitHubDTO -> getGitHubEvent().setValue(gitHubDTO))
                    .flatMap((Function<GitHubDTO, ObservableSource<XmBaseUserInfo>>) gitHubDTO ->
                            mModel.getBaseUserInfo(new HashMap<>())).subscribe(xmBaseUserInfo ->
                    getBaseUserInfoEvent().setValue(xmBaseUserInfo), e->e.printStackTrace());
        }else {
            mModel.getGitHub()
                    .doFinally(super::onViewRefresh)
                    .subscribe(gitHubDTO ->
                            getGitHubEvent().setValue(gitHubDTO), e->e.printStackTrace());
        }

    }
    @Override
    public void onViewRefresh() {
        init();
    }

    public SingleLiveEvent<GitHubDTO> getGitHubEvent() {
        return mGitHubEvent=createLiveData(mGitHubEvent);
    }

    public SingleLiveEvent<XmBaseUserInfo> getBaseUserInfoEvent() {
        return mBaseUserInfoEvent=createLiveData(mBaseUserInfoEvent);
    }
}
