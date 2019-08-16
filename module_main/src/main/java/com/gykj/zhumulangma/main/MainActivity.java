package com.gykj.zhumulangma.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.util.log.TLog;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.event.common.BaseFragmentEvent;
import com.gykj.zhumulangma.common.mvvm.BaseActivity;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.widget.GlobalPlay;
import com.gykj.zhumulangma.main.fragment.MainFragment;
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
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import me.yokeyword.fragmentation.ISupportFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Route(path = AppConstants.Router.Main.A_MAIN)
public class MainActivity extends BaseActivity implements View.OnClickListener, MainFragment.onRootShowListener, IXmPlayerStatusListener {

    private GlobalPlay globalPlay;

    /**
     * 当前 DEMO 应用的回调页，第三方应用应该使用自己的回调页。
     */
    public static final String REDIRECT_URL = "http://api.ximalaya.com/openapi-collector-app/get_access_token";
    public static final String REFRESH_TOKEN_URL = "https://api.ximalaya.com/oauth2/refresh_token?";

    /**
     * 喜马拉雅授权实体类对象
     */
    private XmlyAuthInfo mAuthInfo;

    /**
     * 喜马拉雅授权管理类对象
     */
    private XmlySsoHandler mSsoHandler;

    /**
     * 封装了 "access_token"，"refresh_token"，并提供了他们的管理功能
     */
    private XmlyAuth2AccessToken mAccessToken;

