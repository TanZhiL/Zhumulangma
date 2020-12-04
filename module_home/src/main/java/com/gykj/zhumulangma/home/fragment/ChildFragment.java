package com.gykj.zhumulangma.home.fragment;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gykj.zhumulangma.common.databinding.CommonLayoutRefreshListBinding;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.HomeAdapter;
import com.gykj.zhumulangma.home.bean.HomeItem;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.ChildViewModel;

import java.util.ArrayList;

import static com.gykj.zhumulangma.home.adapter.HomeAdapter.RECYCLEDVIEWPOOL;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:精品
 */
public class ChildFragment extends BaseRefreshFragment<CommonLayoutRefreshListBinding, ChildViewModel, HomeItem> {
    private HomeAdapter mHomeAdapter;

    public ChildFragment() {

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
        mHomeAdapter = new HomeAdapter(new ArrayList<>());
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.recyclerview.setHasFixedSize(true);
        mHomeAdapter.bindToRecyclerView(mBinding.recyclerview);
    }

    @Override
    public void initListener() {
        super.initListener();

    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mHomeAdapter);
    }


    @Override
    public void initData() {
        mViewModel.init();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getNovelItemsEvent().observe(this, novelItems -> {
            mHomeAdapter.setNewData(novelItems);
            //重新恢复可下拉加载更多
            mBinding.refreshLayout.setNoMoreData(false);
        });
    }


    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public Class<ChildViewModel> onBindViewModel() {
        return ChildViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    protected void onRevisible() {
        super.onRevisible();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Home.TAB_REFRESH:
                if (mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
                    mBinding.recyclerview.scrollTo(0, 0);
                    mBinding.refreshLayout.autoRefresh();
                }
                break;
        }
    }

}
