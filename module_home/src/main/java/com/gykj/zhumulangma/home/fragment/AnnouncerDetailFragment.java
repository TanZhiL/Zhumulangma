package com.gykj.zhumulangma.home.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SizeUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.widget.TScrollView;
import com.gykj.zhumulangma.home.R;
import com.maiml.library.BaseItemLayout;
import com.maiml.library.config.ConfigAttrs;
import com.maiml.library.config.Mode;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
public class AnnouncerDetailFragment extends BaseFragment implements
        TScrollView.OnScrollListener, BaseItemLayout.OnBaseItemClick, View.OnClickListener {
    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long mAnnouncerId;
    @Autowired(name = KeyCode.Home.ANNOUNCER_NAME)
    public String mAnnouncerName;

    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private TScrollView mScrollView;
    private ImageView parallax;
    private View flParallax;
    private SmartRefreshLayout refreshLayout;
    private ImageView whiteLeft;
    private ImageView whiteRight;
    private ImageView transLeft;
    private ImageView transRight;

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_announcer_detail;
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


        transLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transLeft.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transLeft.setVisibility(View.VISIBLE);


        transRight.setImageResource(R.drawable.ic_common_more);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transRight.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transRight.setVisibility(View.VISIBLE);

        whiteLeft = ctbWhite.getLeftCustomView().findViewById(R.id.iv_left);
        whiteRight = ctbWhite.getRightCustomView().findViewById(R.id.iv1_right);
        TextView tvTitle = ctbWhite.getCenterCustomView().findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(mAnnouncerName);

        whiteLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        whiteLeft.setVisibility(View.VISIBLE);

        whiteRight.setImageResource(R.drawable.ic_common_more);
        whiteRight.setVisibility(View.VISIBLE);

    }


    @Override
    public void initListener() {
        super.initListener();
        mScrollView.setOnScrollListener(this);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                ctbTrans.setAlpha(1 - (float) offset / ctbTrans.getHeight());

                parallax.setScaleX((float) (1+percent*0.2));
                parallax.setScaleY((float) (1+percent*0.2));

                flParallax.setTranslationY(offset);
            }
        });
        whiteLeft.setOnClickListener(this);
        whiteRight.setOnClickListener(this);
        transLeft.setOnClickListener(this);
        transRight.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onScroll(int scrollY) {
        flParallax.setTranslationY(-scrollY);
        ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
        ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == whiteLeft || v == transLeft) {
          pop();
        }
    }

}
