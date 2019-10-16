package com.gykj.zhumulangma.common.mvvm.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
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
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.AppHelper;
import com.gykj.zhumulangma.common.R;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.mvvm.view.status.BlankCallback;
import com.gykj.zhumulangma.common.mvvm.view.status.EmptyCallback;
import com.gykj.zhumulangma.common.mvvm.view.status.ErrorCallback;
import com.gykj.zhumulangma.common.mvvm.view.status.LoadingCallback;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;


/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:Fragment基类
 */
public abstract class BaseFragment extends SupportFragment {
    protected static final String TAG = BaseFragment.class.getSimpleName();
    private CompositeDisposable mCompositeDisposable;
    protected View mView;
    private ViewStub mViewStubContent;
    protected LoadService mBaseLoadService;
    protected CommonTitleBar mSimpleTitleBar;
    protected App mApplication;
    private Handler mLoadingHandler = new Handler();
    /**
     * 是否第一次进入
     */
    private boolean isFirst = true;

    protected interface SimpleBarStyle {
        /**
         * 返回图标(默认)
         */
        int LEFT_BACK = 0;
        /**
         * 返回图标+文字
         */
        int LEFT_BACK_TEXT = 1;
        /**
         * 附加图标
         */
        int LEFT_ICON = 2;
        /**
         * 标题(默认)
         */
        int CENTER_TITLE = 7;
        /**
         * 自定义布局
         */
        int CENTER_CUSTOME = 8;
        /**
         * 文字
         */
        int RIGHT_TEXT = 4;
        /**
         * 图标(默认)
         */
        int RIGHT_ICON = 5;
        /**
         * 自定义布局
         */
        int RIGHT_CUSTOME = 6;
    }

    protected abstract @LayoutRes
    int onBindLayout();

    protected void initParam() {
    }

    protected abstract void initView(View view);

    public void initListener() {
    }

    public abstract void initData();

