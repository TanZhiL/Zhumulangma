package com.gykj.zhumulangma.home.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TBannerImageAdapter;
import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AnnouncerAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentAnnouncerBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerViewModel;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:主播
 */
public class AnnouncerFragment extends BaseRefreshFragment<HomeFragmentAnnouncerBinding, AnnouncerViewModel, Announcer>
        implements OnBannerListener {

    private AnnouncerAdapter mAnnouncerAdapter;


    @Override
    public int onBindLayout() {
        return R.layout.home_fragment_announcer;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    public void initView() {
        mBinding.banner.addBannerLifecycleObserver(this);
        mBinding.banner.setIndicator(new CircleIndicator(mActivity));
        mBinding.banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT);
        mBinding.banner.setOnBannerListener(this);
        mAnnouncerAdapter = new AnnouncerAdapter(R.layout.home_item_announcer);
        mBinding.rvAnnouncer.setLayoutManager(new LinearLayoutManager(mActivity));
        mAnnouncerAdapter.bindToRecyclerView(mBinding.rvAnnouncer);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.banner.setOnBannerListener(this);
        mAnnouncerAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
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
            mBinding.banner.setAdapter(new TBannerImageAdapter(bannerV2s));
            mBinding.banner.setOnBannerListener(this);
        });
        mViewModel.getInitAnnouncerEvent().observe(this, announcers -> mAnnouncerAdapter.setNewData(announcers));
    }

    @Override
    public void OnBannerClick(Object data, int position) {
        BannerBean bannerV2 = mViewModel.getBannerV2Event().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getBannerContentId()));
                break;
            case 3:
                mViewModel.play(bannerV2.getBannerContentId());
                break;
            case 1:
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, bannerV2.getBannerContentId()));
            case 4:
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Discover.F_WEB)
                        .withLong(KeyCode.Discover.PATH, bannerV2.getBannerContentId()));
                break;
        }
    }


    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Home.TAB_REFRESH:
                if (mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
                    mBinding.nsv.scrollTo(0, 0);
                    mBinding.refreshLayout.autoRefresh();
                }
                break;
        }
    }
}
