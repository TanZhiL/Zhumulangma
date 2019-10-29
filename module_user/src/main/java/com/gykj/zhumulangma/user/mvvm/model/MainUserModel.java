package com.gykj.zhumulangma.user.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.net.RxAdapter;
import com.gykj.zhumulangma.common.net.dto.GitHubDTO;

import io.reactivex.Observable;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/11 16:39
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class MainUserModel extends ZhumulangmaModel {
    public MainUserModel(Application application) {
        super(application);
    }

    public Observable<GitHubDTO> getGitHub() {
        return mNetManager.getCacheProvider().getGitHub(mNetManager.getUserService().getGitHub())
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }
}
