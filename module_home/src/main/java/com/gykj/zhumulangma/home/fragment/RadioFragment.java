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

import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioViewModel;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import java.util.ArrayList;
import java.util.List;


public class RadioFragment extends BaseMvvmFragment<RadioViewModel> {

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
    protected void initView(View view) {
        setSwipeBackEnable(false);
        initLocal();
        initTop();
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