    /**
     * 再次可见
     */
    protected void onRevisible() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = App.getInstance();
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
    }

    /**
     * RxView添加订阅
     */
    protected void addDisposable(Disposable mDisposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(mDisposable);
    }

    /**
     * 取消RxView所有订阅
     */
    private void clearDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.common_layout_root, container, false);
        initCommonView();
        initParam();
        //不采用懒加载
        if (!enableLazy()) {
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
        if (enableLazy()) {
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
        if (!enableLazy()) {
            initData();
        }
    }

    /**
     * 填充布局(布局懒加载)
     */
    protected void loadView() {
        mViewStubContent.setLayoutResource(onBindLayout());
        View contentView = mViewStubContent.inflate();
        LoadSir.Builder builder = new LoadSir.Builder()
                .addCallback(getInitCallBack())
                .addCallback(getEmptyCallback())
                .addCallback(getErrorCallback())
                .addCallback(getLoadingCallback())
                .setDefaultCallback(SuccessCallback.class);
        if (!CollectionUtils.isEmpty(onBindExtraCallBack())) {
            for (Callback callback : onBindExtraCallBack()) {
                builder.addCallback(callback);
            }
        }
        mBaseLoadService = builder.build().register(contentView, (Callback.OnReloadListener) BaseFragment.this::onReload);
    }

    /**
     * 提供状态布局
     *
     * @return
     */
    protected Callback getInitCallBack() {
        return new BlankCallback();
    }

    protected Callback getLoadingCallback() {
        return new LoadingCallback();
    }

    protected Callback getErrorCallback() {
        return new ErrorCallback();
    }

    protected Callback getEmptyCallback() {
        return new EmptyCallback();
    }

    /**
     * 提供额外状态布局
     *
     * @return
     */
    protected List<Callback> onBindExtraCallBack() {
        return null;
    }

    /**
     * 初始化基本布局
     */
    private void initCommonView() {
        mSimpleTitleBar = mView.findViewById(R.id.ctb_simple);
        mViewStubContent = mView.findViewById(R.id.view_stub_content);
        if (enableSimplebar()) {
            mSimpleTitleBar.setBackgroundResource(R.drawable.shap_common_simplebar);
            mSimpleTitleBar.setVisibility(View.VISIBLE);
            initSimpleBar();
        }
    }

    /**
     * 初始化通用标题栏
     */
    private void initSimpleBar() {
        // 中间
        if (onBindBarCenterStyle() == BaseFragment.SimpleBarStyle.CENTER_TITLE) {
            String[] strings = onBindBarTitleText();
            if (strings != null && strings.length > 0) {
                if (null != strings[0] && strings[0].trim().length() > 0) {
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
        } else if (onBindBarCenterStyle() == BaseFragment.SimpleBarStyle.CENTER_CUSTOME && onBindBarCenterCustome() != null) {
            ViewGroup group = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.fl_custome);
            group.setVisibility(View.VISIBLE);
            group.addView(onBindBarCenterCustome(), new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        //左边
        if (onBindBarLeftStyle() == BaseFragment.SimpleBarStyle.LEFT_BACK) {

            ImageView backView = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            if (onBindBarBackIcon() != null) {
                backView.setImageResource(onBindBarBackIcon());
            }
            backView.setVisibility(View.VISIBLE);
            backView.setOnClickListener(v -> onSimpleBackClick());
        } else if (onBindBarLeftStyle() == BaseFragment.SimpleBarStyle.LEFT_BACK_TEXT) {
            View backIcon = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            backIcon.setVisibility(View.VISIBLE);
            backIcon.setOnClickListener(v -> onSimpleBackClick());
            View backTv = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.tv_back);
            backTv.setVisibility(View.VISIBLE);
            backTv.setOnClickListener(v -> onSimpleBackClick());
        } else if (onBindBarLeftStyle() == BaseFragment.SimpleBarStyle.LEFT_ICON && onBindBarLeftIcon() != null) {
            ImageView icon = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_left);
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(onBindBarLeftIcon());
            icon.setOnClickListener(this::onLeftIconClick);
        }
        //右边
        switch (onBindBarRightStyle()) {
            case BaseFragment.SimpleBarStyle.RIGHT_TEXT:
                String[] strings = onBindBarRightText();
                if (strings == null || strings.length == 0) {
                    break;
                }
                if (null != strings[0] && strings[0].trim().length() > 0) {
                    TextView tv1 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.tv1_right);
                    tv1.setVisibility(View.VISIBLE);
                    tv1.setText(strings[0]);
                    tv1.setOnClickListener(this::onRight1Click);
                }
                if (strings.length > 1 && null != strings[1] && strings[1].trim().length() > 0) {
                    TextView tv2 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.tv2_right);
                    tv2.setVisibility(View.VISIBLE);
                    tv2.setText(strings[1]);
                    tv2.setOnClickListener(this::onRight2Click);
                }
                break;
            case BaseFragment.SimpleBarStyle.RIGHT_ICON:
                Integer[] ints = onBindBarRightIcon();
                if (ints == null || ints.length == 0) {
                    break;
                }
                if (null != ints[0]) {
                    ImageView iv1 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.iv1_right);
                    iv1.setVisibility(View.VISIBLE);
                    iv1.setImageResource(ints[0]);
                    iv1.setOnClickListener(this::onRight1Click);
                }
                if (ints.length > 1 && null != ints[1]) {
                    ImageView iv2 = mSimpleTitleBar.getRightCustomView().findViewById(R.id.iv2_right);
                    iv2.setVisibility(View.VISIBLE);
                    iv2.setImageResource(ints[1]);
                    iv2.setOnClickListener(this::onRight2Click);
                }
                break;
            case BaseFragment.SimpleBarStyle.RIGHT_CUSTOME:
                if (onBindBarRightCustome() != null) {
                    ViewGroup group = mSimpleTitleBar.getRightCustomView().findViewById(R.id.fl_custome);
                    group.setVisibility(View.VISIBLE);
                    group.addView(onBindBarRightCustome(), new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                break;
        }

    }

    /**
     * 是否开启通用标题栏,默认true
     *
     * @return
     */
    protected boolean enableSimplebar() {
        return true;
    }

    /**
     * 初始化右边标题栏类型
     *
     * @return
     */
    protected int onBindBarRightStyle() {
        return BaseActivity.SimpleBarStyle.RIGHT_ICON;
    }

    /**
     * 初始化左边标题栏类型
     *
     * @return
     */
    protected int onBindBarLeftStyle() {
        return BaseActivity.SimpleBarStyle.LEFT_BACK;
    }

    /**
     * 初始化中间标题栏类型
     *
     * @return
     */
    protected int onBindBarCenterStyle() {
        return BaseActivity.SimpleBarStyle.CENTER_TITLE;
    }

    /**
     * 初始化标题栏右边文本
     *
     * @return
     */
    protected String[] onBindBarRightText() {
        return null;
    }

    /**
     * 初始化标题文本
     *
     * @return
     */
    protected String[] onBindBarTitleText() {
        return null;
    }

    /**
     * 初始化标题栏右边图标
     *
     * @return
     */
    protected @DrawableRes
    Integer[] onBindBarRightIcon() {
        return null;
    }

    /**
     * 初始化标题栏左边附加图标
     *
     * @return
     */
    protected @DrawableRes
    Integer onBindBarLeftIcon() {
        return null;
    }

    /**
     * 初始化标题栏左边返回按钮图标
     *
     * @return
     */
    protected @DrawableRes
    Integer onBindBarBackIcon() {
        return null;
    }

    /**
     * 点击标题栏返回按钮事件
     */
    protected void onSimpleBackClick() {
        pop();
    }

    /**
     * 初始化标题栏右侧自定义布局
     *
     * @return
     */
    protected View onBindBarRightCustome() {
        return null;
    }

    /**
     * 初始化标题栏中间自定义布局
     *
     * @return
     */
    protected View onBindBarCenterCustome() {
        return null;
    }

    /**
     * 设置标题栏背景颜色
     *
     * @return
     */
    protected void setSimpleBarBg(@ColorInt int color) {
        mSimpleTitleBar.setBackgroundColor(color);
    }

    /**
     * 点击标题栏靠右第一个事件
     *
     * @return
     */
    protected void onRight1Click(View v) {

    }

    /**
     * 点击标题栏靠右第二个事件
     *
     * @return
     */
    protected void onRight2Click(View v) {

    }

    /**
     * 点击标题栏靠左附加事件
     *
     * @return
     */
    protected void onLeftIconClick(View v) {

    }

    /**
     * 是否开启懒加载,默认true
     *
     * @return
     */
    protected boolean enableLazy() {
        return true;
    }

    /**
     * 设置标题栏标题文字
     *
     * @param strings
     */
    protected void setTitle(String[] strings) {
        if (!enableSimplebar()) {
            throw new IllegalStateException("导航栏中不可用,请设置enableSimplebar为true");
        } else if (onBindBarCenterStyle() != BaseActivity.SimpleBarStyle.CENTER_TITLE) {
            throw new IllegalStateException("导航栏中间布局不为标题类型,请设置onBindBarCenterStyle(SimpleBarStyle.CENTER_TITLE)");
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

    /**
     * 设置标题栏文字颜色
     *
     * @param color
     */
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

    /**
     * 设置标题栏返回按钮图片
     *
     * @param res
     */
    protected void setBarBackIconRes(@DrawableRes int res) {
        if (!enableSimplebar()) {
            throw new IllegalStateException("导航栏中不可用,请设置enableSimplebar为true");
        } else {
            ImageView ivBack = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            ivBack.setImageResource(res);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FragmentEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventSticky(FragmentEvent event) {
    }


    /**
     * 显示初始化状态页
     */
    public void showInitView() {
        clearStatus();
        mBaseLoadService.showCallback(getInitCallBack().getClass());
    }


    /**
     * 显示出错状态页
     */
    public void showErrorView() {
        clearStatus();
        mBaseLoadService.showCallback(getErrorCallback().getClass());
    }

    /**
     * 显示空状态页
     */
    public void showEmptyView() {
        clearStatus();
        mBaseLoadService.showCallback(getEmptyCallback().getClass());
    }

    /**
     * 显示loading状态页
     *
     * @param tip 为null时不带提示文本
     */
    public void showLoadingView(String tip) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && ((BaseFragment) parentFragment).enableSimplebar()) {
            ((BaseFragment) parentFragment).showLoadingView(tip);
        } else {
            clearStatus();
            mBaseLoadService.setCallBack(getLoadingCallback().getClass(), (context, view1) -> {
                TextView tvTip = view1.findViewById(R.id.tv_tip);
                if(tvTip==null){
                    throw new IllegalStateException(getLoadingCallback().getClass()+"必须带有显示提示文本的TextView,且id为R.id.tv_tip");
                }
                if (tip == null) {
                    tvTip.setVisibility(View.GONE);
                } else {
                    tvTip.setVisibility(View.VISIBLE);
                    tvTip.setText(tip);
                }
            });
            //延时300毫秒显示,避免闪屏
            mLoadingHandler.postDelayed(() -> mBaseLoadService.showCallback(getLoadingCallback().getClass()), 300);

        }
    }

    /**
     * 清除所有状态页
     */
    public void clearStatus() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && ((BaseFragment) parentFragment).enableSimplebar()) {
            ((BaseFragment) parentFragment).clearStatus();
        }
        mLoadingHandler.removeCallbacksAndMessages(null);
        mBaseLoadService.showSuccess();
    }

    /**
     * 点击状态页默认执行事件
     */
    protected void onReload(View v) {
        showInitView();
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

    /**
     * 页面跳转
     *
     * @param path
     */
    protected void navigateTo(String path) {
        Object navigation = ARouter.getInstance().build(path).navigation();
        if (null != navigation) {
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }

    protected void navigateTo(String path, int launchMode) {
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.launchMode = launchMode;
        if (null != navigation) {
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }

    protected void navigateTo(String path, int launchMode, ExtraTransaction extraTransaction) {
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.launchMode = launchMode;
        navigateBean.extraTransaction = extraTransaction;
        if (null != navigation) {
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }

    protected void navigateTo(String path, ExtraTransaction extraTransaction) {
        Object navigation = ARouter.getInstance().build(path).navigation();
        NavigateBean navigateBean = new NavigateBean(path, (ISupportFragment) navigation);
        navigateBean.extraTransaction = extraTransaction;
        if (null != navigation) {
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                    new NavigateBean(path, (ISupportFragment) navigation)));
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        //如果正在显示loading,则清除
        if (mBaseLoadService.getCurrentCallback() == LoadingCallback.class) {
            clearStatus();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLoadingHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        clearDisposable();
        AppHelper.refWatcher.watch(this);
    }
}
