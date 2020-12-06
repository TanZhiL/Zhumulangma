package com.gykj.zhumulangma.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TFragmentStateAdapter;
import com.gykj.zhumulangma.common.aop.LoginHelper;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmActivity;
import com.gykj.zhumulangma.common.mvvm.view.status.LoadingStatus;
import com.gykj.zhumulangma.common.util.PermissionPageUtil;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.widget.GlobalPlay;
import com.gykj.zhumulangma.main.databinding.MainActivityMainBinding;
import com.gykj.zhumulangma.main.dialog.SplashAdPopup;
import com.gykj.zhumulangma.main.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.main.mvvm.viewmodel.MainViewModel;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.next.easynavigation.utils.NavigationUtil;
import com.next.easynavigation.view.EasyNavigationBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Route(path = Constants.Router.Main.A_MAIN)
public class MainActivity extends BaseMvvmActivity<MainActivityMainBinding,MainViewModel> implements View.OnClickListener,
         EasyNavigationBar.OnTabClickListener {
    private XmPlayerManager mPlayerManager = XmPlayerManager.getInstance(this);
    private PlayHistoryBean mHistoryBean;
    private GlobalPlay globalplay;

    private String[] tabText = {"首页", "我听", "发现", "我的"};

    private @DrawableRes
    int[] normalIcon = {R.drawable.main_tab_home_normal, R.drawable.main_tab_litsten_normal
            , R.drawable.main_tab_find_normal, R.drawable.main_tab_user_normal};
    private @DrawableRes
    int[] selectIcon = {R.drawable.main_tab_home_press, R.drawable.main_tab_listen_press
            ,R.drawable.main_tab_find_press, R.drawable.main_tab_user_press};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

    @Override
    public int onBindLayout() {
        return R.layout.main_activity_main;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    /**
     * 显示广告
     */
    private void initAd() {
        mViewModel.initAd();
    }

    @Override
    public void initView() {
        //手动添加布局,减少布局层级
        globalplay = new GlobalPlay(this);
        globalplay.setRadius(NavigationUtil.dip2px(this, 19));
        globalplay.setBarWidth(NavigationUtil.dip2px(this, 2));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(NavigationUtil.dip2px(this, 50), NavigationUtil.dip2px(this, 50));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        ((ViewGroup) mBinding.getRoot()).addView(globalplay, layoutParams);

        List<Fragment> fragments = new ArrayList<>();
        Object home = mRouter.build(Constants.Router.Home.F_MAIN).navigation();
        if (null != home) {
            fragments.add((Fragment) home);
        }
        Object listen = mRouter.build(Constants.Router.Listen.F_MAIN).navigation();
        if (null != listen) {
            fragments.add((Fragment) listen);
        }
        Object discover = mRouter.build(Constants.Router.Discover.F_MAIN).navigation();
        if (null != listen) {
            fragments.add((Fragment) discover);
        }
        Object user = mRouter.build(Constants.Router.User.F_MAIN).navigation();
        if (null != listen) {
            fragments.add((Fragment) user);
        }

        mBinding.vp.setOffscreenPageLimit(fragments.size());
        mBinding.vp.setAdapter(new TFragmentStateAdapter(this, fragments));
        mBinding.vp.setUserInputEnabled(false);
        mBinding.enb.defaultSetting()
                .setupWithViewPager(mBinding.vp)
                .titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .lineHeight(1)
                .mode(EasyNavigationBar.NavigationMode.MODE_ADD)
                .centerImageRes(R.drawable.shap_third_white_coner)
                .fragmentManager(getSupportFragmentManager())
                .normalTextColor(getResources().getColor(R.color.colorGray))   //Tab未选中时字体颜色
                .selectTextColor(getResources().getColor(R.color.colorPrimary))   //Tab选中时字体颜色
                .tabTextSize(11)   //Tab文字大小
                .iconSize(27)
                .centerIconSize(0)//取消中间图标
                .navigationHeight(50)
                .setOnTabClickListener(this)
                .build();

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
        mViewModel.getHistoryEvent().observe(this, bean -> {
            mHistoryBean = bean;
            if (bean.getKind().equals(PlayableModel.KIND_TRACK)) {
                globalplay.setImage(TextUtils.isEmpty(bean.getTrack().getCoverUrlSmall())
                        ? bean.getTrack().getAlbum().getCoverUrlLarge() : bean.getTrack().getCoverUrlSmall());
                globalplay.setProgress(1.0f * bean.getPercent() / 100);
            } else {
                globalplay.setImage(bean.getSchedule().getRelatedProgram().getBackPicUrl());
            }
        });
        mViewModel.getCoverEvent().observe(this, s -> globalplay.play(s));
        mViewModel.getShowAdEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                new XPopup.Builder(MainActivity.this).customAnimator(new SplashAdPopup.AlphaAnimator())
                        .setPopupCallback(new SimpleCallback() {
                            @Override
                            public void onDismiss(BasePopupView popupView) {
                                super.onDismiss(popupView);
                                mViewModel.adDissmiss();
                            }

                            @Override
                            public boolean onBackPressed(BasePopupView popupView) {
                                ActivityUtils.startHomeActivity();
                                return true;
                            }
                        })
                        .asCustom(new SplashAdPopup(MainActivity.this)).show();
            }
        });
    }


    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == globalplay) {
            if (null == mPlayerManager.getCurrSound(true)) {
                if (mHistoryBean == null) {
                    RouteHelper.navigateTo(Constants.Router.Home.F_RANK);
                } else {
                    mViewModel.play(mHistoryBean);
                }
            } else {
                mPlayerManager.play();
                if (mPlayerManager.getCurrSound().getKind().equals(PlayableModel.KIND_TRACK)) {
                    RouteHelper.navigateTo(Constants.Router.Home.F_PLAY_TRACK);

                } else if (mPlayerManager.getCurrSound().getKind().equals(PlayableModel.KIND_SCHEDULE)) {
                    RouteHelper.navigateTo(Constants.Router.Home.F_PLAY_RADIIO);
                }
            }
        }
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
    public void onBackPressed() {
        //如果正在显示loading,则清除
        if (mBaseLoadService.getCurrentCallback() == LoadingStatus.class) {
            clearStatus();
            return;
        }
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            ActivityUtils.startHomeActivity();
        }
    }
/*
    @Override
    public void onEvent(ActivityEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
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
                config.setCancelButtonVisibility(true);
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
    }*/



    @Override
    public boolean onTabSelectEvent(View view, int position) {
        return false;
    }

    @Override
    public boolean onTabReSelectEvent(View view, int position) {
        switch (position){
            case 0:
                EventBus.getDefault().post(new FragmentEvent(EventCode.Home.TAB_REFRESH));
                break;
            case 1:
                EventBus.getDefault().post(new FragmentEvent(EventCode.Listen.TAB_REFRESH));
                break;
            case 2:
                EventBus.getDefault().post(new FragmentEvent(EventCode.Discover.TAB_REFRESH));
                break;
            case 3:
                EventBus.getDefault().post(new FragmentEvent(EventCode.User.TAB_REFRESH));
                break;
        }
        return false;
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
    public void onDestroy() {
        super.onDestroy();
        mPlayerManager.removePlayerStatusListener(playerStatusListener);
        mPlayerManager.removeAdsStatusListener(adsStatusListener);
        UMShareAPI.get(this).release();
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
