package com.gykj.zhumulangma.home.fragment;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.databinding.CommonLayoutListBinding;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchRadioViewModel;
import com.kingja.loadsir.callback.Callback;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索电台
 */
public class SearchRadioFragment extends BaseRefreshFragment<CommonLayoutListBinding, SearchRadioViewModel, Radio>
        implements BaseQuickAdapter.OnItemClickListener {

    private RadioAdapter mRadioAdapter;

    public SearchRadioFragment() {

    }


    @Override
    public int onBindLayout() {
        return R.layout.common_layout_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackground(null);

    }

    @Override
    public void initView() {
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.recyclerview.setHasFixedSize(true);
        mRadioAdapter = new RadioAdapter(R.layout.home_item_radio_line);
        mRadioAdapter.bindToRecyclerView(mBinding.recyclerview);
        mRadioAdapter.setOnItemClickListener(this);
    }


    @Override
    public void initData() {
        String keyword = getArguments().getString(KeyCode.Home.KEYWORD);
        mViewModel.setKeyword(keyword);
        mViewModel.init();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        mViewModel.playRadio(mRadioAdapter.getItem(position));
    }

    @Override
    public Class<SearchRadioViewModel> onBindViewModel() {
        return SearchRadioViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitRadiosEvent().observe(this, radioList -> mRadioAdapter.setNewData(radioList));
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }
    @Override
    protected boolean enableSwipeBack() {
        return false;
    }
    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mRadioAdapter);
    }

    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
