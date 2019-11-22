package com.gykj.zhumulangma.home.fragment;


import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ResourceUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.ProvinceBean;
import com.gykj.zhumulangma.common.databinding.CommonLayoutListBinding;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.databinding.HomeLayoutRankBarCenterBinding;
import com.gykj.zhumulangma.home.dialog.RadioProvincePopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioListViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.kingja.loadsir.callback.Callback;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 10:21
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:电台列表
 */
@Route(path = Constants.Router.Home.F_RADIO_LIST)
public class RadioListFragment extends BaseRefreshMvvmFragment<CommonLayoutListBinding, RadioListViewModel, Radio> implements
        BaseQuickAdapter.OnItemClickListener, RadioProvincePopup.onSelectedListener, RadioProvincePopup.onPopupDismissingListener {
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

    private HomeLayoutRankBarCenterBinding mBarCenterBind;


    private List<ProvinceBean> mProvinceBeans;
    private RadioProvincePopup mProvincePopup;

    public RadioListFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_list;
    }

    @Override
    protected void initView() {
        String s = ResourceUtils.readAssets2String("province.json");
        mProvinceBeans = new Gson().fromJson(s, new TypeToken<ArrayList<ProvinceBean>>() {
        }.getType());

        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.recyclerview.setHasFixedSize(true);
        mRadioAdapter = new RadioAdapter(R.layout.home_item_radio_line);
        mRadioAdapter.bindToRecyclerView(mBinding.recyclerview);
        mRadioAdapter.setOnItemClickListener(this);

        mBarCenterBind.tvTitle.setText(mTitle);
        if (mType == PROVINCE) {
            mBarCenterBind.ivDown.setVisibility(View.VISIBLE);
            mBarCenterBind.tvTitle.setText(mProvinceBeans.get(0).getProvince_name());
        }

        mProvincePopup = new RadioProvincePopup(mActivity, this);
        mProvincePopup.setDismissingListener(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        if (mType == PROVINCE) {
            RxView.clicks(mBarCenterBind.getRoot())
                    .doOnSubscribe(this)
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> switchProvince());
        }
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mRadioAdapter);
    }

    @Override
    public void initData() {
        mProvinceCode = mProvinceBeans.get(0).getProvince_code();
        mViewModel.setProvinceCode(mProvinceCode);
        mViewModel.setType(mType);
        mViewModel.init();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mRadioAdapter) {
            mViewModel.playRadio(mRadioAdapter.getItem(position));
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
    public Integer[] onBindBarRightIcon() {
        if (mType == LOCAL_PROVINCE || mType == COUNTRY || mType == PROVINCE || mType == INTERNET || mType == RANK || mType == LOCAL_CITY) {
            return null;
        }
        return new Integer[]{R.drawable.ic_common_search};
    }

    @Override
    public View onBindBarCenterCustome() {
        mBarCenterBind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.home_layout_rank_bar_center, null, false);
        return mBarCenterBind.getRoot();
    }


    @Override
    public SimpleBarStyle onBindBarCenterStyle() {
        return SimpleBarStyle.CENTER_CUSTOME;
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitRadiosEvent().observe(this, radios -> mRadioAdapter.setNewData(radios));
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    private void switchProvince() {

        if (mProvincePopup.isShow()) {
            mProvincePopup.dismiss();
        } else {
            mBarCenterBind.ivDown.animate().rotation(180).setDuration(200);
            new XPopup.Builder(mActivity).atView(mSimpleTitleBar).popupPosition(PopupPosition.Bottom).asCustom(mProvincePopup).show();
        }
    }


    @Override
    public void onSelected(int province_code, String province_name) {
        if (mProvinceCode != province_code) {
            mProvinceCode = province_code;
            mBarCenterBind.tvTitle.setText(province_name);
            mViewModel.setProvinceCode(mProvinceCode);
            mViewModel.init();
        }
    }

    @Override
    public void onDismissing() {
        mBarCenterBind.ivDown.animate().rotation(0).setDuration(200);
    }

    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
