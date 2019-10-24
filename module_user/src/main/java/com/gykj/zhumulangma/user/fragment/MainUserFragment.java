package com.gykj.zhumulangma.user.fragment;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.aop.LoginHelper;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.net.dto.GitHubDTO;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.user.R;
import com.gykj.zhumulangma.user.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.user.mvvm.viewmodel.MainUserViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.bugly.beta.Beta;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:我的
 */
@Route(path = AppConstants.Router.User.F_MAIN)
public class MainUserFragment extends BaseRefreshMvvmFragment<MainUserViewModel, Object> implements View.OnClickListener {

    private GitHubDTO mGitHubDTO;

    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private NestedScrollView scrollView;
    private ImageView ivParallax;
    private View flParallax;
    private SmartRefreshLayout refreshLayout;
    private ImageView whiteLeft;
    private ImageView whiteRight;
    private ImageView transLeft;
    private ImageView transRight;

    @Override
    protected int onBindLayout() {
        return R.layout.user_fragment_main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }

    @Override
    protected void initView(View view) {
        ctbTrans = view.findViewById(R.id.ctb_trans);
        ctbWhite = view.findViewById(R.id.ctb_white);
        scrollView = view.findViewById(R.id.nsv);
        ivParallax = view.findViewById(R.id.iv_parallax);
        flParallax = view.findViewById(R.id.fl_parallax);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        initBar();

    }

