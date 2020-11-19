package com.gykj.zhumulangma.home.fragment;


import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.activity.RadioListActivity;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.adapter.RadioHistoryAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentRadioBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioViewModel;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:电台
 */
public class RadioFragment extends BaseRefreshFragment<HomeFragmentRadioBinding, RadioViewModel, Radio>
        implements View.OnClickListener {

    private RadioHistoryAdapter mHistoryAdapter;
    private RadioAdapter mLocalAdapter;
    private RadioAdapter mTopAdapter;
    // private String mCityCode;

    public RadioFragment() {
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
        initHistory();
        initLocal();
        initTop();
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.ivMore.setOnClickListener(this);
        mBinding.ivLess.setOnClickListener(this);
        mBinding.llLocal.setOnClickListener(this);
        mBinding.llCountry.setOnClickListener(this);
        mBinding.llProvince.setOnClickListener(this);
        mBinding.llInternet.setOnClickListener(this);
        mBinding.ihTop.setOnClickListener(view ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_RADIO_LIST)
                        .withInt(KeyCode.Home.TYPE, RadioListActivity.RANK)
                        .withString(KeyCode.Home.TITLE, "排行榜")));
        mBinding.ihHistory.setOnClickListener(view -> RouterUtil.navigateTo(Constants.Router.Listen.F_HISTORY));
        mBinding.ihLocal.setOnClickListener(view -> mViewModel.navigateToCity());
        mLocalAdapter.setOnItemClickListener((adapter, view, position) -> mViewModel.playRadio(mLocalAdapter.getItem(position)));
        mTopAdapter.setOnItemClickListener((adapter, view, position) -> mViewModel.playRadio(mTopAdapter.getItem(position)));
        mHistoryAdapter.setOnItemClickListener((adapter, view, position) ->
                mViewModel.playRadio(String.valueOf(mHistoryAdapter.getItem(position).getSchedule().getRadioId())));
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

    private void initHistory() {
        mHistoryAdapter = new RadioHistoryAdapter(R.layout.home_item_radio_line);
        mBinding.rvHistory.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvHistory.setHasFixedSize(true);
        mHistoryAdapter.bindToRecyclerView(mBinding.rvHistory);
    }

    private void initLocal() {
        mLocalAdapter = new RadioAdapter(R.layout.home_item_radio_line);
        mBinding.rvLocal.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvLocal.setHasFixedSize(true);
        mLocalAdapter.bindToRecyclerView(mBinding.rvLocal);
    }

    private void initTop() {
        mTopAdapter = new RadioAdapter(R.layout.home_item_radio_line);
        mBinding.rvTop.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvTop.setHasFixedSize(true);
        mTopAdapter.bindToRecyclerView(mBinding.rvTop);
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public Class<RadioViewModel> onBindViewModel() {
        return RadioViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getLocalsEvent().observe(this, radios -> {
            if (!CollectionUtils.isEmpty(radios)) {
                mLocalAdapter.setNewData(radios);
            }
        });
        mViewModel.getTopsEvent().observe(this, radios -> mTopAdapter.setNewData(radios));
        mViewModel.getHistorysEvent().observe(this, historyBeans -> {
            if (historyBeans.size() == 0) {
                mBinding.gpHistory.setVisibility(View.GONE);
            } else {
                mHistoryAdapter.setNewData(historyBeans);
                mBinding.gpHistory.setVisibility(View.VISIBLE);
            }
        });
        mViewModel.getCityNameEvent().observe(this, cn -> mBinding.ihLocal.setTitle(cn));
        mViewModel.getStartLocationEvent().observe(this, aVoid -> startLocation());
        mViewModel.getTitleEvent().observe(this, s -> mBinding.ihLocal.setTitle(s));
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

        new RxPermissions(RadioFragment.this).request(Manifest.permission.ACCESS_COARSE_LOCATION)
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
        } else if (id == R.id.ll_local) {
            mViewModel.navigateToProvince();
        } else if (id == R.id.ll_country) {
            RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListActivity.COUNTRY)
                    .withString(KeyCode.Home.TITLE, "国家台"));
        } else if (id == R.id.ll_province) {
            RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListActivity.PROVINCE)
                    .withString(KeyCode.Home.TITLE, "省市台"));
        } else if (id == R.id.ll_internet) {
            RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListActivity.INTERNET)
                    .withString(KeyCode.Home.TITLE, "网络台"));
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
