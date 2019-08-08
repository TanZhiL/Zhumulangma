package com.gykj.zhumulangma.common.mvvm;

import android.arch.lifecycle.Observer;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gykj.zhumulangma.common.mvvm.view.IBaseRefreshView;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

/**
 * Description: <下拉刷新、上拉加载更多的Activity><br>
 * Author:      mxdl<br>
 * Date:        2019/07/02<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public abstract class BaseMvvmRefreshActivity<T,VM extends BaseRefreshViewModel> extends BaseMvvmActivity<VM> implements IBaseRefreshView<T> {
    protected SmartRefreshLayout mRefreshLayout;

    @Override
    protected void initCommonView() {
        super.initCommonView();
        initRefreshView();
    }

    @Override
    protected void initBaseViewObservable() {
        super.initBaseViewObservable();
        initBaseViewRefreshObservable();
    }

    private void initBaseViewRefreshObservable() {
        mViewModel.getUCRefresh().getAutoRefresLiveEvent().observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                autoLoadData();
            }
        });
        mViewModel.getUCRefresh().getRefresLiveEvent().observe(this, new Observer<List<T>>() {

            @Override
            public void onChanged(@Nullable List<T> list) {
                refreshData(list);
            }
        });
        mViewModel.getUCRefresh().getLoadMoreLiveEvent().observe(this, new Observer<List<T>>() {

            @Override
            public void onChanged(@Nullable List<T> list) {
                loadMoreData(list);
            }
        });
        mViewModel.getUCRefresh().getStopRefresLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                stopRefresh(success);
            }
        });

        mViewModel.getUCRefresh().getStopLoadMoreLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                stopLoadMore(success);
            }
        });
        mViewModel.getUCRefresh().getNoMoreDataLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean noMoreData) {
                setNoMoreData(noMoreData);
            }
        });
    }

    public void initRefreshView() {
        mRefreshLayout = findViewById(onBindRreshLayout());
        // 下拉刷新
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                onRefreshEvent();
            }
        });
      mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
          @Override
          public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
              onLoadMoreEvent();
          }
      });
    }

    protected abstract @IdRes int onBindRreshLayout();

    @Override
    public void enableRefresh(boolean b) {
        mRefreshLayout.setEnableRefresh(b);
    }

    @Override
    public void enableLoadMore(boolean b) {
        mRefreshLayout.setEnableLoadMore(b);
    }

    @Override
    public void stopRefresh(boolean success) {
        mRefreshLayout.finishLoadMore(success);
    }

    @Override
    public void stopLoadMore(boolean success) {
        mRefreshLayout.finishLoadMore(success);
    }

    @Override
    public void setNoMoreData(boolean noMoreData) {
        mRefreshLayout.setNoMoreData(noMoreData);
    }

    @Override
    public void autoLoadData() {
        if(mRefreshLayout!=null){
            mRefreshLayout.autoRefresh();
        }
    }

}
