package com.gykj.zhumulangma.home.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TFragmentStateAdapter;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.extra.ViewPagerHelper;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.databinding.HomeFragmentSearchResultBinding;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索下结果页
 */
@Route(path = Constants.Router.Home.F_SEARCH_RESULT)
public class SearchResultFragment extends BaseFragment<HomeFragmentSearchResultBinding> {

    @Autowired(name = KeyCode.Home.KEYWORD)
    public String mKeyword;

    public SearchResultFragment() {

    }

    @Override
    public int onBindLayout() {
        return R.layout.home_fragment_search_result;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }

    @Override
    public void initView() {
        String[] tabs = {"专辑", "声音", "主播"};
        Fragment albumFragment = new SearchAlbumFragment();
        Fragment trackFragment = new SearchTrackFragment();
        Fragment announcerFragment = new SearchAnnouncerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KeyCode.Home.KEYWORD, getArguments().getString(KeyCode.Home.KEYWORD));
        albumFragment.setArguments(bundle);
        trackFragment.setArguments(bundle);
        announcerFragment.setArguments(bundle);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(albumFragment);
        fragments.add(trackFragment);
        fragments.add(announcerFragment);

        TFragmentStateAdapter adapter = new TFragmentStateAdapter(
                this, fragments);
        mBinding.viewpager.setOffscreenPageLimit(fragments.size());
        mBinding.viewpager.setAdapter(adapter);

        final CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdjustMode(true);

        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), mBinding.viewpager, 75));
        mBinding.magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mBinding.magicIndicator, mBinding.viewpager);
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