    private void initBar() {

        transLeft = ctbTrans.getLeftCustomView().findViewById(R.id.iv_left);
        transRight = ctbTrans.getRightCustomView().findViewById(R.id.iv1_right);


        transLeft.setImageResource(R.drawable.ic_common_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transLeft.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transLeft.setVisibility(View.VISIBLE);


        transRight.setImageResource(R.drawable.ic_common_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transRight.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transRight.setVisibility(View.VISIBLE);

        whiteLeft = ctbWhite.getLeftCustomView().findViewById(R.id.iv_left);
        whiteRight = ctbWhite.getRightCustomView().findViewById(R.id.iv1_right);
        TextView tvTitle = ctbWhite.getCenterCustomView().findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("我的");

        whiteLeft.setImageResource(R.drawable.ic_common_message);
        whiteLeft.setVisibility(View.VISIBLE);

        whiteRight.setImageResource(R.drawable.ic_common_settings);
        whiteRight.setVisibility(View.VISIBLE);

    }

    @Override
    public void initListener() {
        super.initListener();
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (nestedScrollView, i, scrollY, i2, i3) -> {
                    flParallax.setTranslationY(-scrollY);
                    ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
                    ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
                });
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent,
                                       int offset, int headerHeight, int maxDragHeight) {
                ctbTrans.setAlpha(1 - (float) offset / ctbTrans.getHeight());

                ivParallax.setScaleX((float) (1 + percent * 0.2));
                ivParallax.setScaleY((float) (1 + percent * 0.2));

                flParallax.setTranslationY(offset);
            }
        });
        fd(R.id.ll_download).setOnClickListener(this);
        fd(R.id.ll_history).setOnClickListener(this);
        fd(R.id.ll_favorit).setOnClickListener(this);
        whiteLeft.setOnClickListener(this);
        whiteRight.setOnClickListener(this);
        transLeft.setOnClickListener(this);
        transRight.setOnClickListener(this);
        fd(R.id.iv_user).setOnClickListener(this);
        fd(R.id.cl_fxzq).setOnClickListener(this);
        fd(R.id.cl_sys).setOnClickListener(this);
        fd(R.id.cl_wxhd).setOnClickListener(this);
        fd(R.id.cl_jcgx).setOnClickListener(this);
        fd(R.id.cl_gy).setOnClickListener(this);
        fd(R.id.iv_avatar).setOnClickListener(this);
        fd(R.id.cl_zx).setOnClickListener(this);
        fd(R.id.tv_nickname).setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout, null);
    }

    @Override
    public void initData() {
        mViewModel.init();
    }

    @Override
    protected void initViewObservable() {
        mViewModel.getGitHubEvent().observe(this, gitHubDTO -> {
            mGitHubDTO = gitHubDTO;
            ((TextView) fd(R.id.tv_star)).setText(convertNum(gitHubDTO.getStargazers_count()));
            ((TextView) fd(R.id.tv_fork)).setText(convertNum(gitHubDTO.getForks_count()));
      //      ((TextView) fd(R.id.tv_desc)).setText(gitHubDTO.getDescription());
        });
        RequestOptions options=new RequestOptions();
        options.placeholder(R.drawable.ic_user_avatar)
                .error(R.drawable.ic_user_avatar);
        mViewModel.getBaseUserInfoEvent().observe(this, xmBaseUserInfo -> {
            Glide.with(MainUserFragment.this).load(xmBaseUserInfo.getAvatarUrl())
                    .apply(options)
                    .into((ImageView) fd(R.id.iv_avatar));
            ((TextView) fd(R.id.tv_nickname)).setText(xmBaseUserInfo.getNickName());
            fd(R.id.tv_vip).setVisibility(xmBaseUserInfo.isVip() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.ll_download == id) {
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_DOWNLOAD);
        } else if (R.id.ll_history == id) {
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_HISTORY);
        } else if (R.id.ll_favorit == id) {
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_FAVORITE);
        } else if (v == whiteLeft || v == transLeft) {
            RouteUtil.navigateTo(AppConstants.Router.User.F_MESSAGE);
        } else if (id == R.id.cl_fxzq) {
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHARE));
        } else if (id == R.id.cl_sys) {
            new RxPermissions(this).requestEach(new String[]{Manifest.permission.CAMERA})
                    .subscribe(permission -> {
                        if (permission.granted) {
                            RouteUtil.navigateTo(AppConstants.Router.Home.F_SCAN);
                        } else {
                            ToastUtil.showToast("请允许应用使用相机权限");
                        }
                    });
        } else if (id == R.id.cl_wxhd) {
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_FAVORITE);
        } else if (id == R.id.cl_jcgx) {
            Beta.checkUpgrade();
        } else if (id == R.id.cl_gy || id == R.id.iv_user) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Discover.F_WEB)
                    .withString(KeyCode.Discover.PATH, "https://github.com/TanZhiL")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Discover.F_WEB, (ISupportFragment) navigation)));
        } else if (id == R.id.cl_zx) {
            new AlertDialog.Builder(mActivity)
                    .setMessage("您确定要注销登录吗?")
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("确定", (dialog, which) -> LoginHelper.getInstance().logout()).show();
        } else if (id == R.id.tv_nickname||id==R.id.iv_avatar) {
            if (!AccessTokenManager.getInstanse().hasLogin()) {
                LoginHelper.getInstance().login(mActivity);
            }
        }
    }

    @Override
    protected Class<MainUserViewModel> onBindViewModel() {
        return MainUserViewModel.class;
    }

    @Override
    protected ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    private String convertNum(int num) {
        if (num < 1000) {
            return String.valueOf(num);
        }
        String dy1000 = String.valueOf(num / 1000);
        String xy1000 = String.valueOf(num % 1000 / 100);

        return dy1000 + "." + xy1000 + "k";
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.User.TAB_REFRESH:

                if (isSupportVisible() && mBaseLoadService.getCurrentCallback() != getInitCallBack().getClass()) {
                    fd(R.id.nsv).scrollTo(0, 0);
                    ((SmartRefreshLayout) fd(R.id.refreshLayout)).autoRefresh();
                }
                break;
            case EventCode.Main.LOGINSUCC:
                fd(R.id.nsv).scrollTo(0, 0);
                ((SmartRefreshLayout) fd(R.id.refreshLayout)).autoRefresh();
                break;
            case EventCode.Main.LOGOUTSUCC:
                Glide.with(MainUserFragment.this).load(R.drawable.ic_user_avatar).into((ImageView) fd(R.id.iv_avatar));
                ((TextView) fd(R.id.tv_nickname)).setText("未登陆");
                fd(R.id.tv_vip).setVisibility(View.GONE);
                break;
        }
    }

}
