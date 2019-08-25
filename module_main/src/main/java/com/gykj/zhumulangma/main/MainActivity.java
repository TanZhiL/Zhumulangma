package com.gykj.zhumulangma.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.App;
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
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

import static com.gykj.zhumulangma.common.AppConstants.Ximalaya.REDIRECT_URL;

@Route(path = AppConstants.Router.Main.A_MAIN)
public class MainActivity extends BaseActivity implements View.OnClickListener, MainFragment.onRootShowListener, IXmPlayerStatusListener, IXmAdsStatusListener {

    private GlobalPlay globalPlay;

    /**
     * 当前 DEMO 应用的回调页，第三方应用应该使用自己的回调页。
     */


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
        XmPlayerManager.getInstance(this).addAdsStatusListener(this);
    }

    @Override
    public void initData() {
        if(XmPlayerManager.getInstance(this).isPlaying()){
            Track currSoundIgnoreKind = XmPlayerManager.getInstance(this).getCurrSoundIgnoreKind(true);
            if (null == currSoundIgnoreKind) {
                return;
            }
            globalPlay.play(currSoundIgnoreKind.getCoverUrlMiddle());
        }
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
                   /*     if (!AccessTokenManager.getInstanse().hasLogin()) {
                            goLogin();
                        } else {
                            start(navigateBean.fragment);
                        }*/
                        start(navigateBean.fragment);
                        break;
                    case AppConstants.Router.Home.F_PLAY_TRACK:
                        extraTransaction().setCustomAnimations(
                                com.gykj.zhumulangma.common.R.anim.push_bottom_in,
                                com.gykj.zhumulangma.common.R.anim.no_anim,
                                com.gykj.zhumulangma.common.R.anim.no_anim,
                                com.gykj.zhumulangma.common.R.anim.push_bottom_out)
                                .start(navigateBean.fragment);
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
            XmPlayerManager.getInstance(this).play();

            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_PLAY_TRACK).navigation();
            if (null != navigation) {
                EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                        new NavigateBean(AppConstants.Router.Home.F_PLAY_TRACK, (ISupportFragment) navigation)));
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
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
            /*    finish();
                android.os.Process.killProcess(android.os.Process.myPid());*/
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
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
        if (null == currSoundIgnoreKind) {
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
        globalPlay.setProgress((float) i/(float) i1);
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }

    @Override
    public void onStartGetAdsInfo() {

    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {

    }

    @Override
    public void onAdsStartBuffering() {
    globalPlay.setProgress(0);
    }

    @Override
    public void onAdsStopBuffering() {

    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        Log.e(TAG, "onStartPlayAds: "+advertis );
        String imageUrl = advertis.getImageUrl();
        if(TextUtils.isEmpty(imageUrl)){
            globalPlay.play(R.drawable.notification_default);
        }else {
            globalPlay.play(imageUrl);
        }
    }

    @Override
    public void onCompletePlayAds() {

    }

    @Override
    public void onError(int i, int i1) {

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
            App.registerLoginTokenChangeListener(MainActivity.this.getApplicationContext());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmPlayerManager.getInstance(this).removePlayerStatusListener(this);
        XmPlayerManager.getInstance(this).removeAdsStatusListener(this);
    }
}
