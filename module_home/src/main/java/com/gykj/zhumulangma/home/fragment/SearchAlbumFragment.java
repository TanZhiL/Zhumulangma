package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.util.DisplayUtil;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.HotStoryAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchResultViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;


public class SearchAlbumFragment extends BaseMvvmFragment<SearchResultViewModel> implements OnLoadMoreListener,
        BaseQuickAdapter.OnItemClickListener {


    private RecyclerView rv;
    private SmartRefreshLayout refreshLayout;
    private HotStoryAdapter mAdapter;


    private String keyword;

    public SearchAlbumFragment() {

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
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setHasFixedSize(true);
        mAdapter = new HotStoryAdapter(R.layout.home_item_hot_story);
        mAdapter.bindToRecyclerView(rv);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);

    }

    @Override
    public void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    @Override
    public void initData() {
        keyword = getArguments().getString(KeyCode.Home.KEYWORD);
        mViewModel.searchAlbums(keyword);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.searchAlbums(keyword);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mAdapter.getData().get(position).getId())
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,navigation));
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
        mViewModel.getAlbumSingleLiveEvent().observe(this, albums -> {
            if (null == albums || (mAdapter.getData().size() == 0 && albums.size() == 0)) {
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
