package com.gykj.zhumulangma.common.mvvm.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.AppHelper;
import com.gykj.zhumulangma.common.R;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.event.common.BaseFragmentEvent;
import com.gykj.zhumulangma.common.status.EmptyCallback;
import com.gykj.zhumulangma.common.status.ErrorCallback;
import com.gykj.zhumulangma.common.status.InitCallback;
import com.gykj.zhumulangma.common.status.LoadingCallback;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.callback.SuccessCallback;
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
 * Author: Thomas.
 * Date: 2019/9/10 8:23
 * Email: 1071931588@qq.com
 * Description:Fragment基类
 */
public abstract class BaseFragment extends SupportFragment implements IBaseView {
    protected static final String TAG = BaseFragment.class.getSimpleName();
    private CompositeDisposable mCompositeDisposable;
    protected Context mContext;
    protected View mView;
    private ViewStub mViewStubContent;
    protected LoadService mLoadService;
    protected CommonTitleBar mSimpleTitleBar;
    private Handler mLoadingHandler = new Handler();
    protected App mApplication;

    private boolean isFirst = true;

    protected interface BarStyle {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = App.getInstance();
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.common_layout_root, container, false);
        mView.setBackgroundColor(Color.WHITE);
        initCommonView(mView);
        initParam();
        //不采用懒加载
        if (!lazyEnable()) {
            loadView();
            initView(mView);
            initListener();
        }
        return attachToSwipeBack(mView);
    }


    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        //采用懒加载
        if (lazyEnable()) {
            loadView();
            initView(mView);
            initListener();
            initData();
        }

    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        //不采用懒加载
        if (!lazyEnable()) {
            initData();
        }
    }

    /**
     * 填充布局
     */
    protected void loadView() {

        mViewStubContent.setLayoutResource(onBindLayout());
        View contentView = mViewStubContent.inflate();
        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new InitCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new ErrorCallback())
                .addCallback(new LoadingCallback())
                .setDefaultCallback(SuccessCallback.class)
                .build();
        mLoadService = loadSir.register(contentView, (Callback.OnReloadListener) v -> BaseFragment.this.onReload(v));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        clearDisposable();
        AppHelper.refWatcher.watch(this);
    }

    protected void initParam() {
    }

    protected void initCommonView(View view) {
        mSimpleTitleBar = mView.findViewById(R.id.ctb_simple);
        mViewStubContent = mView.findViewById(R.id.view_stub_content);
        if (enableSimplebar()) {
            mSimpleTitleBar.setBackgroundResource(R.drawable.shap_common_simplebar);
            mSimpleTitleBar.setVisibility(View.VISIBLE);
            initSimpleBar();
        }
    }

    protected void initSimpleBar() {
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

        if (onBindBarLeftStyle() == BarStyle.LEFT_BACK) {

            View backView = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            backView.setVisibility(View.VISIBLE);
            backView.setOnClickListener(v -> onSimpleBackClick());
        } else if (onBindBarLeftStyle() == BarStyle.LEFT_BACK_TEXT) {
            View backIcon = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            backIcon.setVisibility(View.VISIBLE);
            backIcon.setOnClickListener(v -> onSimpleBackClick());
            View backTv = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.tv_back);
            backTv.setVisibility(View.VISIBLE);
            backTv.setOnClickListener(v -> onSimpleBackClick());
        } else if (onBindBarLeftStyle() == BarStyle.LEFT_ICON && onBindBarLeftIcon() != null) {
            ImageView icon = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_left);
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(onBindBarLeftIcon());
            icon.setOnClickListener(v -> onLeftIconClick(v));
        }

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
                    tv1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRight1Click(v);
                        }
                    });
                }
                if (strings.length > 1 && null != strings[1] && strings[1].trim().length() > 0) {
                    TextView tv2 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.tv2_right);
                    tv2.setVisibility(View.VISIBLE);
                    tv2.setText(strings[1]);
                    tv2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRight2Click(v);
                        }
                    });
                }
                break;
            case BarStyle.RIGHT_ICON:
                Integer[] ints = onBindBarRightIcon();
                if (ints == null || ints.length == 0) {
                    break;
                }
                if (ints.length > 0 && null != ints[0]) {
                    ImageView iv1 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.iv1_right);
                    iv1.setVisibility(View.VISIBLE);
                    iv1.setImageResource(ints[0]);
                    iv1.setOnClickListener(v -> onRight1Click(v));
                }
                if (ints.length > 1 && null != ints[1]) {
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

    protected void onSimpleBackClick() {
        pop();
    }

    protected int onBindBarRightStyle() {
        return BarStyle.RIGHT_ICON;
    }

    protected int onBindBarLeftStyle() {
        return BarStyle.LEFT_BACK;
    }

    protected int onBindBarCenterStyle() {
        return BaseActivity.BarStyle.CENTER_TITLE;
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

    /**
     * 默认懒加载,true
     * @return
     */
    protected boolean lazyEnable() {
        return true;
    }

    protected void setTitle(String[] strings) {
        if (!enableSimplebar()) {
            throw new IllegalStateException("导航栏中不可用,请设置enableSimplebar为true");
        } else if (onBindBarCenterStyle() != BaseActivity.BarStyle.CENTER_TITLE) {
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

    protected void setSimpleBarBg(@ColorInt int color) {
        mSimpleTitleBar.setBackgroundColor(color);
    }

    protected void onRight1Click(View v) {

    }

    protected void onRight2Click(View v) {

    }

    protected void onLeftIconClick(View v) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public <T> void onEvent(BaseFragmentEvent<T> event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public <T> void onEventSticky(BaseFragmentEvent<T> event) {
    }

    protected abstract int onBindLayout();

    protected abstract void initView(View view);

    @Override
    public final void initView() {

    }

    public abstract void initData();


    public void initListener() {
    }


    protected boolean enableSimplebar() {
        return true;
    }

    protected boolean enableSwipeBack() {
        return true;
    }


    public void showInitView() {
        clearStatus();
        mLoadService.showCallback(InitCallback.class);
    }


    public void showErrorView() {
      clearStatus();
        mLoadService.showCallback(ErrorCallback.class);
    }


    public void showEmptyView() {
        clearStatus();
        mLoadService.showCallback(EmptyCallback.class);

    }

    public void showLoadingView(String tip) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && ((BaseFragment) parentFragment).enableSimplebar()) {
            ((BaseFragment) parentFragment).showLoadingView(tip);
        } else {
            clearStatus();
            mLoadService.setCallBack(LoadingCallback.class, (context, view1) -> {
                TextView tvTip = view1.findViewById(R.id.tv_tip);
                if (tip==null) {
                    tvTip.setVisibility(View.GONE);
                } else {
                    tvTip.setVisibility(View.VISIBLE);
                    tvTip.setText(tip);
                }
            });
            //延时100毫秒显示,避免闪屏
            mLoadingHandler.postDelayed(() -> mLoadService.showCallback(LoadingCallback.class), 100);

        }
    }

    public void clearStatus() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && ((BaseFragment) parentFragment).enableSimplebar()) {
            ((BaseFragment) parentFragment).clearStatus();
        }
        mLoadingHandler.removeCallbacksAndMessages(null);
        mLoadService.showSuccess();
    }

    protected void onReload(View v) {
        mLoadService.showSuccess();
        initData();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (!isFirst) {
            onRevisible();
        }
        isFirst = false;
    }

    /**
     * 再次可见
     */
    protected void onRevisible() {
    }

    /**
     * findViewById
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T fd(@IdRes int id) {
        return mView.findViewById(id);
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    protected void navigateTo(String path) {
        Object navigation = ARouter.getInstance().build(path).navigation();
        if (null != navigation) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }

    protected void navigateTo(String path, int launchMode) {
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.launchMode = launchMode;
        if (null != navigation) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }

    protected void navigateTo(String path, int launchMode, ExtraTransaction extraTransaction) {
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.launchMode = launchMode;
        navigateBean.extraTransaction = extraTransaction;
        if (null != navigation) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }

    protected void navigateTo(String path, ExtraTransaction extraTransaction) {
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.extraTransaction = extraTransaction;
        if (null != navigation) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }
}
