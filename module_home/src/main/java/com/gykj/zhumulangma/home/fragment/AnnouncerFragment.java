package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.gykj.zhumulangma.common.widget.TScrollView;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AnnouncerAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/10 8:23
 * Email: 1071931588@qq.com
 * Description:
 */
public class AnnouncerFragment extends BaseMvvmFragment<AnnouncerViewModel> implements OnBannerListener,
        BaseQuickAdapter.OnItemClickListener, OnRefreshLoadMoreListener, TScrollView.OnScrollListener {

    private static final String TAG = "AnnouncerFragment";
    Banner banner;
    RecyclerView rvAnnouncer;
    SmartRefreshLayout refreshLayout;

    private AnnouncerAdapter mAnnouncerAdapter;


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_announcer;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }
    protected void initView(View view) {

        rvAnnouncer =fd(R.id.rv_announcer);
        refreshLayout=fd(R.id.refreshLayout);

        initBanner();
        initList();
    }

    @Override
    public void initListener() {
        super.initListener();
        banner.setOnBannerListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAnnouncerAdapter.setOnItemClickListener(this);
        ((TScrollView)fd(R.id.tsv_content)).setOnScrollListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getBannerList();
        mViewModel.getAnnouncerList();
    }

    private void initBanner() {
        banner= fd(R.id.banner);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setDelayTime(3000);
    }

    private void initList() {
        mAnnouncerAdapter = new AnnouncerAdapter(R.layout.home_item_announcer);
        rvAnnouncer.setLayoutManager(new LinearLayoutManager(mContext));
//        rvAnnouncer.setHasFixedSize(true);
        mAnnouncerAdapter.bindToRecyclerView(rvAnnouncer);
    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if(banner!=null)
            banner.startAutoPlay();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        banner.stopAutoPlay();
    }

    @Override
    public Class<AnnouncerViewModel> onBindViewModel() {
        return AnnouncerViewModel.class;
    }
    @Override
    protected boolean enableSimplebar() {
        return false;
    }
    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getBannerV2SingleLiveEvent().observe(this, bannerV2s -> {
            List<String> images = new ArrayList<>();
            for (BannerV2 bannerV2 : bannerV2s) {
                images.add(bannerV2.getBannerUrl());
            }
            banner.setImages(images).setImageLoader(new MainHomeFragment.GlideImageLoader()).start();
        });


        mViewModel.getAnnouncerSingleLiveEvent().observe(this, announcers -> {
            if(null==announcers||(mAnnouncerAdapter.getData().size()==0&&announcers.size()==0)){
                showNoDataView(true);
                return;
            }
            if (announcers.size() > 0) {
                mAnnouncerAdapter.addData(announcers);
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getAnnouncerList();
    }

    @Override
    public void OnBannerClick(int position) {
        BannerV2 bannerV2 = mViewModel.getBannerV2SingleLiveEvent().getValue().get(position);
        switch (bannerV2.getBannerContentType()){
            case 2:
                Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getAlbumId())
                        .navigation();
                EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                        new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
                break;
            case 3:
                mViewModel.play(bannerV2.getTrackId());
                break;
            case 1:
                Object navigation1 = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID,bannerV2.getBannerUid())
                        .navigation();
                EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                        new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation1)));

                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
                .withLong(KeyCode.Home.ANNOUNCER_ID, mAnnouncerAdapter.getItem(position).getAnnouncerId())
                .withString(KeyCode.Home.ANNOUNCER_NAME, mAnnouncerAdapter.getItem(position).getNickname())
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation)));
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getAnnouncerList();
    }

    @Override
    public void onScroll(int scrollY) {
        TLog.d(scrollY);
        TLog.d(fd(R.id.ll_title).getTop());
        fd(R.id.fl_title_top).setVisibility(scrollY>fd(R.id.ll_title).getTop()?View.VISIBLE:View.GONE);
    }
}
