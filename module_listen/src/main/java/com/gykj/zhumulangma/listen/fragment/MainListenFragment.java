package com.gykj.zhumulangma.listen.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TFragmentPagerAdapter;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.listen.R;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:我听
 */
@Route(path = AppConstants.Router.Listen.F_MAIN)
public class MainListenFragment extends BaseFragment implements View.OnClickListener {

    public MainListenFragment() {
    }

    @Override
    protected int onBindLayout() {
        return R.layout.listen_fragment_main;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }
    @Override
    protected void initView(View view) {
        String[] tabs={"我的订阅","推荐订阅"};
        ViewPager viewpager = fd(R.id.viewpager);
        List<Fragment> pages=new ArrayList<>();
        pages.add(new SubscribeFragment());
        pages.add(new RecommendFragment());

        TFragmentPagerAdapter adapter = new TFragmentPagerAdapter(
                getChildFragmentManager(),pages);
        viewpager.setOffscreenPageLimit(2);
        viewpager.setAdapter(adapter);
        MagicIndicator magicIndicator = fd(R.id.magic_indicator);
        final CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), viewpager,60));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
    }

    @Override
    public void initListener() {
        super.initListener();
        fd(R.id.ll_download).setOnClickListener(this);
        fd(R.id.ll_history).setOnClickListener(this);
        fd(R.id.ll_favorite).setOnClickListener(this);
        fd(R.id.tv_purchased).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public int onBindBarLeftStyle() {
        return SimpleBarStyle.LEFT_ICON;
    }

    @Override
    public int onBindBarRightStyle() {
        return SimpleBarStyle.RIGHT_ICON;
    }

    @Override
    public Integer onBindBarLeftIcon() {
        return R.drawable.ic_common_message;
    }

    @Override
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_search};
    }

    @Override
    public String[] onBindBarTitleText() {
        return  new String[]{"我听"};
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.ll_download==id){
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_DOWNLOAD);
        }else  if(R.id.ll_history==id){
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_HISTORY);
        }else  if(R.id.ll_favorite==id){
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_FAVORITE);
        }
    }
    @Override
    public void onLeftIconClick(View v) {
        super.onLeftIconClick(v);
        RouteUtil.navigateTo(AppConstants.Router.User.F_MESSAGE);
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        RouteUtil.navigateTo(AppConstants.Router.Home.F_SEARCH);
    }
}
