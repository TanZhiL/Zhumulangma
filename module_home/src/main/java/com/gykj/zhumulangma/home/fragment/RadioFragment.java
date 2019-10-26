package com.gykj.zhumulangma.home.fragment;


import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.widget.ItemHeader;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.adapter.RadioHistoryAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:电台
 */
public class RadioFragment extends BaseRefreshMvvmFragment<RadioViewModel, Radio> implements View.OnClickListener {

    private RadioHistoryAdapter mHistoryAdapter;
    private RadioAdapter mLocalAdapter;
    private RadioAdapter mTopAdapter;

    private ViewGroup clMore;
    private ImageView ivMore;

    public RadioFragment() {
    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_radio;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackground(null);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        clMore = fd(R.id.cl_more);
        ivMore = fd(R.id.iv_more);
        initHistory();
        initLocal();
        initTop();
    }

    @Override
    public void initListener() {
        super.initListener();
        ivMore.setOnClickListener(this);
        fd(R.id.iv_less).setOnClickListener(this);
        fd(R.id.ll_local).setOnClickListener(this);
        fd(R.id.ll_country).setOnClickListener(this);
        fd(R.id.ll_province).setOnClickListener(this);
        fd(R.id.ll_internet).setOnClickListener(this);
        fd(R.id.ih_top).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.RANK)
                    .withString(KeyCode.Home.TITLE, "排行榜")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        });
        fd(R.id.ih_history).setOnClickListener(view -> RouteUtil.navigateTo(Constants.Router.Listen.F_HISTORY));
        fd(R.id.ih_local).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.LOCAL_CITY)
                    .withString(KeyCode.Home.TITLE, SPUtils.getInstance().getString(Constants.SP.CITY_NAME, Constants.Default.CITY_NAME))
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        });
        mLocalAdapter.setOnItemClickListener((adapter, view, position) -> mViewModel.playRadio(mLocalAdapter.getItem(position)));
        mTopAdapter.setOnItemClickListener((adapter, view, position) -> mViewModel.playRadio(mTopAdapter.getItem(position)));
        mHistoryAdapter.setOnItemClickListener((adapter, view, position) ->
                mViewModel.playRadio(String.valueOf(mHistoryAdapter.getItem(position).getSchedule().getRadioId())));
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(fd(R.id.refreshLayout), null);
    }


    @Override
    public void initData() {

        String cityCode = SPUtils.getInstance().getString(Constants.SP.CITY_CODE, Constants.Default.CITY_CODE);

        mViewModel.init(cityCode);
        String cityName = SPUtils.getInstance().getString(Constants.SP.CITY_NAME, Constants.Default.CITY_NAME);
        ItemHeader itemHeader = fd(R.id.ih_local);
        itemHeader.setTitle(cityName);

        //初始化定位
        AMapLocationClient mLocationClient = new AMapLocationClient(mApplication);
        AMapLocationClientOption option = new AMapLocationClientOption();
        //获取一次定位结果：
        option.setOnceLocation(true);
        option.setLocationCacheEnable(false);
        option.setNeedAddress(true);
        option.setMockEnable(true);
        // 设置定位回调监听
        mLocationClient.setLocationListener(aMapLocation -> {
            if (!TextUtils.isEmpty(aMapLocation.getAdCode()) && !cityCode.equals(aMapLocation.getAdCode().substring(0, 4))) {
                String city = aMapLocation.getCity();
                String province = aMapLocation.getProvince();
                SPUtils.getInstance().put(Constants.SP.CITY_CODE, aMapLocation.getAdCode().substring(0, 4), true);
                SPUtils.getInstance().put(Constants.SP.CITY_NAME, city.substring(0, city.length() - 1), true);
                SPUtils.getInstance().put(Constants.SP.PROVINCE_CODE, aMapLocation.getAdCode().substring(0, 3) + "000", true);
                SPUtils.getInstance().put(Constants.SP.PROVINCE_NAME, province.substring(0, province.length() - 1), true);

                itemHeader.setTitle(SPUtils.getInstance().getString(Constants.SP.CITY_NAME));
                mViewModel.init(SPUtils.getInstance().getString(Constants.SP.CITY_CODE));
            }
        });
        mLocationClient.setLocationOption(option);
        new RxPermissions(this).request(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION})
                .subscribe(granted -> {
                    if (granted) {
                        mLocationClient.startLocation();
                    } else {
                        ToastUtil.showToast("无法获取本地电台,请允许应用获取位置信息");
                    }
                });
    }

    @Override
    protected void onReload(View v) {
        showInitView();
        mViewModel.onViewRefresh();
    }

    private void initHistory() {
        RecyclerView rvHistory = fd(R.id.rv_history);
        mHistoryAdapter = new RadioHistoryAdapter(R.layout.home_item_radio_line);
        rvHistory.setLayoutManager(new LinearLayoutManager(mActivity));
        rvHistory.setHasFixedSize(true);
        mHistoryAdapter.bindToRecyclerView(rvHistory);
    }

    private void initLocal() {
        RecyclerView rvLocal = fd(R.id.rv_local);
        mLocalAdapter = new RadioAdapter(R.layout.home_item_radio_line);
        rvLocal.setLayoutManager(new LinearLayoutManager(mActivity));
        rvLocal.setHasFixedSize(true);
        mLocalAdapter.bindToRecyclerView(rvLocal);
    }

    private void initTop() {
        RecyclerView rvTop = fd(R.id.rv_top);
        mTopAdapter = new RadioAdapter(R.layout.home_item_radio_line);
        rvTop.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTop.setHasFixedSize(true);
        mTopAdapter.bindToRecyclerView(rvTop);
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
                fd(R.id.gp_history).setVisibility(View.GONE);
            } else {
                mHistoryAdapter.setNewData(historyBeans);
                fd(R.id.gp_history).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        mViewModel.getHistory();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == ivMore) {
            clMore.setVisibility(View.VISIBLE);
            ivMore.setVisibility(View.GONE);
        } else if (id == R.id.iv_less) {
            clMore.setVisibility(View.GONE);
            ivMore.setVisibility(View.VISIBLE);
        } else if (id == R.id.ll_local) {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.LOCAL_PROVINCE)
                    .withString(KeyCode.Home.TITLE, SPUtils.getInstance().getString(
                            Constants.SP.PROVINCE_NAME, Constants.Default.PROVINCE_NAME))
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        } else if (id == R.id.ll_country) {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.COUNTRY)
                    .withString(KeyCode.Home.TITLE, "国家台")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        } else if (id == R.id.ll_province) {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.PROVINCE)
                    .withString(KeyCode.Home.TITLE, "省市台")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        } else if (id == R.id.ll_internet) {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.INTERNET)
                    .withString(KeyCode.Home.TITLE, "网络台")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        }
    }
    @Override
    public  void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Home.TAB_REFRESH:
                if(isSupportVisible()&&mBaseLoadService.getCurrentCallback()!= getInitStatus().getClass()){
                    fd(R.id.nsv).scrollTo(0,0);
                    ((SmartRefreshLayout)fd(R.id.refreshLayout)).autoRefresh();
                }
                break;
        }
    }
}
