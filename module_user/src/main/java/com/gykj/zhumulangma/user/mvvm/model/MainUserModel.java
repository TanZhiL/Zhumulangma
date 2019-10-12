package com.gykj.zhumulangma.user.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.dto.GitHubDTO;
import com.gykj.zhumulangma.common.net.http.RxAdapter;

import io.reactivex.Observable;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/11 16:39
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class MainUserModel  extends ZhumulangmaModel {
    RetrofitManager mRetrofitManager=RetrofitManager.getInstance();
    public MainUserModel(Application application) {
        super(application);
    }
    public Observable<GitHubDTO> getGitHub(){
        return mRetrofitManager.getUserService().getGitHub()
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }
}
