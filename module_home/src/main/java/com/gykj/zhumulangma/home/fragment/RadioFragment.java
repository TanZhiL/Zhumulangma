package com.gykj.zhumulangma.home.fragment;


import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.gykj.zhumulangma.common.widget.ItemHeader;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.adapter.RadioHistoryAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioViewModel;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.radio.CityList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation.ISupportFragment;


public class RadioFragment extends BaseMvvmFragment<RadioViewModel> implements View.OnClickListener {

    private RecyclerView rvHistory;
    private RadioHistoryAdapter mHistoryAdapter;
    private RecyclerView rvLocal;
    private RadioAdapter mLocalAdapter;
    private RecyclerView rvTop;
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
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.RANK)
                    .withString(KeyCode.Home.TITLE, "排行榜")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        });
        fd(R.id.ih_history).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.HISTORY)
                    .withString(KeyCode.Home.TITLE, "最近收听")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        });
        fd(R.id.ih_local).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.LOCAL_CITY)
                    .withString(KeyCode.Home.TITLE, SPUtils.getInstance().getString(AppConstants.SP.CITY_NAME, AppConstants.Defualt.CITY_NAME))
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        });
        mLocalAdapter.setOnItemClickListener((adapter, view, position) -> {
            RadioUtil.getInstance(mContext).playLiveRadioForSDK(mLocalAdapter.getData().get(position));
            navigateTo(AppConstants.Router.Home.F_PLAY_RADIIO);
        });
        mTopAdapter.setOnItemClickListener((adapter, view, position) -> {
            RadioUtil.getInstance(mContext).playLiveRadioForSDK(mTopAdapter.getData().get(position));
            navigateTo(AppConstants.Router.Home.F_PLAY_RADIIO);
        });
        mHistoryAdapter.setOnItemClickListener((adapter, view, position) ->
                mViewModel.play(String.valueOf(mHistoryAdapter.getData().get(position).getSchedule().getRadioId())));
    }

    @Override
    public void initData() {


        mViewModel.getTopList();
        mViewModel._getHistory();
        getLocalData();
    }

    private void getLocalData() {
        String cityCode = SPUtils.getInstance().getString(AppConstants.SP.CITY_CODE,AppConstants.Defualt.CITY_CODE);
        String cityName = SPUtils.getInstance().getString(AppConstants.SP.CITY_NAME, AppConstants.Defualt.CITY_NAME);

        ItemHeader itemHeader = fd(R.id.ih_local);
        itemHeader.setTitle(cityName);
        mViewModel.getLocalList(cityCode);
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
            TLog.d(aMapLocation.toString());
            if(!TextUtils.isEmpty(aMapLocation.getAdCode())&&!cityCode.equals(aMapLocation.getAdCode().substring(0,4))){
                String city = aMapLocation.getCity();
                String province = aMapLocation.getProvince();
                SPUtils.getInstance().put(AppConstants.SP.CITY_CODE,aMapLocation.getAdCode().substring(0,4),true);
                SPUtils.getInstance().put(AppConstants.SP.CITY_NAME,city.substring(0,city.length()-1),true);
                SPUtils.getInstance().put(AppConstants.SP.PROVINCE_CODE,aMapLocation.getAdCode().substring(0,3)+"000",true);
                SPUtils.getInstance().put(AppConstants.SP.PROVINCE_NAME,province.substring(0,province.length()-1),true);

                itemHeader.setTitle(SPUtils.getInstance().getString(AppConstants.SP.CITY_NAME));
                mViewModel.getLocalList(SPUtils.getInstance().getString(AppConstants.SP.CITY_CODE));
            }
        });
        mLocationClient.setLocationOption(option);
        new RxPermissions(this).requestEach(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION})
                .subscribe(permission -> {
                    if (permission.granted) {
                        mLocationClient.startLocation();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        ToastUtil.showToast("无法获取本地电台,请允许应用获取位置信息");
                    } else {
                        ToastUtil.showToast("无法获取本地电台,请允许应用获取位置信息");
                    }
                });
    }

    private void initHistory() {
        rvHistory = fd(R.id.rv_history);
        mHistoryAdapter = new RadioHistoryAdapter(R.layout.home_item_radio);
        rvHistory.setLayoutManager(new LinearLayoutManager(mContext));
        rvHistory.setHasFixedSize(true);
        mHistoryAdapter.bindToRecyclerView(rvHistory);
    }

    private void initLocal() {
        rvLocal = fd(R.id.rv_local);
        mLocalAdapter = new RadioAdapter(R.layout.home_item_radio);
        rvLocal.setLayoutManager(new LinearLayoutManager(mContext));
        rvLocal.setHasFixedSize(true);
        mLocalAdapter.bindToRecyclerView(rvLocal);
    }

    private void initTop() {
        rvTop = fd(R.id.rv_top);
        mTopAdapter = new RadioAdapter(R.layout.home_item_radio);
        rvTop.setLayoutManager(new LinearLayoutManager(mContext));
        rvTop.setHasFixedSize(true);
        mTopAdapter.bindToRecyclerView(rvTop);
    }

    @Override
    protected boolean enableSimplebar() {
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
        mViewModel.getLocalSingleLiveEvent().observe(this, radios -> {
            if(!CollectionUtils.isEmpty(radios)){
                mLocalAdapter.setNewData(radios);
            }
        });
        mViewModel.getTopSingleLiveEvent().observe(this, radios -> mTopAdapter.setNewData(radios));
        mViewModel.getHistorySingleLiveEvent().observe(this, historyBeans -> {
            if (historyBeans.size() == 0) {
                rvHistory.setVisibility(View.GONE);
                fd(R.id.ih_history).setVisibility(View.GONE);
            } else {
                mHistoryAdapter.setNewData(historyBeans);
                rvHistory.setVisibility(View.VISIBLE);
                fd(R.id.ih_history).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        mViewModel._getHistory();
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
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.LOCAL)
                    .withString(KeyCode.Home.TITLE,SPUtils.getInstance().getString(
                            AppConstants.SP.PROVINCE_NAME,AppConstants.Defualt.PROVINCE_NAME))
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        } else if (id == R.id.ll_country) {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.COUNTRY)
                    .withString(KeyCode.Home.TITLE, "国家台")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        } else if (id == R.id.ll_province) {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.PROVINCE)
                    .withString(KeyCode.Home.TITLE, "省市台")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        } else if (id == R.id.ll_internet) {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.INTERNET)
                    .withString(KeyCode.Home.TITLE, "网络台")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        }
    }
}
