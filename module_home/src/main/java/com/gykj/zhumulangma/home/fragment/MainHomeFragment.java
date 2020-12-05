package com.gykj.zhumulangma.home.fragment;


import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TFragmentStateAdapter;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.extra.ViewPagerHelper;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.bean.TabBean;
import com.gykj.zhumulangma.home.databinding.HomeFragmentMainBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.MainHomeViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.sunfusheng.marqueeview.MarqueeView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:首页
 */
@Route(path = Constants.Router.Home.F_MAIN)
public class MainHomeFragment extends BaseMvvmFragment<HomeFragmentMainBinding, MainHomeViewModel>
        implements View.OnClickListener, MarqueeView.OnItemClickListener {

    public MainHomeFragment() {
    }

    @Override
    public int onBindLayout() {
        return R.layout.home_fragment_main;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initView() {
        if (StatusBarUtils.supportTransparentStatusBar()) {
            mBinding.clTitlebar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        RxView.clicks(mBinding.llSearch)
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    Postcard postcard = mRouter.build(Constants.Router.Home.F_SEARCH);
                    if (!CollectionUtils.isEmpty(mBinding.marqueeView.getMessages())) {
                        postcard.withString(KeyCode.Home.HOTWORD, (String) mBinding.marqueeView.getMessages()
                                .get(mBinding.marqueeView.getPosition()));
                    }
                    RouteHelper.navigateTo(postcard);
                });

        mBinding.ivDownload.setOnClickListener(this);
        mBinding.ivHistory.setOnClickListener(this);
        RxView.clicks(mBinding.ivMessage)
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> RouteHelper.navigateTo(Constants.Router.User.F_MESSAGE));
        mBinding.marqueeView.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel.init();
    }

    @Override
    protected void onRevisible() {
        super.onRevisible();
        if (CollectionUtils.isEmpty(mBinding.marqueeView.getMessages())) {
            mViewModel.getHotWords();
        }else {
            mBinding.marqueeView.startFlipping();
        }
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_download) {
            new RxPermissions(this).requestEach(new String[]{Manifest.permission.CAMERA})
                    .subscribe(permission -> {
                        if (permission.granted) {
                            RouteHelper.navigateTo(Constants.Router.Discover.F_SCAN);
                        } else {
                            ToastUtil.showToast("请允许应用使用相机权限");
                        }
                    });

        } else if (id == R.id.iv_history) {
            RouteHelper.navigateTo(Constants.Router.Listen.F_HISTORY);
        }

    }

    @Override
    public Class<MainHomeViewModel> onBindViewModel() {
        return MainHomeViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getHotWordsEvent().observe(this, hotWords -> {
            List<String> words = new ArrayList<>(hotWords.size());
            for (HotWord word : hotWords) {
                words.add(word.getSearchword());
            }
            mBinding.marqueeView.startWithList(words);
        });
        mViewModel.getTabsEvent().observe(this, tabBeans -> {
            List<String> titles = new ArrayList<>(tabBeans.size());
            List<Fragment> fragments = new ArrayList<>();
            for (TabBean tabBean : tabBeans) {
                HomeFragment homeFragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(KeyCode.Home.TAB,tabBean);
                homeFragment.setArguments(bundle);
                titles.add(tabBean.getCatName());
                fragments.add(homeFragment);
            }
            TFragmentStateAdapter adapter = new TFragmentStateAdapter(MainHomeFragment.this, fragments);
            mBinding.viewpager.setOffscreenPageLimit(fragments.size());
            mBinding.viewpager.setAdapter(adapter);

            final CommonNavigator commonNavigator = new CommonNavigator(mActivity);
            commonNavigator.setAdapter(new TabNavigatorAdapter(titles, mBinding.viewpager, 50));
            commonNavigator.setAdjustMode(true);
            mBinding.magicIndicator.setNavigator(commonNavigator);
            ViewPagerHelper.bind(mBinding.magicIndicator, mBinding.viewpager);
        });
    }

    @Override
    public void onItemClick(int position, TextView textView) {
        RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_SEARCH)
                .withString(KeyCode.Home.HOTWORD, (String) mBinding.marqueeView.getMessages().get(position)));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding.marqueeView != null) {
            mBinding.marqueeView.stopFlipping();
        }
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }
}
