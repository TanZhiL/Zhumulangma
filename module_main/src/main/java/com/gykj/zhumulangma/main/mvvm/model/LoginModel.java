package com.gykj.zhumulangma.main.mvvm.model;

import android.app.Application;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.UserBean;
import com.gykj.zhumulangma.common.mvvm.model.BaseModel;
import com.gykj.zhumulangma.common.net.CommonService;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.dto.LoginDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.net.http.RxAdapter;

import io.reactivex.Observable;

/**
 * Author: Thomas.
 * Date: 2019/7/30 17:14
 * Email: 1071931588@qq.com
 * Description:
 */
public class LoginModel extends BaseModel {
    private CommonService mCommonService;

    public LoginModel(Application application) {
        super(application);
        mCommonService = RetrofitManager.getInstance().getCommonService();
    }

    public Observable<UserBean> getUser() {
        return Observable.create(emitter -> {
            UserBean userBean = null;
            try {
                String user = SPUtils.getInstance().getString(AppConstants.SP.USER);
                userBean = new Gson().fromJson(user, UserBean.class);
            } catch (JsonSyntaxException e) {
              emitter.onError(e);
            }
            if (null != userBean) {
                emitter.onNext(userBean);
                emitter.onComplete();
            }else {
                emitter.onError(new IllegalStateException("用户尚未登陆"));
            }
        })
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<UserBean> saveUser(final UserBean userBean) {
        return Observable.create(emitter -> {
            try {
                SPUtils.getInstance().put(AppConstants.SP.TOKEN, userBean.getToken());
                SPUtils.getInstance().put(AppConstants.SP.USER, new Gson().toJson(userBean));
            } catch (Exception e) {
               emitter.onError(e);
            }
            emitter.onNext(userBean);
            emitter.onComplete();
        })
                .compose(RxAdapter.schedulersTransformer())
                .compose(RxAdapter.exceptionTransformer());
    }

    public Observable<ResponseDTO<UserBean>> login(LoginDTO loginDTO) {
        return mCommonService.login(loginDTO)
                .compose(RxAdapter.schedulersTransformer())
                .compose(RxAdapter.exceptionTransformer());
    }
}
