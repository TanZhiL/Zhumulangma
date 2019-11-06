package com.gykj.zhumulangma.home.fragment;


import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TFragmentPagerAdapter;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.databinding.HomeFragmentMainBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HomeViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.sunfusheng.marqueeview.MarqueeView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;

import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:首页
 */
@Route(path = Constants.Router.Home.F_MAIN)
public class MainHomeFragment extends BaseMvvmFragment<HomeFragmentMainBinding, HomeViewModel>
        implements View.OnClickListener, MarqueeView.OnItemClickListener {


    public MainHomeFragment() {
    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_main;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }

    @Override
    protected void initView() {
        String[] tabs = {"热门", "分类", "精品", "主播", "广播"};

        if (StatusBarUtils.supportTransparentStatusBar()) {
            mBinding.clTitlebar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        }
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HotFragment());
        fragments.add(new CategoryFragment());
        fragments.add(new FineFragment());
        fragments.add(new AnnouncerFragment());
        fragments.add(new RadioFragment());

        TFragmentPagerAdapter adapter = new TFragmentPagerAdapter(
                getChildFragmentManager(), fragments);
        mBinding.viewpager.setOffscreenPageLimit(4);
        mBinding.viewpager.setAdapter(adapter);

        final CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), mBinding.viewpager, 50));
        commonNavigator.setAdjustMode(true);
        mBinding.magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mBinding.magicIndicator, mBinding.viewpager);

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
                    RouterUtil.navigateTo(postcard);
                });

        mBinding.ivDownload.setOnClickListener(this);
        mBinding.ivHistory.setOnClickListener(this);
        RxView.clicks(mBinding.ivMessage)
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> RouterUtil.navigateTo(Constants.Router.User.F_MESSAGE));
        mBinding.marqueeView.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getHotWords();
    }

    @Override
    protected void onRevisible() {
        super.onRevisible();
        if (CollectionUtils.isEmpty(mBinding.marqueeView.getMessages())) {
            mViewModel.getHotWords();
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
                            RouterUtil.navigateTo(Constants.Router.Home.F_SCAN);
                        } else {
                            ToastUtil.showToast("请允许应用使用相机权限");
                        }
                    });

        } else if (id == R.id.iv_history) {
            RouterUtil.navigateTo(Constants.Router.Listen.F_HISTORY);
        }

    }

    @Override
    public Class<HomeViewModel> onBindViewModel() {
        return HomeViewModel.class;
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
    }

    @Override
    public void onItemClick(int position, TextView textView) {
        RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_SEARCH)
                .withString(KeyCode.Home.HOTWORD, (String) mBinding.marqueeView.getMessages().get(position)));
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (mBinding.marqueeView != null && !CollectionUtils.isEmpty(mBinding.marqueeView.getMessages())) {
            mBinding.marqueeView.startFlipping();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (mBinding.marqueeView != null) {
            mBinding.marqueeView.stopFlipping();
        }
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }
}
