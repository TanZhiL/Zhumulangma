package com.gykj.zhumulangma.common.aop;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.extra.RxField;
import com.gykj.zhumulangma.common.net.Constans;
import com.gykj.zhumulangma.common.net.NetManager;
import com.gykj.zhumulangma.common.net.RxAdapter;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.tencent.bugly.Bugly;
import com.ximalaya.ting.android.opensdk.auth.call.IXmlyAuthListener;
import com.ximalaya.ting.android.opensdk.auth.constants.XmlyConstants;
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

import okhttp3.FormBody;

/**
 * Author: Thomas.<br/>
 * Date: 2019/10/15 14:57<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:登陆跳转帮助类
 */
public class LoginHelper {

    private XmlyAuthInfo mAuthInfo;
    private XmlyAuth2AccessToken mAccessToken;
    private XmlySsoHandler mSsoHandler;
    private static volatile LoginHelper instance;

    private LoginHelper() {
    }

    public static LoginHelper getInstance() {
        if (instance == null) {
            synchronized (LoginHelper.class) {
                if (instance == null) {
                    instance = new LoginHelper();
                }
            }
        }
        return instance;
    }

    public XmlySsoHandler getSsoHandler() {
        return mSsoHandler;
    }

    public void login(Activity activity) {
        try {
            mAuthInfo = new XmlyAuthInfo(App.getInstance(), CommonRequest.getInstanse().getAppKey(), CommonRequest.getInstanse()
                    .getPackId(), Constans.REDIRECT_URL, CommonRequest.getInstanse().getAppKey());
        } catch (XimalayaException e) {
            e.printStackTrace();
        }
        mSsoHandler = new XmlySsoHandler(activity, mAuthInfo);
        mSsoHandler.authorize(new CustomAuthListener());
    }

    public void logout() {
        if (!AccessTokenManager.getInstanse().hasLogin()) {
            ToastUtil.showToast(ToastUtil.LEVEL_W, "尚未登陆");
            return;
        }
        AccessTokenManager.getInstanse().loginOut(new ILoginOutCallBack() {
            @Override
            public void onSuccess() {
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    AccessTokenKeeper.clear(App.getInstance());
                    mAccessToken = new XmlyAuth2AccessToken();
                }
                CommonRequest.getInstanse().setITokenStateChange(null);
                EventBus.getDefault().post(new FragmentEvent(EventCode.Main.LOGOUTSUCC));
                ToastUtil.showToast(ToastUtil.LEVEL_S, "注销成功");
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
            registerLoginTokenChangeListener();
            Bugly.setUserId(App.getInstance(), mAccessToken.getUid());
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

    public static void registerLoginTokenChangeListener() {
        // 使用此回调了就表示贵方接了需要用户登录才能访问的接口,如果没有此类接口可以不用设置此接口,之前的逻辑没有发生改变
        // !!! 此监听在登录之后设置上 ,退出登录后需要取消掉
        // 此接口表示token已经失效 ,如果第三方有refreshToken(refreshToken 由我方给出,refresh操作由贵方服务端实现,具体服务端实现请咨询文档后面的技术支持) 可以用refreshToken来获取或延长token
        // refresh 分同步请求和异步请求,同步请求一般是在请求过程中,我方服务端发现token已经过期,给返回token失效code,这时如果可以通过refreshtoken 再次刷新token ,我方再次请求,则可以达到用户无感知的进行访问操作
        // 异步请求一般是在刚开始启动应用的是本地检查到token失效 这时对实时性要求不高则可以异步refresh
        // 返回true 表示已经发起了refresh操作 ,如果是同步接口还表示已经拿到最新的token ,具体可以查看我方demo中的实例
        // tokenLosted 表示经过了refreshToken操作了但是还是token失效 ,那么就要去登录页面了 ,因为refreshToken也是有过期时间的
        CommonRequest.getInstanse().setITokenStateChange(new CommonRequest.ITokenStateChange() {
            // 此接口表示token已经失效 ,
            @Override
            public boolean getTokenByRefreshSync() {
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    return refreshSync();
                }
                return false;
            }

            @Override
            public boolean getTokenByRefreshAsync() {
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                        refresh();
                        return true;
                }
                return false;
            }

            @Override
            public void tokenLosted() {
                //登陆失效
                EventBus.getDefault().post(new FragmentEvent(EventCode.Main.LOGOUTSUCC));
            }
        });
    }

    public static void refresh() {
        NetManager.getInstance().getCommonService().refreshToken(getFormBody())
                .compose(RxAdapter.schedulersTransformer())
                .compose(RxAdapter.exceptionTransformer())
                .subscribe(xmTokenDTO -> AccessTokenManager.getInstanse().setAccessTokenAndUid(xmTokenDTO.getAccess_token(),
                        xmTokenDTO.getRefresh_token(), xmTokenDTO.getExpires_in(), Integer.toString(xmTokenDTO.getUid())),
                        Throwable::printStackTrace);
    }

    public static boolean refreshSync() {
        RxField<Boolean> isSucc=new RxField<>();

        NetManager.getInstance().getCommonService().refreshToken(getFormBody())
                .compose(RxAdapter.exceptionTransformer())
                .subscribe(xmTokenDTO -> {
                            AccessTokenManager.getInstanse().setAccessTokenAndUid(xmTokenDTO.getAccess_token(),
                                    xmTokenDTO.getRefresh_token(), xmTokenDTO.getExpires_in(), Integer.toString(xmTokenDTO.getUid()));
                            isSucc.set(true);
                        },
                        Throwable::printStackTrace);
        return isSucc.get();
    }

    @NonNull
    private static FormBody getFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
        try {
            builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token");
            builder.add(XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN, AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
            builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().getAppKey());
            builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().getDeviceId());
            builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID);
            builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().getPackId());
            builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().getUid());
            builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, Constans.REDIRECT_URL);
        } catch (XimalayaException e) {
            e.printStackTrace();
        }

        return builder.build();
    }

}
