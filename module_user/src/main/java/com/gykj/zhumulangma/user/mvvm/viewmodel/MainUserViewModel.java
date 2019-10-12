package com.gykj.zhumulangma.user.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.net.dto.GitHubDTO;
import com.gykj.zhumulangma.user.mvvm.model.MainUserModel;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/10 8:52
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class MainUserViewModel extends BaseRefreshViewModel<MainUserModel,Object> {
    private SingleLiveEvent<GitHubDTO> mGitHubEvent;
    public MainUserViewModel(@NonNull Application application, MainUserModel model) {
        super(application, model);
    }

    public void init(){
        mModel.getGitHub()
                .doFinally(super::onViewRefresh)
                .subscribe(gitHubDTO -> getGitHubEvent().setValue(gitHubDTO), e->e.printStackTrace());
    }

    @Override
    public void onViewRefresh() {
        init();
    }

    public SingleLiveEvent<GitHubDTO> getGitHubEvent() {
        return mGitHubEvent=createLiveData(mGitHubEvent);
    }
}
