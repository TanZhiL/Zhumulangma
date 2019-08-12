package com.gykj.zhumulangma.user.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SizeUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.widget.TScrollView;
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
public class MainUserFragment extends BaseFragment implements TScrollView.OnScrollListener, BaseItemLayout.OnBaseItemClick {


    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private BaseItemLayout bilUser;
    private TScrollView mScrollView;
    private ImageView parallax;
    private SmartRefreshLayout refreshLayout;

    @Override
    protected int onBindLayout() {
        return R.layout.user_fragment_main;
    }

    @Override
    protected void initView(View view) {
        setSwipeBackEnable(false);
        ctbTrans = view.findViewById(R.id.ctb_trans);
        ctbWhite = view.findViewById(R.id.ctb_white);
        bilUser = view.findViewById(R.id.bil_user);
        mScrollView = view.findViewById(R.id.msv);
        parallax = view.findViewById(R.id.parallax);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        initBar();
        initItemList();

    }

    private void initBar() {

        ImageView transLeft=ctbTrans.getLeftCustomView().findViewById(R.id.iv1);
        ImageView transRight=ctbTrans.getRightCustomView().findViewById(R.id.iv1);


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

        ImageView whiteLeft=ctbWhite.getLeftCustomView().findViewById(R.id.iv1);
        ImageView whiteRight=ctbWhite.getRightCustomView().findViewById(R.id.iv1);
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
        valueList.add("帮助与反馈");
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
        mScrollView.setOnScrollListener(this);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                ctbTrans.setAlpha(1 - (float) offset / ctbTrans.getHeight());
                parallax.setTranslationY(offset);
            }
        });
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
        parallax.setTranslationY(-scrollY);
        ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
        ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
    }

    @Override
    public void onItemClick(int position) {

    }
}
