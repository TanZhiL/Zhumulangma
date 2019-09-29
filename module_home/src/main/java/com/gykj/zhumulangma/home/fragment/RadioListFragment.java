package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ResourceUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.ProvinceBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.ProvinceAdapter;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioListViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
/**
 * Author: Thomas.
 * Date: 2019/8/14 10:21
 * Email: 1071931588@qq.com
 * Description:电台列表
 */
@Route(path = AppConstants.Router.Home.F_RADIO_LIST)
public class RadioListFragment extends BaseRefreshMvvmFragment<RadioListViewModel, Radio> implements
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    //本省台
    public static final int LOCAL_PROVINCE = 999;
    //国家台
    public static final int COUNTRY = 998;
    //省市台
    public static final int PROVINCE = 997;
    //网络台
    public static final int INTERNET = 996;
    //排行榜
    public static final int RANK = 995;
    //当地城市台
    public static final int LOCAL_CITY = 993;

    @Autowired(name = KeyCode.Home.TYPE)
    public int mType;
    @Autowired(name = KeyCode.Home.TITLE)
    public String mTitle;
    private RadioAdapter mRadioAdapter;
    private int mProvinceCode;
    private ProvinceAdapter mProvinceAdapter;

    private SmartRefreshLayout refreshLayout;
    //下拉中间视图
    private View llbarCenter;
    private View ivCategoryDown;
    private RecyclerView rvProvince;
    private FrameLayout flMask;
    private TextView tvTitle;

    public RadioListFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_radio_list;
    }

    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = fd(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        ivCategoryDown = llbarCenter.findViewById(R.id.iv_down);
        tvTitle = llbarCenter.findViewById(R.id.tv_title);

        mRadioAdapter = new RadioAdapter(R.layout.home_item_radio);
        mRadioAdapter.bindToRecyclerView(recyclerView);
        mRadioAdapter.setOnItemClickListener(this);
        refreshLayout = fd(R.id.refreshLayout);
        rvProvince = fd(R.id.rv_category);
        flMask = fd(R.id.fl_mask);
        rvProvince.setLayoutManager(new GridLayoutManager(mContext, 5));
        String s = ResourceUtils.readAssets2String("province.json");
        List<ProvinceBean> provinceBeans = new Gson().fromJson(s, new TypeToken<ArrayList<ProvinceBean>>() {
        }.getType());
        mProvinceAdapter = new ProvinceAdapter(R.layout.home_item_rank_category, provinceBeans);
        rvProvince.setHasFixedSize(true);
        mProvinceAdapter.bindToRecyclerView(rvProvince);
        mProvinceAdapter.setOnItemClickListener(this);
        tvTitle.setText(mTitle);
        if (mType == PROVINCE) {
            ivCategoryDown.setVisibility(View.VISIBLE);
            tvTitle.setText(mProvinceAdapter.getItem(0).getProvince_name());
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        llbarCenter.setOnClickListener(this);
        flMask.setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout, mRadioAdapter);
    }

    @Override
    public void initData() {
        mProvinceCode = mProvinceAdapter.getItem(0).getProvince_code();
        mViewModel.setProvinceCode(mProvinceCode);
        mViewModel.setType(mType);
        mViewModel.init();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mRadioAdapter) {
            RadioUtil.getInstance(mContext).playLiveRadioForSDK(mRadioAdapter.getItem(position));
            navigateTo(AppConstants.Router.Home.F_PLAY_RADIIO);
        } else if (adapter == mProvinceAdapter) {
            switchProvince();
            if (mProvinceCode != mProvinceAdapter.getItem(position).getProvince_code()) {
                mProvinceCode = mProvinceAdapter.getItem(position).getProvince_code();
                tvTitle.setText(mProvinceAdapter.getItem(position).getProvince_name());
                mViewModel.setProvinceCode(mProvinceCode);
                mViewModel.init();
            }
        }
    }


    @Override
    public Class<RadioListViewModel> onBindViewModel() {
        return RadioListViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    protected Integer[] onBindBarRightIcon() {
        if (mType == LOCAL_PROVINCE || mType == COUNTRY || mType == PROVINCE || mType == INTERNET || mType == RANK || mType == LOCAL_CITY) {
            return null;
        }
        return new Integer[]{R.drawable.ic_common_search};
    }

    @Override
    protected View onBindBarCenterCustome() {
        llbarCenter = LayoutInflater.from(mContext).inflate(R.layout.home_layout_rank_bar_center, null);
        return llbarCenter;
    }


    @Override
    protected int onBindBarCenterStyle() {
        return BarStyle.CENTER_CUSTOME;
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitRadiosEvent().observe(this, radios -> mRadioAdapter.setNewData(radios));
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mType == PROVINCE && (v == llbarCenter || id == R.id.fl_mask)) {
            switchProvince();
        }
    }

    private void switchProvince() {
        if (flMask.getVisibility() == View.VISIBLE) {

            flMask.animate().withStartAction(() -> {
                flMask.setAlpha(1);
                flMask.setBackgroundColor(Color.TRANSPARENT);
            }).translationY(-rvProvince.getHeight()).alpha(0).setDuration(200).withEndAction(() -> {
                flMask.setVisibility(View.GONE);
            });

            ivCategoryDown.animate().rotationBy(180).setDuration(200);
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.SHOW_GP));
        } else {
            flMask.setTranslationY(-rvProvince.getHeight() == 0 ? -400 : -rvProvince.getHeight());
            flMask.animate().withStartAction(() -> {
                flMask.setAlpha(0);
                flMask.setVisibility(View.VISIBLE);
            }).translationY(0).alpha(1).setDuration(200).withEndAction(() -> flMask.setBackgroundColor(0x99000000));

            ivCategoryDown.animate().rotationBy(180).setDuration(200);
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.HIDE_GP));
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        if (flMask.getVisibility() == View.VISIBLE) {
            switchProvince();
        } else {
            pop();
        }
        return true;
    }
}
