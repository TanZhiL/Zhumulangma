package com.gykj.zhumulangma.main;

import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.AppHelper;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.event.common.BaseFragmentEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmActivity;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.widget.GlobalPlay;
import com.gykj.zhumulangma.main.dialog.SplashAdPopup;
import com.gykj.zhumulangma.main.fragment.MainFragment;
import com.gykj.zhumulangma.main.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.main.mvvm.viewmodel.MainViewModel;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.SimpleCallback;
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
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

import static com.gykj.zhumulangma.common.AppConstants.Ximalaya.REDIRECT_URL;

@Route(path = AppConstants.Router.Main.A_MAIN)
public class MainActivity extends BaseMvvmActivity<MainViewModel> implements View.OnClickListener,
        MainFragment.onRootShowListener, IXmPlayerStatusListener, IXmAdsStatusListener {
    private XmPlayerManager mPlayerManager = XmPlayerManager.getInstance(this);
    private XmlyAuthInfo mAuthInfo;
    private XmlySsoHandler mSsoHandler;
    private XmlyAuth2AccessToken mAccessToken;
    private PlayHistoryBean mHistoryBean;
    private GlobalPlay globalPlay;

    @Override
    protected int onBindLayout() {
        return R.layout.main_activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //清除全屏显示
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.d(TAG, "onCreate() called "+System.currentTimeMillis());
        super.onCreate(savedInstanceState);    //布局优化
        long adOffset = System.currentTimeMillis() - SPUtils.getInstance().getLong(AppConstants.SP.AD_TIME, 0);
        //显示广告
        if(adOffset>5*60*1000&&new File(getFilesDir().getAbsolutePath()+AppConstants.Defualt.AD_NAME)
                .exists()){
            new XPopup.Builder(this).customAnimator(new SplashAdPopup.AlphaAnimator())
                    .setPopupCallback(new SimpleCallback() {
                        @Override
                        public void onDismiss() {
                            super.onDismiss();
                             SPUtils.getInstance().put(AppConstants.SP.AD_TIME,System.currentTimeMillis());
                            mViewModel._getBing();
                        }

                        @Override
                        public boolean onBackPressed() {
                            ActivityUtils.startHomeActivity();
                            return true;
                        }
                    })
                    .asCustom(new SplashAdPopup(this)).show();
        }else if(!new File(getFilesDir().getAbsolutePath()+AppConstants.Defualt.AD_NAME)
                .exists()){
            mViewModel._getBing();
        }
        setTheme(R.style.NullTheme);
        Log.d(TAG, "onCreate() called "+System.currentTimeMillis());
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Log.d(TAG, "onWindowFocusChanged() called "+System.currentTimeMillis());
        }
    }
    @Override
    public void initListener() {
        globalPlay.setOnClickListener(this);
        mPlayerManager.addPlayerStatusListener(this);
        mPlayerManager.addAdsStatusListener(this);
    }

    @Override
    public void initData() {
        new Handler().postDelayed(() -> {
            if (XmPlayerManager.getInstance(MainActivity.this).isPlaying()) {
                Track currSoundIgnoreKind = XmPlayerManager.getInstance(MainActivity.this).getCurrSoundIgnoreKind(true);
                if (null == currSoundIgnoreKind) {
                    return;
                }
                globalPlay.play(TextUtils.isEmpty(currSoundIgnoreKind.getCoverUrlSmall())
                        ?currSoundIgnoreKind.getAlbum().getCoverUrlLarge():currSoundIgnoreKind.getCoverUrlSmall());
            }else {
                mViewModel.getLastSound();
            }
        }, 100);

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }


    @Override
    public void onClick(View v) {

        if (v == globalPlay) {
            if (null == mPlayerManager.getCurrSound(true)) {
                if(mHistoryBean==null){

                    navigateTo(AppConstants.Router.Home.F_RANK,extraTransaction().setCustomAnimations(
                            com.gykj.zhumulangma.common.R.anim.push_bottom_in,
                            com.gykj.zhumulangma.common.R.anim.no_anim,
                            com.gykj.zhumulangma.common.R.anim.no_anim,
                            com.gykj.zhumulangma.common.R.anim.push_bottom_out));
                }else {
                    mViewModel.play(mHistoryBean);
                }
            } else {
                mPlayerManager.play();
                if (mPlayerManager.getCurrSound().getKind().equals(PlayableModel.KIND_TRACK)) {
                    navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);

                } else if (mPlayerManager.getCurrSound().getKind().equals(PlayableModel.KIND_SCHEDULE)) {
                    navigateTo(AppConstants.Router.Home.F_PLAY_RADIIO);
                }
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


    @Override
    public void onPlayStart() {
        Track currSoundIgnoreKind = mPlayerManager.getCurrSoundIgnoreKind(true);
        if (null == currSoundIgnoreKind) {
            return;
        }
        globalPlay.play(TextUtils.isEmpty(currSoundIgnoreKind.getCoverUrlSmall())
                ?currSoundIgnoreKind.getAlbum().getCoverUrlLarge():currSoundIgnoreKind.getCoverUrlSmall());
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
        initProgress(i, i1);
    }

    private void initProgress(int cur, int dur) {
        if (mPlayerManager.getCurrPlayType() == XmPlayListControl.PLAY_SOURCE_RADIO) {
            try {
                Schedule schedule = (Schedule) mPlayerManager.getCurrSound();
                if (BaseUtil.isInTime(schedule.getStartTime() + "-" + schedule.getEndTime()) == 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:HH:mm", Locale.getDefault());
                    long start = sdf.parse(schedule.getStartTime()).getTime();
                    long end = sdf.parse(schedule.getEndTime()).getTime();
                    cur = (int) (System.currentTimeMillis() - start);
                    dur = (int) (end - start);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        globalPlay.setProgress((float)cur /(float)dur);
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
        String imageUrl = advertis.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            globalPlay.play(R.drawable.notification_default);
        } else {
            globalPlay.play(imageUrl);
        }
    }

    @Override
    public void onCompletePlayAds() {

    }

    @Override
    public void onError(int i, int i1) {

    }

    @Override
    public Class<MainViewModel> onBindViewModel() {
        return MainViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getHistorySingleLiveEvent().observe(this, bean -> {
            mHistoryBean=bean;
            if(bean.getKind().equals(PlayableModel.KIND_TRACK)){
                globalPlay.setImage(TextUtils.isEmpty(bean.getTrack().getCoverUrlSmall())
                        ?bean.getTrack().getAlbum().getCoverUrlLarge():bean.getTrack().getCoverUrlSmall());
                globalPlay.setProgress(bean.getPercent());
            }else {
                globalPlay.setImage(bean.getSchedule().getRelatedProgram().getBackPicUrl());
            }
        });
        mViewModel.getCoverSingleLiveEvent().observe(this, s -> globalPlay.play(s));
    }



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
                ActivityUtils.startHomeActivity();
            }
        }
    }

    @Override
    public <T> void onEvent(BaseActivityEvent<T> event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Main.NAVIGATE:
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
                    case AppConstants.Router.Home.F_PLAY_RADIIO:
                        extraTransaction().setCustomAnimations(
                                com.gykj.zhumulangma.common.R.anim.push_bottom_in,
                                com.gykj.zhumulangma.common.R.anim.no_anim,
                                com.gykj.zhumulangma.common.R.anim.no_anim,
                                com.gykj.zhumulangma.common.R.anim.push_bottom_out).start(
                                navigateBean.fragment, ISupportFragment.SINGLETASK);
                        break;
                    default:
                        if (navigateBean.extraTransaction != null) {
                            navigateBean.extraTransaction.start(navigateBean.fragment, navigateBean.launchMode);
                        } else {
                            start(navigateBean.fragment, navigateBean.launchMode);
                        }
                        break;
                }
                break;
            case EventCode.Main.HIDE_GP:
                globalPlay.hide();
                break;
            case EventCode.Main.SHOW_GP:
                globalPlay.show();
                break;
            case EventCode.Main.LOGIN:
                goLogin();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerManager.removePlayerStatusListener(this);
        mPlayerManager.removeAdsStatusListener(this);
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

    class CustomAuthListener implements IXmlyAuthListener {
        @Override
        public void onComplete(Bundle bundle) {
            parseAccessToken(bundle);
            AppHelper.registerLoginTokenChangeListener(MainActivity.this.getApplicationContext());
            EventBus.getDefault().post(new BaseFragmentEvent<>(EventCode.Main.LOGINSUCC));
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

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

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