    @Override
    protected int onBindLayout() {
        return R.layout.main_activity_main;
    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);
        if (findFragment(MainFragment.class) == null) {
            MainFragment mainFragment = new MainFragment();
            mainFragment.setShowListener(this);
            loadRootFragment(R.id.fl_container, mainFragment);
        }
        globalPlay = fd(R.id.gp);

    }

    @Override
    public void initListener() {
        globalPlay.setOnClickListener(this);
        XmPlayerManager.getInstance(this).addPlayerStatusListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public <T> void onEvent(BaseActivityEvent<T> event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.MainCode.NAVIGATE:
                NavigateBean navigateBean = (NavigateBean) event.getData();
                if (null == navigateBean.fragment) {
                    return;
                }
                switch (navigateBean.path) {
                    case AppConstants.Router.User.F_MESSAGE:
                        //登录拦截
                     /*   String token = AccessTokenKeeper.readAccessToken(this).getToken();
                        TLog.d(AccessTokenKeeper.readAccessToken(this));
                        if (TextUtils.isEmpty(token)) {
                            goLogin();
                        } else {
                            start(navigateBean.fragment);
                        }*/
                        start(navigateBean.fragment);
                        break;
                    default:
                        start(navigateBean.fragment);
                        break;
                }
                break;
            case EventCode.MainCode.HIDE_GP:
                globalPlay.setVisibility(View.GONE);
                break;
            case EventCode.MainCode.SHOW_GP:
                globalPlay.setVisibility(View.VISIBLE);
                break;
            case EventCode.MainCode.LOGIN:
                goLogin();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (v == globalPlay) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Player.F_PLAY_TRACK).navigation();
            if (null != navigation) {
                start((ISupportFragment) navigation);
            }
        }
    }

    @Override
    public void onRootShow(boolean isVisible) {
        if (isVisible)
            globalPlay.setBackgroundColor(Color.TRANSPARENT);
        else
            globalPlay.setBackground(getResources().getDrawable(R.drawable.shap_common_widget_play));
    }


    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    private void goLogin() {
        try {
            mAuthInfo = new XmlyAuthInfo(this, CommonRequest.getInstanse().getAppKey(), CommonRequest.getInstanse()
                    .getPackId(), REDIRECT_URL, CommonRequest.getInstanse().getAppKey());
        } catch (XimalayaException e) {
            e.printStackTrace();
        }
        mSsoHandler = new XmlySsoHandler(this, mAuthInfo);

        mSsoHandler.authorize(new CustomAuthListener());
    }

    @Override
    public void onPlayStart() {
        Track currSoundIgnoreKind = XmPlayerManager.getInstance(this).getCurrSoundIgnoreKind(true);
        if(null==currSoundIgnoreKind){
            return;
        }
        globalPlay.play(currSoundIgnoreKind.getCoverUrlMiddle());
    }

    @Override
    public void onPlayPause() {
        globalPlay.pause();
    }

    @Override
    public void onPlayStop() {
        globalPlay.pause();
    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {

    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int i) {

    }

    @Override
    public void onPlayProgress(int i, int i1) {

    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }

    /**
     * 喜马拉雅认证授权回调类。
     * 1. 客户端授权时，需要在 {@link #onActivityResult} 中调用 {@link XmlySsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * <p>
     * 注意：当认证授权成功时，喜马拉雅 OAuthSDK 中已经保存了 access_token uid 等信息，
     * 需要使用时调用 { AccessTokenKeeper#readAccessToken(Context)}。
     */
    class CustomAuthListener implements IXmlyAuthListener {

        // 当认证授权成功时，回调该方法
        @Override
        public void onComplete(Bundle bundle) {
            parseAccessToken(bundle);
            registerLoginTokenChangeListener(MainActivity.this.getApplicationContext());
            EventBus.getDefault().post(new BaseFragmentEvent<>(EventCode.MainCode.LOGINSUCC));
            ToastUtil.showToast("登录成功");
        }

        // 当授权过程中发生异常（如回调地址无效等信息等）时，回调该方法
        @Override
        public void onXmlyException(final XmlyException e) {
            e.printStackTrace();
        }

        // 当用户主动取消授权时，回调该方法
        @Override
        public void onCancel() {
        }
    }

    private void parseAccessToken(Bundle bundle) {
        // 从 Bundle 中解析 access token
        mAccessToken = XmlyAuth2AccessToken.parseAccessToken(bundle);
        if (mAccessToken.isSessionValid()) {
            /**
             * 关键!!!!!!!!!!
             * 结果返回之后将取回的结果设置到token管理器中
             */
            AccessTokenManager.getInstanse().setAccessTokenAndUid(mAccessToken.getToken(), mAccessToken
                    .getRefreshToken(), mAccessToken.getExpiresAt(), mAccessToken.getUid());
        }
    }

    public static void registerLoginTokenChangeListener(final Context context) {
        // 使用此回调了就表示贵方接了需要用户登录才能访问的接口,如果没有此类接口可以不用设置此接口,之前的逻辑没有发生改变
        CommonRequest.getInstanse().setITokenStateChange(new CommonRequest.ITokenStateChange() {
            // 此接口表示token已经失效 ,
            @Override
            public boolean getTokenByRefreshSync() {
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    try {
                        return refreshSync();
                    } catch (XimalayaException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean getTokenByRefreshAsync() {
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    try {
                        refresh();
                        return true;
                    } catch (XimalayaException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public void tokenLosted() {
                EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.LOGIN));
            }
        });
    }

    public static void refresh() throws XimalayaException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .build();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token");
        builder.add(XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN, AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().getAppKey());
        builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().getDeviceId());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID);
        builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().getPackId());
        builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().getUid());
        builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URL);
        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url("https://api.ximalaya.com/oauth2/refresh_token?")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d("refresh", "refreshToken, request failed, error message = " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                String body = response.body().string();

                System.out.println("TingApplication.refreshSync  1  " + body);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonObject != null) {
                    AccessTokenManager.getInstanse().setAccessTokenAndUid(jsonObject.optString("access_token"),
                            jsonObject.optString("refresh_token"), jsonObject.optLong("expires_in"), jsonObject
                                    .optString("uid"));
                }
            }
        });
    }

    public static boolean refreshSync() throws XimalayaException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .build();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token");
        builder.add(XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN, AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().getAppKey());
        builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().getDeviceId());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID);
        builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().getPackId());
        builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().getUid());
        builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URL);
        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url(REFRESH_TOKEN_URL)
                .post(body)
                .build();
        try {
            Response execute = client.newCall(request).execute();
            if (execute.isSuccessful()) {
                try {
                    String string = execute.body().string();
                    JSONObject jsonObject = new JSONObject(string);

                    System.out.println("TingApplication.refreshSync  2  " + string);

                    AccessTokenManager.getInstanse().setAccessTokenAndUid(jsonObject.optString("access_token"),
                            jsonObject.optString("refresh_token"), jsonObject.optLong("expires_in"), jsonObject
                                    .optString("uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    // 用户退出登录，清空喜马拉雅授权 SDK 中保存的 access token 信息
    public void logout() {
        AccessTokenManager.getInstanse().loginOut(new ILoginOutCallBack() {
            @Override
            public void onSuccess() {
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    AccessTokenKeeper.clear(MainActivity.this);
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
}
