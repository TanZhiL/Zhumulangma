package com.gykj.zhumulangma.listen.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.SubscribeAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.SubscribeViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;


public class SubscribeFragment extends BaseMvvmFragment<SubscribeViewModel>
        implements OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private SubscribeAdapter mSubscribeAdapter;
    private View vFooter;
    public SubscribeFragment() {
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
        mRecyclerView = fd(R.id.rv);
        mRefreshLayout = fd(R.id.refreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mSubscribeAdapter = new SubscribeAdapter(R.layout.listen_item_subscribe);
        mSubscribeAdapter.bindToRecyclerView(mRecyclerView);
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.listen_layout_subscribe_footer, null);
        vFooter=inflate.findViewById(R.id.cl_content);
        mSubscribeAdapter.addFooterView(inflate);
    }

    @Override
    public void initListener() {
        super.initListener();
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
        mSubscribeAdapter.setOnItemChildClickListener(this);
        mSubscribeAdapter.setOnItemClickListener(this);
        vFooter.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getSubscribes();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getSubscribesSingleLiveEvent().observe(this, subscribeBeans -> {

            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                if (CollectionUtils.isEmpty(subscribeBeans)) {
                    showEmptyView(true);
                }
                mSubscribeAdapter.setNewData(subscribeBeans);
                mRefreshLayout.finishRefresh();
            } else {
                if (null == subscribeBeans || (mSubscribeAdapter.getData().size() == 0 && subscribeBeans.size() == 0)) {
                    showEmptyView(true);
                    mRefreshLayout.finishLoadMore();
                    return;
                }
                if (subscribeBeans.size() > 0) {
                    mSubscribeAdapter.addData(subscribeBeans);
                    mRefreshLayout.finishLoadMore();
                } else {
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getSubscribes();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.refresh();
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (id == R.id.iv_play) {
            mViewModel.play(String.valueOf(mSubscribeAdapter.getItem(position).getAlbumId()));
        }
    }

    @Override
    public Class<SubscribeViewModel> onBindViewModel() {
        return SubscribeViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mSubscribeAdapter.getItem(position).getAlbum().getId())
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
    }

    @Override
    public void onClick(View v) {
        if(v==vFooter){
            navigateTo(AppConstants.Router.Home.F_RANK);
        }
    }
}
