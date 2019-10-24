package com.gykj.zhumulangma.listen.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.HistoryAdapter;
import com.gykj.zhumulangma.listen.bean.PlayHistoryItem;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.HistoryViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/16 8:44
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
@Route(path = AppConstants.Router.Listen.F_HISTORY)
public class HistoryFragment extends BaseRefreshMvvmFragment<HistoryViewModel,PlayHistoryItem> implements
        BaseQuickAdapter.OnItemClickListener{

    private SmartRefreshLayout refreshLayout;
    private HistoryAdapter mHistoryAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void initView(View view) {
        refreshLayout = fd(R.id.refreshLayout);
        RecyclerView recyclerView = fd(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        mHistoryAdapter = new HistoryAdapter(null);
        mHistoryAdapter.bindToRecyclerView(recyclerView);
    }

    @Override
    public void initListener() {
        super.initListener();
        mHistoryAdapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout,mHistoryAdapter);
    }

    @Override
    public void initData() {
        mViewModel.init();
    }

    @Override
    public String[] onBindBarTitleText() {
        return new String[]{"播放历史"};
    }


    @Override
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_listen_history_delete};
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
        mViewModel.getInitHistorysEvent().observe(this, historyItems -> mHistoryAdapter.setNewData(historyItems));
    }

    @Override
    protected void onLoadMoreSucc(List<PlayHistoryItem> list) {
        //两页衔接处处理
        if(!CollectionUtils.isEmpty(mHistoryAdapter.getData())&&mViewModel.dateCovert(
                mHistoryAdapter.getItem(mHistoryAdapter.getData().size()-1).data.getDatatime())
                .equals(list.get(0).header)){
            list.remove(0);
        }
        mHistoryAdapter.addData(list);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        PlayHistoryItem playHistoryItem = mHistoryAdapter.getItem(position);
        if(playHistoryItem.itemType!= PlayHistoryItem.HEADER){
            if(playHistoryItem.itemType== PlayHistoryItem.TRACK){
                mViewModel.playRadio(playHistoryItem.data.getGroupId(),
                        playHistoryItem.data.getTrack().getDataId());
            }else {
                mViewModel.playRadio(String.valueOf(playHistoryItem.data.getGroupId()));
            }
        }
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        new AlertDialog.Builder(mActivity)
                .setMessage("确定要清空播放历史吗?")
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    mViewModel.clear();
                    mHistoryAdapter.getData().clear();
                    showEmptyView();
                }).show();
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }
}
