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
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.ProvinceBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.ProvinceAdapter;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.adapter.RadioHistoryAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioListViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Home.F_RADIO_LIST)
public class RadioListFragment extends BaseMvvmFragment<RadioListViewModel> implements BaseQuickAdapter.OnItemClickListener,
        OnLoadMoreListener, View.OnClickListener {

    public static final int LOCAL = 999;
    public static final int COUNTRY = 998;
    public static final int PROVINCE = 997;
    public static final int INTERNET = 996;
    public static final int RANK = 995;
    public static final int HISTORY = 994;
    public static final int LOCAL_CITY = 993;

    @Autowired(name = KeyCode.Home.TYPE)
    public int type;
    @Autowired(name = KeyCode.Home.TITLE)
    public String title;
    private RecyclerView rv;
    private SmartRefreshLayout refreshLayout;
    private RadioAdapter mAdapter;
    private RadioHistoryAdapter mHistoryAdapter;
    //下拉中间视图
    private View llbarCenter;
    private View ivCategoryDown;
    private TextView tvTitle;

    private int provinceCode;
    private RecyclerView rvCategory;
    private FrameLayout flMask;
    private ProvinceAdapter mProvinceAdapter;

    public RadioListFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_radio_list;
    }

    @Override
    protected void initView(View view) {
        rv = fd(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setHasFixedSize(true);

        ivCategoryDown = llbarCenter.findViewById(R.id.iv_down);
        tvTitle = llbarCenter.findViewById(R.id.tv_title);

        if(type==HISTORY){
            mHistoryAdapter=new RadioHistoryAdapter(R.layout.home_item_radio);
            mHistoryAdapter.bindToRecyclerView(rv);
            mHistoryAdapter.setOnItemClickListener(this);
        }else {
            mAdapter = new RadioAdapter(R.layout.home_item_radio);
            mAdapter.bindToRecyclerView(rv);
            mAdapter.setOnItemClickListener(this);
        }
        refreshLayout = view.findViewById(R.id.refreshLayout);

        rvCategory = fd(R.id.rv_category);
        flMask = fd(R.id.fl_mask);
        rvCategory.setLayoutManager(new GridLayoutManager(mContext, 5));
        String s = ResourceUtils.readAssets2String("province.json");
        List<ProvinceBean> provinceBeans = new Gson().fromJson(s, new TypeToken<ArrayList<ProvinceBean>>() {
        }.getType());
        mProvinceAdapter = new ProvinceAdapter(R.layout.home_item_rank_category, provinceBeans);
        rvCategory.setHasFixedSize(true);
        mProvinceAdapter.bindToRecyclerView(rvCategory);
        mProvinceAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        refreshLayout.setOnLoadMoreListener(this);

        llbarCenter.setOnClickListener(this);
        flMask.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tvTitle.setText(title);
        switch (type) {
            case LOCAL:
                mViewModel.getRadioList(RadioListViewModel.PROVINCE,
                        SPUtils.getInstance().getString(AppConstants.SP.PROVINCE_CODE,AppConstants.Defualt.PROVINCE_CODE));
                break;
            case HISTORY:
                mViewModel._getHistory();
                break;
            case COUNTRY:
                mViewModel.getRadioList(RadioListViewModel.COUNTRY, null);
                break;
            case PROVINCE:
                ivCategoryDown.setVisibility(View.VISIBLE);
                tvTitle.setText(mProvinceAdapter.getData().get(0).getProvince_name());
                provinceCode = mProvinceAdapter.getData().get(0).getProvince_code();
                mViewModel.getRadioList(RadioListViewModel.PROVINCE, String.valueOf(provinceCode));
                break;
            case INTERNET:
                mViewModel.getRadioList(RadioListViewModel.INTERNET, null);
                break;
            case RANK:
                mViewModel._getRankRadios();
                break;
            case LOCAL_CITY:
                mViewModel.getLocalCity(SPUtils.getInstance().getString(AppConstants.SP.CITY_CODE,AppConstants.Defualt.CITY_CODE));
                break;
            default:
                mViewModel._getRadiosByCategory(String.valueOf(type));
                break;
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAdapter) {
            RadioUtil.getInstance(mContext).playLiveRadioForSDK(mAdapter.getData().get(position));
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_PLAY_RADIIO).navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_PLAY_RADIIO, (ISupportFragment) navigation)));
        } else if (adapter == mProvinceAdapter) {
            switchCategory();
            if (provinceCode != mProvinceAdapter.getData().get(position).getProvince_code()) {
                provinceCode = mProvinceAdapter.getData().get(position).getProvince_code();
                tvTitle.setText(mProvinceAdapter.getData().get(position).getProvince_name());
                mAdapter.setNewData(null);
                mViewModel.getRadioList(RadioListViewModel.PROVINCE, String.valueOf(provinceCode));
            }
        }else if(adapter==mHistoryAdapter){
            mViewModel.play(String.valueOf(mHistoryAdapter.getData().get(position).getSchedule().getRadioId()));
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        switch (type) {
            case LOCAL:
                mViewModel.getRadioList(RadioListViewModel.PROVINCE,
                        SPUtils.getInstance().getString(AppConstants.SP.PROVINCE_CODE,AppConstants.Defualt.PROVINCE_CODE));
                break;
            case HISTORY:
                mViewModel._getHistory();
                break;
            case COUNTRY:
                mViewModel.getRadioList(RadioListViewModel.COUNTRY, null);
                break;
            case PROVINCE:
                ivCategoryDown.setVisibility(View.VISIBLE);
                tvTitle.setText(mProvinceAdapter.getData().get(0).getProvince_name());
                mViewModel.getRadioList(RadioListViewModel.PROVINCE, String.valueOf(provinceCode));
                break;
            case INTERNET:
                mViewModel.getRadioList(RadioListViewModel.INTERNET, null);
                break;
            case RANK:
               refreshLayout.finishLoadMoreWithNoMoreData();
                break;
                case LOCAL_CITY:
                mViewModel.getLocalCity(SPUtils.getInstance().getString(AppConstants.SP.CITY_CODE));
                break;
            default:
                mViewModel._getRadiosByCategory(String.valueOf(type));
                break;
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
        return new Integer[]{R.drawable.ic_common_share};
    }

    @Override
    protected View onBindBarCenterCustome() {
        llbarCenter = LayoutInflater.from(mContext).inflate(R.layout.home_layout_rank_bar_center, null);
        return llbarCenter;
    }

    @Override
    protected int onBindBarRightStyle() {
        return BarStyle.RIGHT_ICON;
    }

    @Override
    protected int onBindBarCenterStyle() {
        return BarStyle.CENTER_CUSTOME;
    }

    @Override
    public void initViewObservable() {
        mViewModel.getRadioSingleLiveEvent().observe(this, radios -> {

            if (null == radios || (mAdapter.getData().size() == 0 && radios.size() == 0)) {
                showNoDataView(true);
                return;
            }
            if (radios.size() > 0) {
                mAdapter.addData(radios);
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
        mViewModel.getHistorySingleLiveEvent().observe(this, historyBeans -> {

            if (null == historyBeans || (mHistoryAdapter.getData().size() == 0 && historyBeans.size() == 0)) {
                showNoDataView(true);
                return;
            }
            if (historyBeans.size() > 0) {
                mHistoryAdapter.addData(historyBeans);
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (type == PROVINCE && (v == llbarCenter || id == R.id.fl_mask)) {
            switchCategory();
        }
    }

    private void switchCategory() {
        if (flMask.getVisibility() == View.VISIBLE) {

            flMask.animate().withStartAction(() -> {
                flMask.setAlpha(1);
                flMask.setBackgroundColor(Color.TRANSPARENT);
            }).translationY(-rvCategory.getHeight()).alpha(0).setDuration(200).withEndAction(() -> {
                flMask.setVisibility(View.GONE);
            });

            ivCategoryDown.animate().rotationBy(180).setDuration(200);
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.SHOW_GP));
        } else {
            flMask.setTranslationY(-rvCategory.getHeight() == 0 ? -400 : -rvCategory.getHeight());
            flMask.animate().withStartAction(() -> {
                flMask.setAlpha(0);
                flMask.setVisibility(View.VISIBLE);
            }).translationY(0).alpha(1).setDuration(200).withEndAction(() -> flMask.setBackgroundColor(0x99000000));

            ivCategoryDown.animate().rotationBy(180).setDuration(200);
            EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.HIDE_GP));
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        if (flMask.getVisibility() == View.VISIBLE) {
            switchCategory();
        } else {
            pop();
        }
        return true;
    }
}
