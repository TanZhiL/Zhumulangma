package com.gykj.zhumulangma.home.fragment;


import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.SearchHistoryAdapter;
import com.gykj.zhumulangma.home.adapter.SearchHotAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentSearchHistoryBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchViewModel;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索下历史页
 */
public class SearchHistoryFragment extends BaseMvvmFragment<HomeFragmentSearchHistoryBinding,SearchViewModel>
        implements View.OnClickListener {

    private SearchHistoryAdapter mHistoryAdapter;
    private SearchHotAdapter mHotAdapter;
    private onSearchListener mSearchListener;

    public SearchHistoryFragment() {
    }

    @Override
    public int onBindLayout() {
        return R.layout.home_fragment_search_history;
    }


    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initView() {
        mBinding.tvClear.setOnClickListener(this);

        mBinding.rvHistory.setLayoutManager(new com.library.flowlayout.FlowLayoutManager());
        mBinding.rvHistory.setHasFixedSize(true);

        mHistoryAdapter = new SearchHistoryAdapter(R.layout.common_item_tag);
        mHistoryAdapter.bindToRecyclerView(mBinding.rvHistory);
        mHistoryAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (mSearchListener != null)
                mSearchListener.onSearch(mHistoryAdapter.getItem(position).getKeyword());
        });

        mBinding.rvHot.setLayoutManager(new com.library.flowlayout.FlowLayoutManager());
        mBinding.rvHot.setHasFixedSize(true);

        mHotAdapter = new SearchHotAdapter(R.layout.common_item_tag);
        mHotAdapter.bindToRecyclerView(mBinding.rvHot);
        mHotAdapter.setOnItemClickListener((adapter, view12, position) -> {
            if (mSearchListener != null)
                mSearchListener.onSearch(mHotAdapter.getItem(position).getSearchword());
        });
    }


    public void refreshHistory() {
        mViewModel.refreshHistory();
    }

    @Override
    public void initData() {
        mViewModel.getHistory();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.tv_clear == id) {
            mViewModel.clearHistory();
            mHistoryAdapter.setNewData(null);
        }
    }

    public void setSearchListener(onSearchListener searchListener) {
        mSearchListener = searchListener;
    }


    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    public interface onSearchListener {

        void onSearch(String keyword);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getHotWordsEvent().observe(this, hotWords -> mHotAdapter.setNewData(hotWords));
        mViewModel.getHistorySingleLiveEvent().observe(this, historyBeanList ->
                mHistoryAdapter.setNewData(historyBeanList));
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
