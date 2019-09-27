package com.gykj.zhumulangma.home.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.adapter.TFragmentPagerAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.home.R;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * 搜索结果分类页面
 */
@Route(path = AppConstants.Router.Home.F_SEARCH_RESULT)
public class SearchResultFragment extends BaseFragment {

    @Autowired(name = KeyCode.Home.KEYWORD)
    public String mKeyword;

    private String[] mTabs = {"专辑", "声音", "主播", "广播"};
    private List<Fragment> mFragments = new ArrayList<>();

    public SearchResultFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_search_result;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {

        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        ViewPager viewpager = view.findViewById(R.id.viewpager);

        Fragment albumFragment = new SearchAlbumFragment();
        Fragment trackFragment = new SearchTrackFragment();
        Fragment announcerFragment = new SearchAnnouncerFragment();
        Fragment radioFragment = new SearchRadioFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KeyCode.Home.KEYWORD, getArguments().getString(KeyCode.Home.KEYWORD));

        albumFragment.setArguments(bundle);
        trackFragment.setArguments(bundle);
        announcerFragment.setArguments(bundle);
        radioFragment.setArguments(bundle);

        mFragments.add(albumFragment);
        mFragments.add(trackFragment);
        mFragments.add(announcerFragment);
        mFragments.add(radioFragment);

        TFragmentPagerAdapter adapter = new TFragmentPagerAdapter(
                getChildFragmentManager(), mFragments);
        viewpager.setOffscreenPageLimit(4);
        viewpager.setAdapter(adapter);

        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(mTabs), viewpager, 75));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
    }

    @Override
    public void initData() {

    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }



    @Override
    protected boolean enableSimplebar() {
        return false;
    }
}
