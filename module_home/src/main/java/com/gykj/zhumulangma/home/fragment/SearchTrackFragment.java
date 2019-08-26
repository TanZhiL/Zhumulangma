package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.HotStoryAdapter;
import com.gykj.zhumulangma.home.adapter.SearchTrackAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchResultViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;


public class SearchTrackFragment extends BaseMvvmFragment<SearchResultViewModel> implements OnLoadMoreListener,
       BaseQuickAdapter.OnItemClickListener {

    RecyclerView rv;
    SmartRefreshLayout refreshLayout;
    SearchTrackAdapter mAdapter;
    private String keyword;
    public SearchTrackFragment() {

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
        mAdapter=new SearchTrackAdapter(R.layout.home_item_seach_track);
        mAdapter.bindToRecyclerView(rv);
        refreshLayout=view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);
    }

    @Override
    public void initListener() {
        super.initListener();
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        keyword=getArguments().getString(KeyCode.Home.KEYWORD);
       mViewModel.searchTracks(keyword);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.searchTracks(keyword);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        mViewModel.play(String.valueOf(mAdapter.getData().get(position).getAlbum().getAlbumId()),mAdapter.getData().get(position));

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
        mViewModel.getTrackSingleLiveEvent().observe(this, albums -> {
            if(null==albums||(mAdapter.getData().size()==0&&albums.size()==0)){
                showNoDataView(true);
                return;
            }
            if (albums.size() > 0) {
                mAdapter.addData(albums);
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
