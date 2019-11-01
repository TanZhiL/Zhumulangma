package com.gykj.zhumulangma.common.mvvm.view;

import android.arch.lifecycle.Observer;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:自动处理刷新Fragment基类
 */
public abstract class BaseRefreshMvvmFragment<DB extends ViewDataBinding,VM extends BaseRefreshViewModel, T>
        extends BaseMvvmFragment<DB,VM>
        implements OnRefreshLoadMoreListener {

    private WrapRefresh mWrapRefresh;

    @Override
    public void initListener() {
        super.initListener();
            mWrapRefresh = onBindWrapRefresh();
            mWrapRefresh.refreshLayout.setOnRefreshLoadMoreListener(this);
    }


    protected abstract @NonNull
    WrapRefresh onBindWrapRefresh();

    @Override
    protected void initBaseViewObservable() {
        super.initBaseViewObservable();
        mViewModel.getFinishRefreshEvent().observe(this, (Observer<List<T>>) list -> {
            if (list == null) {
                mWrapRefresh.refreshLayout.finishRefresh(false);
                return;
            }
            if (list.size() == 0) {
                mWrapRefresh.refreshLayout.finishRefresh(true);
                return;
            }
            mWrapRefresh.refreshLayout.finishRefresh(true);
            onRefreshSucc(list);
        });
        mViewModel.getFinishLoadmoreEvent().observe(this, (Observer<List<T>>) list -> {
            if (list == null) {
                mWrapRefresh.refreshLayout.finishLoadMore(false);
                return;
            }
            if (list.size() == 0) {
                mWrapRefresh.refreshLayout.finishLoadMoreWithNoMoreData();
                return;
            }
            mWrapRefresh.refreshLayout.finishLoadMore(true);
            onLoadMoreSucc(list);
        });
    }

    protected void onRefreshSucc(List<T> list) {
        if (mWrapRefresh.quickAdapter != null) {
            mWrapRefresh.quickAdapter.setNewData(list);
        }
    }

    protected void onLoadMoreSucc(List<T> list) {
        if (mWrapRefresh.quickAdapter != null) {
            mWrapRefresh.quickAdapter.addData(list);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.onViewLoadmore();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.onViewRefresh();
    }

    protected class WrapRefresh {
        SmartRefreshLayout refreshLayout;
        BaseQuickAdapter<T, BaseViewHolder> quickAdapter;

        public WrapRefresh(@NonNull SmartRefreshLayout refreshLayout, BaseQuickAdapter<T, BaseViewHolder> quickAdapter) {
            this.refreshLayout = refreshLayout;
            this.quickAdapter = quickAdapter;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != mWrapRefresh)
        mWrapRefresh.refreshLayout.setOnRefreshLoadMoreListener(null);
    }
}
