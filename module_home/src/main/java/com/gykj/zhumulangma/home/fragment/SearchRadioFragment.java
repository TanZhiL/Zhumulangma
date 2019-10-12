package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.ListCallback;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchRadioViewModel;
import com.kingja.loadsir.callback.Callback;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索电台
 */
public class SearchRadioFragment extends BaseRefreshMvvmFragment<SearchRadioViewModel, Radio> implements
       BaseQuickAdapter.OnItemClickListener {


    private SmartRefreshLayout refreshLayout;
   private RadioAdapter mRadioAdapter;

    public SearchRadioFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackground(null);
        setSwipeBackEnable(false);
    }
    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        mRadioAdapter =new RadioAdapter(R.layout.home_item_radio);
        mRadioAdapter.bindToRecyclerView(recyclerView);
        mRadioAdapter.setOnItemClickListener(this);
        refreshLayout=view.findViewById(R.id.refreshLayout);
    }




    @Override
    public void initData() {
        String keyword = getArguments().getString(KeyCode.Home.KEYWORD);
        mViewModel.setKeyword(keyword);
        mViewModel.init();
    }



    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        RadioUtil.getInstance(mActivity).playLiveRadioForSDK(mRadioAdapter.getItem(position));
        navigateTo(AppConstants.Router.Home.F_PLAY_RADIIO);
    }
    @Override
    public Class<SearchRadioViewModel> onBindViewModel() {
        return SearchRadioViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitRadiosEvent().observe(this, radioList -> mRadioAdapter.setNewData(radioList));
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout,mRadioAdapter);
    }

     @Override
    protected Callback getInitCallBack() {
        return new ListCallback();
    }
}
