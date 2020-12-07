package com.gykj.zhumulangma.common.mvvm.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.R;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:Activity基类,为了减少布局层级,主要用于添加应用内悬浮窗,其他界面请添加到根fragment中
 */
public abstract class BaseActivity<DB extends ViewDataBinding> extends AppCompatActivity implements BaseView, Consumer<Disposable> {
    protected String TAG = BaseActivity.class.getSimpleName();

    //公用Handler
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    //Disposable容器
    protected CompositeDisposable mDisposables = new CompositeDisposable();

    protected ARouter mRouter = ARouter.getInstance();

    //状态页管理
    protected LoadService mBaseLoadService;
    protected DB mBinding;
    protected boolean hasInit;
    //默认标题栏
    protected CommonTitleBar mSimpleTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = "Thomas_" + getClass().getSimpleName();
        Log.d(TAG, "onCreate() savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.common_layout_root);
        ViewStub viewStubContent = findViewById(R.id.vs_content);
        viewStubContent.setLayoutResource(onBindLayout());
        mBinding = DataBindingUtil.bind(viewStubContent.inflate());
        EventBus.getDefault().register(this);
        mRouter.inject(this);
        initCommonView();
        initParam();
        initView();
        initListener();
        initData();
        hasInit = true;
    }

    @Override
    public void accept(Disposable disposable) throws Exception {
        mDisposables.add(disposable);
    }


