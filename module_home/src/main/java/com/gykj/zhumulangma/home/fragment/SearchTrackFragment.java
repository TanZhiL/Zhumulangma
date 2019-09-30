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
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.SearchTrackAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchTrackViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Author: Thomas.
 * Date: 2019/8/13 15:12
 * Email: 1071931588@qq.com
 * Description:搜索声音
 */
public class SearchTrackFragment extends BaseRefreshMvvmFragment<SearchTrackViewModel, Track> implements
        BaseQuickAdapter.OnItemClickListener {


    private SmartRefreshLayout refreshLayout;
    private SearchTrackAdapter mSearchTrackAdapter;

    public SearchTrackFragment() {

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
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        mSearchTrackAdapter = new SearchTrackAdapter(R.layout.home_item_seach_track);
        mSearchTrackAdapter.bindToRecyclerView(recyclerView);
        refreshLayout = view.findViewById(R.id.refreshLayout);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSearchTrackAdapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout,mSearchTrackAdapter);
    }

    @Override
    public void initData() {
        String keyword = getArguments().getString(KeyCode.Home.KEYWORD);
        mViewModel.setKeyword(keyword);
        mViewModel.init();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        mViewModel.play(String.valueOf(mSearchTrackAdapter.getItem(position).getAlbum().getAlbumId())
                , mSearchTrackAdapter.getItem(position));

    }

    @Override
    public Class<SearchTrackViewModel> onBindViewModel() {
        return SearchTrackViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitTracksEvent().observe(this, tracks -> mSearchTrackAdapter.setNewData(tracks));
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }
}
