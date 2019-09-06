package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioViewModel;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;


public class RadioFragment extends BaseMvvmFragment<RadioViewModel>{

    private RecyclerView rvLocal;
    private RadioAdapter mLocalAdapter;
    private RecyclerView rvTop;
    private RadioAdapter mTopAdapter;

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
        initLocal();
        initTop();
    }

    @Override
    public void initListener() {
        super.initListener();
        mLocalAdapter.setOnItemClickListener((adapter, view, position) -> {
            RadioUtil.getInstance(mContext).playLiveRadioForSDK(mLocalAdapter.getData().get(position));
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_PLAY_RADIIO).navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_PLAY_RADIIO, (ISupportFragment) navigation)));
        });
        mTopAdapter.setOnItemClickListener((adapter, view, position) -> {
            RadioUtil.getInstance(mContext).playLiveRadioForSDK(mTopAdapter.getData().get(position));
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_PLAY_RADIIO).navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_PLAY_RADIIO, (ISupportFragment) navigation)));
        });
    }

    @Override
    public void initData() {
        mViewModel.getLocalList();
        mViewModel.getTopList();
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
        mViewModel.getLocalSingleLiveEvent().observe(this, radios -> mLocalAdapter.setNewData(radios));
        mViewModel.getTopSingleLiveEvent().observe(this, radios -> mTopAdapter.setNewData(radios));
    }


}