    /**
     * 初始化基本布局
     */
    public void initCommonView() {
        if (enableSimplebar()) {
            ViewStub viewStubBar = findViewById(R.id.vs_bar);
            viewStubBar.setLayoutResource(R.layout.common_layout_simplebar);
            mSimpleTitleBar = viewStubBar.inflate().findViewById(R.id.ctb_simple);
            initSimpleBar(mSimpleTitleBar);
        }
        LoadSir.Builder builder = new LoadSir.Builder()
                .addCallback(getInitStatus())
                .addCallback(getEmptyStatus())
                .addCallback(getErrorStatus())
                .addCallback(getLoadingStatus())
                .setDefaultCallback(getInitStatus().getClass());
        if (!CollectionUtils.isEmpty(getExtraStatus())) {
            for (Callback callback : getExtraStatus()) {
                builder.addCallback(callback);
            }
        }
        ViewGroup.MarginLayoutParams layoutParams = null;
        if (enableSimplebar()) {
            layoutParams = new FrameLayout.LayoutParams((ViewGroup.MarginLayoutParams) mBinding.getRoot().getLayoutParams());
            boolean b = StatusBarUtils.supportTransparentStatusBar();
            int barHeight = b ? BarUtils.getStatusBarHeight() : 0;
            layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.simpleBarHeight) + barHeight;
        }
        mBaseLoadService = builder.build().register(mBinding.getRoot(), layoutParams, (Callback.OnReloadListener) BaseActivity.this::onReload);
        mBaseLoadService.showSuccess();
    }


    /**
     * 初始化通用标题栏
     */
    protected void initSimpleBar(CommonTitleBar mSimpleTitleBar) {
        // 中间
        if (onBindBarCenterStyle() == SimpleBarStyle.CENTER_TITLE) {
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
        } else if (onBindBarCenterStyle() == SimpleBarStyle.CENTER_CUSTOME && onBindBarCenterCustome() != null) {
            ViewGroup group = mSimpleTitleBar.getCenterCustomView().findViewById(R.id.fl_custome);
            group.setVisibility(View.VISIBLE);
            group.addView(onBindBarCenterCustome(), new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        //左边
        if (onBindBarLeftStyle() == SimpleBarStyle.LEFT_BACK) {

            ImageView backView = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            if (onBindBarBackIcon() != null) {
                backView.setImageResource(onBindBarBackIcon());
            }
            backView.setVisibility(View.VISIBLE);
            backView.setOnClickListener(v -> onSimpleBackClick());
        } else if (onBindBarLeftStyle() == SimpleBarStyle.LEFT_BACK_TEXT) {
            View backIcon = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_back);
            backIcon.setVisibility(View.VISIBLE);
            backIcon.setOnClickListener(v -> onSimpleBackClick());
            View backTv = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.tv_back);
            backTv.setVisibility(View.VISIBLE);
            backTv.setOnClickListener(v -> onSimpleBackClick());
        } else if (onBindBarLeftStyle() == SimpleBarStyle.LEFT_ICON && onBindBarLeftIcon() != null) {
            ImageView icon = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_left);
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(onBindBarLeftIcon());
            icon.setOnClickListener(this::onLeftIconClick);
        }
        //右边
        switch (onBindBarRightStyle()) {
            case RIGHT_TEXT:
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
            case RIGHT_ICON:
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
            case RIGHT_CUSTOME:
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
     * 是否可滑动返回,默认true
     *
     * @return
     */
    protected boolean enableSwipeBack() {
        return true;
    }

    /**
     * 初始化右边标题栏类型
     *
     * @return
     */
    protected SimpleBarStyle onBindBarRightStyle() {
        return SimpleBarStyle.RIGHT_ICON;
    }

    /**
     * 初始化左边标题栏类型
     *
     * @return
     */
    protected SimpleBarStyle onBindBarLeftStyle() {
        return SimpleBarStyle.LEFT_BACK;
    }

    /**
     * 初始化中间标题栏类型
     *
     * @return
     */
    protected SimpleBarStyle onBindBarCenterStyle() {
        return SimpleBarStyle.CENTER_TITLE;
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
     * 点击标题栏返回按钮事件
     */
    public void onSimpleBackClick() {
        finish();
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
     * 设置标题栏标题文字
     *
     * @param strings
     */
    protected void setTitle(String[] strings) {
        if (!enableSimplebar()) {
            throw new IllegalStateException("导航栏中不可用,请设置enableSimplebar为true");
        } else if (onBindBarCenterStyle() != SimpleBarStyle.CENTER_TITLE) {
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
        mBaseLoadService.showCallback(getInitStatus().getClass());
    }


    /**
     * 显示出错状态页
     */
    public void showErrorView() {
        clearStatus();
        mBaseLoadService.showCallback(getErrorStatus().getClass());
    }

    /**
     * 显示空状态页
     */
    public void showEmptyView() {
        clearStatus();
        mBaseLoadService.showCallback(getEmptyStatus().getClass());
    }

    /**
     * 显示loading状态页
     *
     * @param tip 为null时不带提示文本
     */
    public void showLoadingView(String tip) {
        mHandler.removeCallbacks(mLoadStatusRun);
        mBaseLoadService.showSuccess();
        mBaseLoadService.setCallBack(getLoadingStatus().getClass(), (context, view) -> {
            TextView tvTip = view.findViewById(R.id.tv_tip);
            if (tvTip == null) {
                throw new IllegalStateException(getLoadingStatus().getClass() + "必须带有显示提示文本的TextView,且id为R.id.tv_tip");
            }
            if (tip == null) {
                tvTip.setVisibility(View.GONE);
            } else {
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(tip);
            }
        });
        //延时300毫秒显示,避免闪屏
        postDelayed(mLoadStatusRun, 300);
    }

    private final Runnable mLoadStatusRun = new Runnable() {
        @Override
        public void run() {
            mBaseLoadService.showCallback(getLoadingStatus().getClass());
        }
    };

    /**
     * 清除所有状态页
     */
    public void clearStatus() {
        mHandler.removeCallbacks(mLoadStatusRun);
        mBaseLoadService.showSuccess();
    }

    /**
     * 点击状态页默认执行事件
     */
    protected void onReload(View v) {
        showInitView();
        initData();
    }

    protected void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    protected void postDelayed(Runnable runnable, long delayMillis) {
        mHandler.postDelayed(runnable, delayMillis);
    }

    public void share(ShareAction action){
        ShareBoardConfig config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setCancelButtonVisibility(true);
        config.setTitleVisibility(false);
        config.setCancelButtonVisibility(true);
        config.setIndicatorVisibility(false);
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
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        mDisposables.clear();
        //  ThirdHelper.refWatcher.watch(this);
    }

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
