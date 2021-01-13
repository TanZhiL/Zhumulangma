package com.gykj.zhumulangma.home.fragment;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gykj.zhumulangma.common.databinding.CommonLayoutRefreshListBinding;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.HotSkeleton;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.ColumnAdapter;
import com.gykj.zhumulangma.home.bean.HomeItem;
import com.gykj.zhumulangma.home.bean.TabBean;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.ColumnViewModel;
import com.kingja.loadsir.callback.Callback;

import java.util.ArrayList;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:热门
 */

public class ColumnFragment extends BaseRefreshFragment<CommonLayoutRefreshListBinding, ColumnViewModel, HomeItem>{
    private ColumnAdapter mColumnAdapter;
    private TabBean mTabBean;

    public ColumnFragment() {
    }


    @Override
    public int onBindLayout() {
        return R.layout.common_layout_refresh_list;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initView() {
        mTabBean= getArguments().getParcelable(KeyCode.Home.TAB);
        mColumnAdapter = new ColumnAdapter(mTabBean,new ArrayList<>(),mActivity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setRecycleChildrenOnDetach(true);
        mBinding.recyclerview.setLayoutManager(linearLayoutManager);
        mBinding.recyclerview.setHasFixedSize(true);
//        mBinding.recyclerview.setRecycledViewPool(RECYCLEDVIEWPOOL);
        mColumnAdapter.bindToRecyclerView(mBinding.recyclerview);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mColumnAdapter);
    }


    @Override
    public void initData() {
        mViewModel.initArguments(mTabBean);
        mViewModel.init();
    }

    @Override
    protected void onReload(View v) {
        showInitView();
        mViewModel.onViewRefresh();
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }


    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }


    @Override
    public void initViewObservable() {
        mViewModel.getHomeItemsEvent().observe(this, novelItems -> {
            mColumnAdapter.setNewData(novelItems);
            //重新恢复可下拉加载更多
            mBinding.refreshLayout.setNoMoreData(false);
        });
    }
    @Override
    public Callback getInitStatus() {
        mTabBean= getArguments().getParcelable(KeyCode.Home.TAB);
        if(mTabBean.isCat()){
            return super.getInitStatus();
        }
        return new HotSkeleton();
    }
    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Home.TAB_REFRESH:
                   /* if (mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
                        mBinding.recyclerview.scrollTo(0, 0);
                        mBinding.refreshLayout.autoRefresh();
                    }*/
                break;
        }
    }
}
