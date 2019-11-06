package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.extra.GlideImageLoader;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AnnouncerAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentAnnouncerBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerViewModel;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:主播
 */
public class AnnouncerFragment extends BaseRefreshMvvmFragment<HomeFragmentAnnouncerBinding, AnnouncerViewModel, Announcer>
        implements OnBannerListener {

    private AnnouncerAdapter mAnnouncerAdapter;


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_announcer;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    protected void initView() {
        mBinding.banner.setIndicatorGravity(BannerConfig.RIGHT);
        mBinding.banner.setDelayTime(3000);
        mAnnouncerAdapter = new AnnouncerAdapter(R.layout.home_item_announcer);
        mBinding.rvAnnouncer.setLayoutManager(new LinearLayoutManager(mActivity));
        mAnnouncerAdapter.bindToRecyclerView(mBinding.rvAnnouncer);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.banner.setOnBannerListener(this);
        mAnnouncerAdapter.setOnItemClickListener((adapter, view, position) ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, mAnnouncerAdapter.getData().get(position).getAnnouncerId())
                        .withString(KeyCode.Home.ANNOUNCER_NAME, mAnnouncerAdapter.getData().get(position).getNickname())));
        mBinding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (nestedScrollView, i, i1, i2, i3) ->
                        mBinding.flTitleTop.setVisibility(i1 > mBinding.llTitle.getTop() ? View.VISIBLE : View.GONE));
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mAnnouncerAdapter);
    }

    @Override
    public void initData() {
        mViewModel.init();
    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (null != mBinding)
            mBinding.banner.startAutoPlay();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (null != mBinding)
            mBinding.banner.stopAutoPlay();
    }

    @Override
    public Class<AnnouncerViewModel> onBindViewModel() {
        return AnnouncerViewModel.class;
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getBannerV2Event().observe(this, bannerV2s -> {
            List<String> images = new ArrayList<>();
            for (BannerV2 bannerV2 : bannerV2s) {
                images.add(bannerV2.getBannerUrl());
            }
            mBinding.banner.setImages(images).setImageLoader(new GlideImageLoader()).start();
        });
        mViewModel.getInitAnnouncerEvent().observe(this, announcers -> mAnnouncerAdapter.setNewData(announcers));
    }

    @Override
    public void OnBannerClick(int position) {
        BannerV2 bannerV2 = mViewModel.getBannerV2Event().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getAlbumId()));
                break;
            case 3:
                mViewModel.play(bannerV2.getTrackId());
                break;
            case 1:
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, bannerV2.getBannerUid()));
                break;
        }
    }


    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Home.TAB_REFRESH:
                if (isSupportVisible() && mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
                    mBinding.nsv.scrollTo(0, 0);
                    mBinding.refreshLayout.autoRefresh();
                }
                break;
        }
    }
}
