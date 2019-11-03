package com.gykj.zhumulangma.main;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.aop.LoginHelper;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmActivity;
import com.gykj.zhumulangma.common.mvvm.view.status.LoadingStatus;
import com.gykj.zhumulangma.common.util.PermissionPageUtil;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.widget.GlobalPlay;
import com.gykj.zhumulangma.main.dialog.SplashAdPopup;
import com.gykj.zhumulangma.main.fragment.MainFragment;
import com.gykj.zhumulangma.main.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.main.mvvm.viewmodel.MainViewModel;
import com.kingja.loadsir.core.LoadLayout;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;


@Route(path = Constants.Router.Main.A_MAIN)
public class MainActivity extends BaseMvvmActivity<MainViewModel> implements View.OnClickListener,
        MainFragment.onRootShowListener {
    private XmPlayerManager mPlayerManager = XmPlayerManager.getInstance(this);
    private PlayHistoryBean mHistoryBean;
    private GlobalPlay globalplay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //清除全屏显示
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去除背景色,避免过度绘制
        setTheme(R.style.NullTheme);
        super.onCreate(savedInstanceState);
        initAd();
        //申请权限
        new RxPermissions(this).request(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE})
                .subscribe(granted -> {
                    if (!granted) {
                        new XPopup.Builder(this).dismissOnTouchOutside(false)
                                .dismissOnBackPressed(false)
                                .asConfirm("提示", "权限不足,请允许珠穆朗玛听获取权限",
                                        () -> {
                                            new PermissionPageUtil(this).jumpPermissionPage();
                                            AppUtils.exitApp();
                                        }, AppUtils::exitApp)
                                .show();
                    }
                });

    }

    /**
     * 显示广告
     */
    private void initAd() {
        long adOffset = System.currentTimeMillis() - SPUtils.getInstance().getLong(Constants.SP.AD_TIME, 0);
        //显示广告
        if (adOffset > 5 * 60 * 1000 && new File(getFilesDir().getAbsolutePath() + Constants.Default.AD_NAME)
                .exists()) {
            new XPopup.Builder(this).customAnimator(new SplashAdPopup.AlphaAnimator())
                    .setPopupCallback(new SimpleCallback() {
                        @Override
                        public void onDismiss() {
                            super.onDismiss();
                            SPUtils.getInstance().put(Constants.SP.AD_TIME, System.currentTimeMillis());
                            mViewModel.getBing();
                        }

                        @Override
                        public boolean onBackPressed() {
                            ActivityUtils.startHomeActivity();
                            return true;
                        }
                    })
                    .asCustom(new SplashAdPopup(this)).show();
        } else if (!new File(getFilesDir().getAbsolutePath() + Constants.Default.AD_NAME)
                .exists()) {
            mViewModel.getBing();
        }
    }

    @Override
    public void initView() {
        //手动添加布局,减少布局层级
        globalplay = new GlobalPlay(this);
        if (findFragment(MainFragment.class) == null) {
            MainFragment mainFragment = new MainFragment();
            mainFragment.setShowListener(this);
            loadRootFragment(android.R.id.content, mainFragment);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(SizeUtils.dp2px(57), SizeUtils.dp2px(57));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        LoadLayout loadLayout = mBaseLoadService.getLoadLayout();
        ((ViewGroup)loadLayout.getParent().getParent()).addView(globalplay, layoutParams);
    }

    @Override
    public void initListener() {
        globalplay.setOnClickListener(this);
        mPlayerManager.addPlayerStatusListener(playerStatusListener);
        mPlayerManager.addAdsStatusListener(adsStatusListener);
    }

    @Override
    public void initData() {
        mHandler.postDelayed(() -> {
            if (XmPlayerManager.getInstance(MainActivity.this).isPlaying()) {
                Track currSoundIgnoreKind = XmPlayerManager.getInstance(MainActivity.this).getCurrSoundIgnoreKind(true);
                if (null == currSoundIgnoreKind) {
                    return;
                }
                globalplay.play(TextUtils.isEmpty(currSoundIgnoreKind.getCoverUrlSmall())
                        ? currSoundIgnoreKind.getAlbum().getCoverUrlLarge() : currSoundIgnoreKind.getCoverUrlSmall());
            } else {
                mViewModel.getLastSound();
            }
        }, 100);

    }

    @Override
    public void initViewObservable() {
        mViewModel.getHistorySingleLiveEvent().observe(this, bean -> {
            mHistoryBean = bean;
            if (bean.getKind().equals(PlayableModel.KIND_TRACK)) {
                globalplay.setImage(TextUtils.isEmpty(bean.getTrack().getCoverUrlSmall())
                        ? bean.getTrack().getAlbum().getCoverUrlLarge() : bean.getTrack().getCoverUrlSmall());
                globalplay.setProgress(1.0f * bean.getPercent() / 100);
            } else {
                globalplay.setImage(bean.getSchedule().getRelatedProgram().getBackPicUrl());
            }
        });
        mViewModel.getCoverSingleLiveEvent().observe(this, s -> globalplay.play(s));
    }


    @Override
    public void onClick(View v) {
        if (v == globalplay) {
            if (null == mPlayerManager.getCurrSound(true)) {
                if (mHistoryBean == null) {
                    RouteUtil.navigateTo(Constants.Router.Home.F_RANK);
                } else {
                    mViewModel.play(mHistoryBean);
                }
            } else {
                mPlayerManager.play();
                if (mPlayerManager.getCurrSound().getKind().equals(PlayableModel.KIND_TRACK)) {
                    RouteUtil.navigateTo(Constants.Router.Home.F_PLAY_TRACK);

                } else if (mPlayerManager.getCurrSound().getKind().equals(PlayableModel.KIND_SCHEDULE)) {
                    RouteUtil.navigateTo(Constants.Router.Home.F_PLAY_RADIIO);
                }
            }
        }
    }

    @Override
    public void onRootShow(boolean isVisible) {
        if (isVisible)
            globalplay.setBackgroundColor(Color.TRANSPARENT);
        else
            globalplay.setBackground(getResources().getDrawable(R.drawable.shap_main_globalplay));
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
        globalplay.setProgress((float) cur / (float) dur);
    }


    @Override
    public Class<MainViewModel> onBindViewModel() {
        return MainViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(getApplication());
    }


    private long exitTime = 0;

    @Override
    public void onBackPressedSupport() {
        //如果正在显示loading,则清除
        if (mBaseLoadService.getCurrentCallback() == LoadingStatus.class) {
            clearStatus();
            return;
        }
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
    public void onEvent(ActivityEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Main.NAVIGATE:
                NavigateBean navigateBean = (NavigateBean) event.getData();
                if (null == navigateBean.fragment) {
                    return;
                }
                switch (navigateBean.path) {
                    case Constants.Router.User.F_MESSAGE:
                        //登录拦截
                        if (!AccessTokenManager.getInstanse().hasLogin()) {
                            LoginHelper.getInstance().login(this);
                        } else {
                            start(navigateBean.fragment);
                        }
                        break;
                    case Constants.Router.Home.F_PLAY_TRACK:
                    case Constants.Router.Home.F_PLAY_RADIIO:
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
                globalplay.hide();
                break;
            case EventCode.Main.SHOW_GP:
                globalplay.show();
                break;
            case EventCode.Main.SHARE:

                ShareBoardConfig config = new ShareBoardConfig();
                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
                config.setCancelButtonVisibility(true);
                config.setTitleVisibility(false);
                config.setCancelButtonVisibility(false);
                config.setIndicatorVisibility(false);

                ShareAction action = (ShareAction) event.getData();

                if (action == null) {
                    UMWeb web = new UMWeb("https://github.com/TanZhiL/Zhumulangma");
                    web.setTitle("珠穆朗玛听");//标题
                    web.setThumb(new UMImage(this, R.drawable.third_launcher_ting));  //缩略图
                    web.setDescription("珠穆朗玛听");//描述
                    action = new ShareAction(this).withMedia(web);
                }
                action.setDisplayList(
                        SHARE_MEDIA.WEIXIN,
                        SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.QQ,
                        SHARE_MEDIA.QZONE,
                        SHARE_MEDIA.SINA)
                        .setCallback(uMShareListener).open(config);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //分享回调
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (LoginHelper.getInstance().getSsoHandler() != null) {
            LoginHelper.getInstance().getSsoHandler().authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPlayerManager.removePlayerStatusListener(playerStatusListener);
        mPlayerManager.removeAdsStatusListener(adsStatusListener);
        UMShareAPI.get(this).release();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    private IXmPlayerStatusListener playerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onPlayStart() {
            Track currSoundIgnoreKind = mPlayerManager.getCurrSoundIgnoreKind(true);
            if (null == currSoundIgnoreKind) {
                return;
            }
            globalplay.play(TextUtils.isEmpty(currSoundIgnoreKind.getCoverUrlSmall())
                    ? currSoundIgnoreKind.getAlbum().getCoverUrlLarge() : currSoundIgnoreKind.getCoverUrlSmall());
        }

        @Override
        public void onPlayPause() {
            globalplay.pause();
        }

        @Override
        public void onPlayStop() {
            globalplay.pause();
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

        @Override
        public boolean onError(XmPlayerException e) {
            return false;
        }
    };
    private IXmAdsStatusListener adsStatusListener = new IXmAdsStatusListener() {

        @Override
        public void onStartGetAdsInfo() {

        }

        @Override
        public void onGetAdsInfo(AdvertisList advertisList) {

        }

        @Override
        public void onAdsStartBuffering() {
            globalplay.setProgress(0);
        }

        @Override
        public void onAdsStopBuffering() {

        }

        @Override
        public void onStartPlayAds(Advertis advertis, int i) {
            String imageUrl = advertis.getImageUrl();
            if (TextUtils.isEmpty(imageUrl)) {
                globalplay.play(R.drawable.notification_default);
            } else {
                globalplay.play(imageUrl);
            }
        }

        @Override
        public void onCompletePlayAds() {

        }

        @Override
        public void onError(int i, int i1) {

        }
    };
    private UMShareListener uMShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            ToastUtil.showToast(ToastUtil.LEVEL_S, "分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Log.d(TAG, "onError() called with: share_media = [" + share_media + "], throwable = [" + throwable + "]");
            ToastUtil.showToast(ToastUtil.LEVEL_W, throwable.getLocalizedMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtil.showToast(ToastUtil.LEVEL_W, "分享取消");
        }
    };
}
