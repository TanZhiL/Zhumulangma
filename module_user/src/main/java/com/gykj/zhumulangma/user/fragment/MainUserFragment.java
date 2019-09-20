package com.gykj.zhumulangma.user.fragment;

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
import com.blankj.utilcode.util.SizeUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.user.R;
import com.maiml.library.BaseItemLayout;
import com.maiml.library.config.ConfigAttrs;
import com.maiml.library.config.Mode;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

@Route(path = AppConstants.Router.User.F_MAIN)
public class MainUserFragment extends BaseFragment implements BaseItemLayout.OnBaseItemClick, View.OnClickListener {


    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private BaseItemLayout bilUser;
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
        bilUser = view.findViewById(R.id.bil_user);
        mScrollView = view.findViewById(R.id.msv);
        parallax = view.findViewById(R.id.parallax);
        flParallax = view.findViewById(R.id.fl_parallax);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        initBar();
        initItemList();

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

    private void initItemList() {
        List<String> valueList = new ArrayList<>();

        valueList.add("主播工作台");
        valueList.add("我的作品");
        valueList.add("我的巅峰会员");
        valueList.add("我的已购");
        valueList.add("我的钱包");
        valueList.add("我的优惠券");
        valueList.add("分享赚钱");
        valueList.add("扫一扫");
        valueList.add("我喜欢的");
        valueList.add("更多");
        valueList.add("关于");
        valueList.add("设置");

        List<Integer> resIdList = new ArrayList<>();

        resIdList.add(R.drawable.user_zhubo);
        resIdList.add(R.drawable.user_zuoping);
        resIdList.add(R.drawable.user_vip);
        resIdList.add(R.drawable.user_cart);
        resIdList.add(R.drawable.user_qianbao);
        resIdList.add(R.drawable.user_quan);
        resIdList.add(R.drawable.user_zhuanqian);
        resIdList.add(R.drawable.user_sao);
        resIdList.add(R.drawable.user_like);
        resIdList.add(R.drawable.user_more);
        resIdList.add(R.drawable.user_help);
        resIdList.add(R.drawable.user_setting);

        ConfigAttrs attrs = new ConfigAttrs(); // 把全部参数的配置，委托给ConfigAttrs类处理。

        //参数 使用链式方式配置
        attrs.setValueList(valueList)  // 文字 list
                .setResIdList(resIdList) // icon list
                .setIconWidth(23)  //设置icon 的大小
                .setIconHeight(23)
                .setItemMarginTop(1)
                .setItemMarginTop(2, 8)
                .setItemMarginTop(3, 8)
                .setItemMarginTop(7, 8)
                .setItemMarginTop(10, 8)
                .setItemMode(Mode.ARROW)
                .setArrowResId(R.drawable.common_arrow_enter); //设置箭头资源值;
        bilUser.setConfigAttrs(attrs)
                .create(); //
        bilUser.setOnBaseItemClick(this);
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

                parallax.setScaleX((float) (1+percent*0.2));
                parallax.setScaleY((float) (1+percent*0.2));

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
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.ll_download == id) {
            navigateTo(AppConstants.Router.Listen.F_DOWNLOAD);
        }else if (R.id.ll_history == id) {
            navigateTo(AppConstants.Router.Listen.F_HISTORY);
        }else if (R.id.ll_favorit == id) {
            navigateTo(AppConstants.Router.Listen.F_FAVORITE);
        } else if (v == whiteLeft || v == transLeft) {
            navigateTo(AppConstants.Router.User.F_MESSAGE);
        }
    }

}
