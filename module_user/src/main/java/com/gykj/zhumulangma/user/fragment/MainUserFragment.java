package com.gykj.zhumulangma.user.fragment;

import android.Manifest;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SizeUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.user.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.bugly.beta.Beta;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/8/14 13:41
 * Email: 1071931588@qq.com
 * Description:我的
 */
@Route(path = AppConstants.Router.User.F_MAIN)
public class MainUserFragment extends BaseFragment implements View.OnClickListener {


    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private NestedScrollView mScrollView;
    private ImageView parallax;
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
    protected void initView(View view) {
        ctbTrans = view.findViewById(R.id.ctb_trans);
        ctbWhite = view.findViewById(R.id.ctb_white);
        mScrollView = view.findViewById(R.id.msv);
        parallax = view.findViewById(R.id.parallax);
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
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (nestedScrollView, i, scrollY, i2, i3) -> {
                    flParallax.setTranslationY(-scrollY);
                    ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
                    ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
                });
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                ctbTrans.setAlpha(1 - (float) offset / ctbTrans.getHeight());

                parallax.setScaleX((float) (1 + percent * 0.2));
                parallax.setScaleY((float) (1 + percent * 0.2));

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
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.ll_download == id) {
            navigateTo(AppConstants.Router.Listen.F_DOWNLOAD);
        } else if (R.id.ll_history == id) {
            navigateTo(AppConstants.Router.Listen.F_HISTORY);
        } else if (R.id.ll_favorit == id) {
            navigateTo(AppConstants.Router.Listen.F_FAVORITE);
        } else if (v == whiteLeft || v == transLeft) {
            navigateTo(AppConstants.Router.User.F_MESSAGE);
        } else if (id == R.id.cl_fxzq) {
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.SHARE));
        } else if (id == R.id.cl_sys) {
            new RxPermissions(this).requestEach(new String[]{Manifest.permission.CAMERA})
                    .subscribe(permission -> {
                        if (permission.granted) {
                            navigateTo(AppConstants.Router.Home.F_SCAN);
                        } else {
                            ToastUtil.showToast("请允许应用使用相机权限");
                        }
                    });
        } else if (id == R.id.cl_wxhd) {
            navigateTo(AppConstants.Router.Listen.F_FAVORITE);
        } else if (id == R.id.cl_jcgx) {
            Beta.checkUpgrade();
        } else if (id == R.id.cl_gy||id == R.id.iv_user) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Discover.F_WEB)
                    .withString(KeyCode.Discover.PATH, "https://github.com/TanZhiL/Zhumulangma")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Discover.F_WEB, (ISupportFragment) navigation)));
        }
    }

}
