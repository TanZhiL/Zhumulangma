package com.gykj.zhumulangma.main.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.UserBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.net.dto.LoginDTO;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.main.mvvm.model.LoginModel;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/7/30 17:14
 * Email: 1071931588@qq.com
 * Description:
 */
public class LoginViewModel extends BaseViewModel<LoginModel> {
    SingleLiveEvent<UserBean> mUserBeanSingleLiveEvent;

    public LoginViewModel(@NonNull Application application, LoginModel model) {
        super(application, model);
    }

    public void _getUser() {
        mModel.getUser().doOnSubscribe(this)
                .subscribe(userBean -> getUserBeanSingleLiveEvent().postValue(userBean), e -> e.printStackTrace());
    }
    public void _login(String code,String descer_name,String descer_phone,String graer_name,String graer_phone){
        if (invalid(code)) return;
        LoginDTO loginDTO=new LoginDTO();
        loginDTO.setCode(code);
        loginDTO.setDescer_name(descer_name);
        loginDTO.setDescer_phone(descer_phone);
        loginDTO.setGraer_name(graer_name);
        loginDTO.setGraer_phone(graer_phone);
        mModel.login(loginDTO)
                .map(userBeanResponseDTO -> userBeanResponseDTO.result)
                .flatMap(new Function<UserBean, Observable<UserBean>>() {
                    @Override
                    public Observable<UserBean> apply(UserBean userBean) throws Exception {
                        if(!TextUtils.isEmpty(userBean.getGraer_phone())){
                            JPushInterface.setAlias(getApplication(),userBean.getGraer_phone(),null);
                        }
                        return mModel.saveUser(userBean);
                    }
                })
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> postShowTransLoadingViewEvent(""))
                .doFinally(() -> postShowTransLoadingViewEvent(null))
                .subscribe(userBean -> LoginViewModel.this.postStartFragmentEvent(
                        (ISupportFragment) ARouter.getInstance().build(AppConstants.Router.Main.F_MAIN).navigation()),
                        e->e.printStackTrace());
    }

    private boolean invalid(String code) {
        if(TextUtils.isEmpty(code)){
            ToastUtil.showToast("授权码不能为空");
            return true;
        }
        return false;
    }

    public SingleLiveEvent<UserBean> getUserBeanSingleLiveEvent() {
        return mUserBeanSingleLiveEvent = createLiveData(mUserBeanSingleLiveEvent);
    }

}
