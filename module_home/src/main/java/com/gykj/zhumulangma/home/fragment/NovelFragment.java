package com.gykj.zhumulangma.home.fragment;


import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.extra.GlideImageLoader;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.activity.AlbumListActivity;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.adapter.HotLikeAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentRadioBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.NovelViewModel;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.NOVE_DAILY_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.NOVE_DAJIA_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.NOVE_YOUNG_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.NOVE_ZHANGGUI_ID;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:小说
 */
public class NovelFragment extends BaseRefreshFragment<HomeFragmentRadioBinding, NovelViewModel, Radio>
        implements OnBannerListener, View.OnClickListener {

    // private String mCityCode;
    private HotLikeAdapter mDailyAdapter;
    private HotLikeAdapter mDajiaAdapter;
    private HotLikeAdapter mZhangguiAdapter;
    private AlbumAdapter mYoungAdapter;

    public NovelFragment() {
    }


    @Override
    public int onBindLayout() {
        return R.layout.home_fragment_radio;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //避免过度绘制
        mView.setBackground(null);
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initView() {
        initBanner();
        initDaily();
        initDajia();
        initZhanggui();
        initYoung();
    }

    private void initBanner() {
        mBinding.banner.setOnBannerListener(this);
        mBinding.banner.setIndicatorGravity(BannerConfig.RIGHT);
        mBinding.banner.setDelayTime(3000);
    }

    private void initDaily() {
        mDailyAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvHistory.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvHistory.setHasFixedSize(true);
        mDailyAdapter.bindToRecyclerView(mBinding.rvHistory);

    }

    private void initDajia() {
        mDajiaAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvLocal.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvLocal.setHasFixedSize(true);
        mDajiaAdapter.bindToRecyclerView(mBinding.rvLocal);
    }

    private void initZhanggui() {
        mZhangguiAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvTop.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvTop.setHasFixedSize(true);
        mZhangguiAdapter.bindToRecyclerView(mBinding.rvTop);
    }

    private void initYoung() {
        mYoungAdapter = new AlbumAdapter(R.layout.home_item_album);
        mBinding.rvYoung.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvYoung.setHasFixedSize(true);
        mYoungAdapter.bindToRecyclerView(mBinding.rvYoung);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.ivMore.setOnClickListener(this);
        mBinding.ivLess.setOnClickListener(this);
        mBinding.ihTop.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                        .withString(KeyCode.Home.COLUMN, NOVE_ZHANGGUI_ID)
                        .withString(KeyCode.Home.TITLE, mViewModel.getZhangguiNameEvent().getValue())));
        mBinding.ihHistory.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                        .withString(KeyCode.Home.COLUMN, NOVE_DAILY_ID)
                        .withString(KeyCode.Home.TITLE, mViewModel.getDailyNameEvent().getValue())));
        mBinding.ihLocal.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                        .withString(KeyCode.Home.COLUMN, NOVE_DAJIA_ID)
                        .withString(KeyCode.Home.TITLE, mViewModel.getDajiaNameEvent().getValue())));
        mBinding.ihYoung.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                        .withString(KeyCode.Home.COLUMN, NOVE_YOUNG_ID)
                        .withString(KeyCode.Home.TITLE, mViewModel.getYoungNameEvent().getValue())));
        mBinding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    int bottom = mBinding.banner.getBottom();
                    if (scrollY > bottom) {
                        mBinding.banner.stopAutoPlay();
                    } else {
                        mBinding.banner.startAutoPlay();
                    }
                });
        mBinding.youngRefresh.setOnClickListener(this);
        mBinding.dailyRefresh.setOnClickListener(this);
        mBinding.dajiaRefresh.setOnClickListener(this);
        mBinding.zhangguiRefresh.setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, null);
    }


    @Override
    public void initData() {
        mViewModel.init();
    }

    @Override
    protected void onReload(View v) {
        showInitView();
        mViewModel.onViewRefresh();
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public Class<NovelViewModel> onBindViewModel() {
        return NovelViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getBannerEvent().observe(this, bannerV2s -> {
            List<String> images = new ArrayList<>();
            for (BannerBean bannerV2 : bannerV2s) {
                images.add(bannerV2.getBannerCoverUrl());
            }
            mBinding.banner.setImages(images).setImageLoader(new GlideImageLoader()).start();
        });
        mViewModel.getDailyEvent().observe(this, radios -> {
            if (!CollectionUtils.isEmpty(radios)) {
                mDailyAdapter.setNewData(radios);
            }
        });
        mViewModel.getDajiaEvent().observe(this, radios -> mDajiaAdapter.setNewData(radios));
        mViewModel.getYoungEvent().observe(this, radios -> mYoungAdapter.setNewData(radios));
        mViewModel.getZhangguiEvent().observe(this, historyBeans ->
                mZhangguiAdapter.setNewData(historyBeans));
        mViewModel.getDailyNameEvent().observe(this, s -> mBinding.ihHistory.setTitle(s));
        mViewModel.getDajiaNameEvent().observe(this, s -> mBinding.ihLocal.setTitle(s));
        mViewModel.getZhangguiNameEvent().observe(this, s -> mBinding.ihTop.setTitle(s));
        mViewModel.getYoungNameEvent().observe(this, s -> mBinding.ihYoung.setTitle(s));
//        mViewModel.getCityNameEvent().observe(this, cn -> mBinding.ihLocal.setTitle(cn));
//        mViewModel.getStartLocationEvent().observe(this, aVoid -> startLocation());
//        mViewModel.getTitleEvent().observe(this, s -> mBinding.ihLocal.setTitle(s));
    }

    private void startLocation() {
        //初始化定位
        AMapLocationClient locationClient = new AMapLocationClient(mApplication);
        AMapLocationClientOption option = new AMapLocationClientOption();
        //获取一次定位结果：
        option.setOnceLocation(true);
        option.setLocationCacheEnable(false);
        option.setNeedAddress(true);
        option.setMockEnable(true);
        // 设置定位回调监听
        locationClient.setLocationListener(aMapLocation -> mViewModel.saveLocation(aMapLocation));
        locationClient.setLocationOption(option);

        new RxPermissions(NovelFragment.this).request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        locationClient.startLocation();
                    } else {
                        ToastUtil.showToast("无法获取本地电台,请允许应用获取位置信息");
                    }
                }, Throwable::printStackTrace);
    }

    @Override
    protected void onRevisible() {
        super.onRevisible();
        mViewModel.getHistory();
        if (mBinding != null) {
            mBinding.banner.startAutoPlay();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding != null) {
            mBinding.banner.stopAutoPlay();
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == mBinding.ivMore) {
            mBinding.clMore.setVisibility(View.VISIBLE);
            mBinding.ivMore.setVisibility(View.GONE);
        } else if (id == R.id.iv_less) {
            mBinding.clMore.setVisibility(View.GONE);
            mBinding.ivMore.setVisibility(View.VISIBLE);
        }else if (id == R.id.young_refresh) {
            mViewModel.getYoungList();
        }else if (id == R.id.daily_refresh) {
            mViewModel.getDailyList();
        }else if (id == R.id.dajia_refresh) {
            mViewModel.getDajiaList();
        }else if (id == R.id.zhanggui_refresh) {
            mViewModel.getZhangguiList();
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

    @Override
    public void OnBannerClick(int position) {
        BannerBean bannerV2 = mViewModel.getBannerEvent().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getBannerContentId()));
                break;
            case 3:
                mViewModel.playTrack(bannerV2.getBannerContentId());
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
}
