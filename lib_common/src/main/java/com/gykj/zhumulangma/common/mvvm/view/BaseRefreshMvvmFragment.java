package com.gykj.zhumulangma.common.mvvm.view;

import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Author: Thomas.
 * Date: 2019/8/14 13:41
 * Email: 1071931588@qq.com
 * Description:
 */
public abstract class BaseRefreshMvvmFragment<VM extends BaseRefreshViewModel, T> extends BaseMvvmFragment<VM>
        implements OnRefreshLoadMoreListener {

    private SmartRefreshLayout mRefreshLayout;


    @Override
    public void initListener() {
        super.initListener();
        mRefreshLayout=getRefreshLayout();
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    protected abstract SmartRefreshLayout getRefreshLayout();

    @Override
    protected void initBaseViewObservable() {
      super.initBaseViewObservable();
        mViewModel.getFinishRefreshEvent().observe(this, (Observer<List<T>>) list -> {
            if (list == null) {
                mRefreshLayout.finishRefresh(false);
                return;
            }
            if (list.size() == 0) {
                mRefreshLayout.finishRefresh(true);
                return;
            }
            mRefreshLayout.finishRefresh(true);
            onRefreshSucc(list);
        });
        mViewModel.getFinishLoadmoreEvent().observe(this, (Observer<List<T>>) list -> {
            if (list == null) {
                mRefreshLayout.finishLoadMore(false);
                return;
            }
            if (list.size() == 0) {
                mRefreshLayout.finishLoadMoreWithNoMoreData();
                return;
            }
            mRefreshLayout.finishLoadMore(true);
            onLoadMoreSucc(list);
        });
    }

    protected void onLoadMoreSucc(List<T> list) {}

    protected void onRefreshSucc(List<T> list) {}

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.onViewLoadmore();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.onViewRefresh();
    }
}
