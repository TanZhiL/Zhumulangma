package com.gykj.zhumulangma.common.mvvm.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.AppHelper;
import com.gykj.zhumulangma.common.R;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.status.EmptyCallback;
import com.gykj.zhumulangma.common.status.ErrorCallback;
import com.gykj.zhumulangma.common.status.InitLoadingCallback;
import com.gykj.zhumulangma.common.status.LoadingCallback;
import com.gykj.zhumulangma.common.util.SystemUtil;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Description: <BaseActivity><br>
 * Author:      mxdl<br>
 * Date:        2019/06/30<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public abstract class BaseActivity extends SupportActivity implements IBaseView {

    protected static final String TAG = BaseActivity.class.getSimpleName();

    private CompositeDisposable mCompositeDisposable;
    private Handler mLoadingHandler=new Handler();
    protected LoadService mLoadService;
    protected CommonTitleBar mSimpleTitleBar;
    protected App mApplication;

    interface BarStyle {
        //左边
        int LEFT_BACK = 0;
        int LEFT_BACK_TEXT = 1;
        int LEFT_ICON = 2;

        int CENTER_TITLE = 7;
        int CENTER_CUSTOME = 8;

        //右边
        int RIGHT_TEXT = 4;
        int RIGHT_ICON = 5;
        int RIGHT_CUSTOME = 6;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyboardUtils.fixSoftInputLeaks(this);
        mApplication= App.getInstance();
        setContentView(R.layout.common_layout_root);
        EventBus.getDefault().register(this);
        ARouter.getInstance().inject(this);
        initCommonView();
        initView();
        initListener();
        initParam();
        initData();
    }

    /**
     * 添加订阅
     */
    protected void addDisposable(Disposable mDisposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(mDisposable);
    }
    /**
     * 取消所有订阅
     */
    private void clearDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
    protected void initCommonView() {

        ViewStub viewStubContent = findViewById(R.id.view_stub_content);
        mSimpleTitleBar = findViewById(R.id.ctb_simple);
        if (enableSimplebar()) {
            mSimpleTitleBar.setBackgroundResource(R.drawable.shap_common_simplebar);
            mSimpleTitleBar.setVisibility(View.VISIBLE);
            initSimplebar();
        }
        viewStubContent.setLayoutResource(onBindLayout());
        View contentView = viewStubContent.inflate();

        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new InitLoadingCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new ErrorCallback())
                .addCallback(new LoadingCallback())
                .setDefaultCallback(LoadingCallback.class)
                .build();
        mLoadService = loadSir.register(onBindLoadSirView() == null ? contentView : onBindLoadSirView(), new Callback.OnReloadListener() {
            @Override
            public void onReload(View v) {
                BaseActivity.this.onReload(v);
            }
        });
        mLoadService.showSuccess();
    }

    protected View onBindLoadSirView() {
        return null;
    }

    protected void initSimplebar() {
        /**
         * 中间
         */
        if (onBindBarCenterStyle() == BarStyle.CENTER_TITLE) {
            String[] strings = onBindBarTitleText();
            if (strings != null && strings.length > 0) {
                if (strings.length > 0 && null != strings[0] && strings[0].trim().length() > 0) {
                    TextView title = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.tv_title);
                    title.setVisibility(View.VISIBLE);
                    title.setText(strings[0]);
                }
                if (strings.length > 1 && null != strings[1] && strings[1].trim().length() > 0) {
                    TextView subtitle = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.tv_subtitle);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(strings[1]);
                }
            }
        } else if (onBindBarCenterStyle() == BarStyle.CENTER_CUSTOME && onBindBarCenterCustome() != null) {
            ViewGroup group = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.fl_custome);
            group.setVisibility(View.VISIBLE);
            group.addView(onBindBarCenterCustome(), new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        /**
         * 左侧
         */
        if (onBindBarLeftStyle() == BarStyle.LEFT_BACK) {
            View backView = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            backView.setVisibility(View.VISIBLE);
            backView.setOnClickListener(v -> finish());
        } else if (onBindBarLeftStyle() == BarStyle.LEFT_BACK_TEXT) {
            View backIcon = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            backIcon.setVisibility(View.VISIBLE);
            backIcon.setOnClickListener(v -> finish());
            View backTv = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.tv_back);
            backTv.setVisibility(View.VISIBLE);
            backTv.setOnClickListener(v -> finish());
        } else if (onBindBarLeftStyle() == BarStyle.LEFT_ICON && onBindBarLeftIcon() != null) {
            ImageView icon = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_left);
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(onBindBarLeftIcon());
            icon.setOnClickListener(v -> onLeftIconClick(v));
        }
        /**
         * 右侧
         */
        switch (onBindBarRightStyle()) {
            case BarStyle.RIGHT_TEXT:
                String[] strings = onBindBarRightText();
                if (strings == null || strings.length == 0) {
                    break;
                }
                if (strings.length > 0 && null != strings[0] && strings[0].trim().length() > 0) {
                    TextView tv1 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.tv1_right);
                    tv1.setVisibility(View.VISIBLE);
                    tv1.setText(strings[0]);
                    tv1.setOnClickListener(v -> onRight1Click(v));
                }
                if (strings.length > 1 && null != strings[1] && strings[1].trim().length() > 0) {
                    TextView tv2 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.tv2_right);
                    tv2.setVisibility(View.VISIBLE);
                    tv2.setText(strings[1]);
                    tv2.setOnClickListener(v -> onRight2Click(v));
                }
                break;
            case BarStyle.RIGHT_ICON:
                Integer[] ints = onBindBarRightIcon();
                if (ints == null || ints.length == 0) {
                    break;
                }
                if (ints.length > 0 && null != ints[0]) {
                    ImageView iv1 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.iv_left);
                    iv1.setVisibility(View.VISIBLE);
                    iv1.setImageResource(ints[0]);
                    iv1.setOnClickListener(v -> onRight1Click(v));
                }
                if (ints.length > 1 & null != ints[1]) {
                    ImageView iv2 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.iv2_right);
                    iv2.setVisibility(View.VISIBLE);
                    iv2.setImageResource(ints[1]);
                    iv2.setOnClickListener(v -> onRight2Click(v));
                }
                break;
            case BarStyle.RIGHT_CUSTOME:
                if (onBindBarRightCustome() != null) {
                    ViewGroup group = mSimpleTitleBar.getRightCustomView().findViewById(R.id.fl_custome);
                    group.setVisibility(View.VISIBLE);
                    group.addView(onBindBarRightCustome(), new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                break;
        }

    }

    protected int onBindBarRightStyle() {
        return BarStyle.RIGHT_TEXT;
    }

    protected int onBindBarLeftStyle() {
        return BarStyle.LEFT_BACK_TEXT;
    }

    protected int onBindBarCenterStyle() {
        return BarStyle.CENTER_TITLE;
    }

    protected String[] onBindBarRightText() {
        return null;
    }

    protected String[] onBindBarTitleText() {
        return null;
    }

    protected @DrawableRes
    Integer[] onBindBarRightIcon() {
        return null;
    }

    protected @DrawableRes
    Integer onBindBarLeftIcon() {
        return null;
    }

    protected View onBindBarRightCustome() {
        return null;
    }

    protected View onBindBarCenterCustome() {
        return null;
    }

    protected void setSimpleBarBg(@ColorInt int color) {
        mSimpleTitleBar.setBackgroundColor(color);
    }

    protected void setTitle(String[] strings) {
        if (!enableSimplebar()) {
            throw new IllegalStateException("导航栏中不可用,请设置enableSimplebar为true");
        } else if (onBindBarCenterStyle() != BarStyle.CENTER_TITLE) {
            throw new IllegalStateException("导航栏中间布局不为标题类型,请设置onBindBarCenterStyle(BarStyle.CENTER_TITLE)");
        } else {
            if (strings != null && strings.length > 0) {
                if (strings.length > 0 && null != strings[0] && strings[0].trim().length() > 0) {
                    TextView title = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.tv_title);
                    title.setVisibility(View.VISIBLE);
                    title.setText(strings[0]);
                }
                if (strings.length > 1 && null != strings[1] && strings[1].trim().length() > 0) {
                    TextView subtitle = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.tv_subtitle);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(strings[1]);
                }
            }
        }
    }

    protected void setBarTextColor(@ColorInt int color) {
        if (!enableSimplebar()) {
            throw new IllegalStateException("导航栏中不可用,请设置enableSimplebar为true");
        } else {
            TextView tvBack = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.tv_back);
            tvBack.setTextColor(color);
            TextView tvTitle = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.tv_title);
            tvTitle.setTextColor(color);
            TextView tvSubtitle = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.tv_subtitle);
            tvSubtitle.setTextColor(color);
            TextView tv1 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.tv1_right);
            tv1.setTextColor(color);
            TextView tv2 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.tv2_right);
            tv2.setTextColor(color);
        }
    }

    protected void setBarBackIconRes(@DrawableRes int res) {
        if (!enableSimplebar()) {
            throw new IllegalStateException("导航栏中不可用,请设置enableSimplebar为true");
        } else {
            ImageView ivBack = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            ivBack.setImageResource(res);
        }
    }

    protected void onRight1Click(View v) {

    }

    protected void onRight2Click(View v) {

    }

    protected void onLeftIconClick(View v) {

    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (!enableSimplebar()) {
            return;
        }
        if (onBindBarCenterStyle() == BarStyle.CENTER_TITLE && !TextUtils.isEmpty(title)) {
            TextView tvtitle = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.tv_title);
            tvtitle.setVisibility(View.VISIBLE);
            tvtitle.setText(title);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        clearDisposable();
        SystemUtil.fixInputMethodManagerLeak(this);
        AppHelper.refWatcher.watch(this);
    }


    protected abstract int onBindLayout();

    public abstract void initView();

    public abstract void initData();

    public void initListener() {
    }

    protected void initParam() {
    }



    protected boolean enableSimplebar() {
        return true;
    }


    public void showInitView(boolean show) {
        if (!show) {
            mLoadService.showSuccess();
        } else {
            mLoadService.showCallback(InitLoadingCallback.class);
        }
    }


    public void showNetErrView(boolean show) {
        if (!show) {
            mLoadService.showSuccess();
        } else {
            mLoadService.showCallback(ErrorCallback.class);
        }
    }


    public void showNoDataView(boolean show) {
        if (!show) {
            mLoadService.showSuccess();
        } else {
            mLoadService.showCallback(EmptyCallback.class);
        }
    }


    public void showLoadingView(String tip) {
        if (null == tip) {
            mLoadingHandler.removeCallbacksAndMessages(null);
            mLoadService.showSuccess();
        } else {

            mLoadService.setCallBack(LoadingCallback.class, (context, view1) -> {
                TextView tvTip = view1.findViewById(R.id.tv_tip);
                if(tip.length()==0){
                    tvTip.setVisibility(View.GONE);
                }else {
                    tvTip.setText(tip);
                }
            });
            //延时100毫秒显示,避免闪屏
            mLoadingHandler.postDelayed(() -> mLoadService.showCallback(LoadingCallback.class), 100);
        }
    }

    protected void onReload(View v) {
//        mLoadService.showCallback(InitLoadingCallback.class);
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public <T> void onEvent(BaseActivityEvent<T> event) {
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public <T> void onEventSticky(BaseActivityEvent<T> event) {
    }
    @Override
    public Context getContext() {
        return this;
    }

    /**
     * findViewById
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T fd(@IdRes int id) {
        return findViewById(id);
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }
    protected void navigateTo(String path){
        Object navigation = ARouter.getInstance().build(path).navigation();
        if (null != navigation) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }
    protected void navigateTo(String path,int launchMode){
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.launchMode=launchMode;
        if (null != navigation) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }
    protected void navigateTo(String path, ExtraTransaction extraTransaction){
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.extraTransaction=extraTransaction;
        if (null != navigation) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }
    protected void navigateTo(String path, int launchMode, ExtraTransaction extraTransaction){
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.launchMode=launchMode;
        navigateBean.extraTransaction=extraTransaction;
        if (null != navigation) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }

    /**
     * 解决键盘遮挡EditText
     */
/*    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyboardConflictCompat.assistWindow(getWindow());
    }*/

}
