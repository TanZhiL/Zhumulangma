package com.gykj.zhumulangma.listen.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.HistoryAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.HistoryViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

/**
 * Author: Thomas.
 * Date: 2019/8/16 8:44
 * Email: 1071931588@qq.com
 * Description:
 */
@Route(path = AppConstants.Router.Listen.F_HISTORY)
public class HistoryFragment extends BaseMvvmFragment<HistoryViewModel> implements OnLoadMoreListener,
        BaseQuickAdapter.OnItemClickListener {
    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mHistoryAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void initView(View view) {
        mRefreshLayout = fd(R.id.refreshLayout);
        mRefreshLayout.setEnableRefresh(false);
        mRecyclerView = fd(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mHistoryAdapter = new HistoryAdapter(R.layout.listen_item_history);
        mHistoryAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    public void initListener() {
        super.initListener();
        mRefreshLayout.setOnLoadMoreListener(this);
        mHistoryAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getHistory();
    }

    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"播放历史"};
    }


    @Override
    protected Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_listen_delete};
    }

    @Override
    public Class<HistoryViewModel> onBindViewModel() {
        return HistoryViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getHistorySingleLiveEvent().observe(this, playHistoryBeans -> {
            if (null == playHistoryBeans || (mHistoryAdapter.getData().size() == 0 && playHistoryBeans.size() == 0)) {
                showNoDataView(true);
                return;
            }
            if (playHistoryBeans.size() > 0) {
                mHistoryAdapter.addData(playHistoryBeans);
                mRefreshLayout.finishLoadMore();
            } else {
                mRefreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getHistory();

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        PlayHistoryBean playHistoryBean = mHistoryAdapter.getData().get(position);
        mViewModel.play(String.valueOf(playHistoryBean.getAlbumId()), playHistoryBean.getTrack());
    }

    @Override
    protected void onRight1Click(View v) {
        super.onRight1Click(v);
        new AlertDialog.Builder(mContext)
                .setMessage("确定要清空播放历史吗?")
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    mViewModel.clear();
                    mHistoryAdapter.getData().clear();
                    showNoDataView(true);
                }).show();
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }
}
