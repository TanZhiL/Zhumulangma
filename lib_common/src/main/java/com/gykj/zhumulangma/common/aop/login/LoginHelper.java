package com.gykj.zhumulangma.common.aop.login;

import android.app.Activity;
import android.os.Bundle;

import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.AppHelper;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.ximalaya.ting.android.opensdk.auth.call.IXmlyAuthListener;
import com.ximalaya.ting.android.opensdk.auth.exception.XmlyException;
import com.ximalaya.ting.android.opensdk.auth.handler.XmlySsoHandler;
import com.ximalaya.ting.android.opensdk.auth.model.XmlyAuth2AccessToken;
import com.ximalaya.ting.android.opensdk.auth.model.XmlyAuthInfo;
import com.ximalaya.ting.android.opensdk.auth.utils.AccessTokenKeeper;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.ILoginOutCallBack;
import com.ximalaya.ting.android.opensdk.httputil.XimalayaException;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: Thomas.<br/>
 * Date: 2019/10/15 14:57<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:登陆跳转帮助类
 */
public class LoginHelper {

    private XmlyAuthInfo mAuthInfo;
    private XmlyAuth2AccessToken mAccessToken;
    private static volatile LoginHelper instance;

    private LoginHelper(){
    }

    public  static LoginHelper getInstance(){
        if(instance==null){
            synchronized (LoginHelper.class){
                if(instance==null){
                    instance=new LoginHelper();
                }
            }
        }
        return instance;
    }

    public void login(Activity activity) {
        try {
            mAuthInfo = new XmlyAuthInfo(App.getInstance(), CommonRequest.getInstanse().getAppKey(), CommonRequest.getInstanse()
                    .getPackId(), AppConstants.Ximalaya.REDIRECT_URL, CommonRequest.getInstanse().getAppKey());
        } catch (XimalayaException e) {
            e.printStackTrace();
        }
        XmlySsoHandler ssoHandler = new XmlySsoHandler(activity, mAuthInfo);
        ssoHandler.authorize(new CustomAuthListener());
    }

    public void logout() {
        AccessTokenManager.getInstanse().loginOut(new ILoginOutCallBack() {
            @Override
            public void onSuccess() {
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    AccessTokenKeeper.clear(App.getInstance());
                    mAccessToken = new XmlyAuth2AccessToken();
                }
                CommonRequest.getInstanse().setITokenStateChange(null);
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                CommonRequest.getInstanse().setITokenStateChange(null);
            }
        });

    }

    class CustomAuthListener implements IXmlyAuthListener {
        @Override
        public void onComplete(Bundle bundle) {
            parseAccessToken(bundle);
            AppHelper.registerLoginTokenChangeListener(App.getInstance());
            EventBus.getDefault().post(new FragmentEvent(EventCode.Main.LOGINSUCC));
            ToastUtil.showToast("登录成功");
        }

        @Override
        public void onXmlyException(final XmlyException e) {
            e.printStackTrace();
        }

        @Override
        public void onCancel() {
        }

    }

    private void parseAccessToken(Bundle bundle) {
        mAccessToken = XmlyAuth2AccessToken.parseAccessToken(bundle);
        if (mAccessToken.isSessionValid()) {
            AccessTokenManager.getInstanse().setAccessTokenAndUid(mAccessToken.getToken(), mAccessToken
                    .getRefreshToken(), mAccessToken.getExpiresAt(), mAccessToken.getUid());
        }
    }



}
