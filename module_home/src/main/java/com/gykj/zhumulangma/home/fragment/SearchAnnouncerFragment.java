package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AnnouncerAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchResultViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;


public class SearchAnnouncerFragment extends BaseMvvmFragment<SearchResultViewModel> implements OnLoadMoreListener,
       BaseQuickAdapter.OnItemClickListener {


    RecyclerView rv;
    SmartRefreshLayout refreshLayout;
    AnnouncerAdapter mAdapter;

    private String keyword;
    public SearchAnnouncerFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }
    @Override
    protected void initView(View view) {
        rv=view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setHasFixedSize(true);
        mAdapter=new AnnouncerAdapter(R.layout.home_item_announcer);
        mAdapter.bindToRecyclerView(rv);
        mAdapter.setOnItemClickListener(this);
        refreshLayout=view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(this);
    }




    @Override
    public void initData() {
        keyword=getArguments().getString(KeyCode.Home.KEYWORD);
       mViewModel.searchAnnouncer(keyword);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.searchAnnouncer(keyword);
    }



    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
    @Override
    public Class<SearchResultViewModel> onBindViewModel() {
        return SearchResultViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getAnnouncerSingleLiveEvent().observe(this, announcers -> {
            if(null==announcers||(mAdapter.getData().size()==0&&announcers.size()==0)){
                showNoDataView(true);
                return;
            }
            if (announcers.size() > 0) {
                mAdapter.addData(announcers);
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }
}
