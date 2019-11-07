package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.databinding.CommonLayoutListBinding;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.SearchTrackAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchTrackViewModel;
import com.kingja.loadsir.callback.Callback;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/13 15:12
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索声音
 */
public class SearchTrackFragment extends BaseRefreshMvvmFragment<CommonLayoutListBinding,SearchTrackViewModel, Track> implements
        BaseQuickAdapter.OnItemClickListener {


    private SearchTrackAdapter mSearchTrackAdapter;

    public SearchTrackFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackground(null);

    }

    @Override
    protected void initView() {
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.recyclerview.setHasFixedSize(true);
        mSearchTrackAdapter = new SearchTrackAdapter(R.layout.home_item_seach_track);
        mSearchTrackAdapter.bindToRecyclerView(mBinding.recyclerview);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSearchTrackAdapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout,mSearchTrackAdapter);
    }

    @Override
    public void initData() {
        String keyword = getArguments().getString(KeyCode.Home.KEYWORD);
        mViewModel.setKeyword(keyword);
        mViewModel.init();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        mViewModel.play(String.valueOf(mSearchTrackAdapter.getItem(position).getAlbum().getAlbumId())
                , mSearchTrackAdapter.getItem(position));

    }

    @Override
    public Class<SearchTrackViewModel> onBindViewModel() {
        return SearchTrackViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitTracksEvent().observe(this, tracks -> mSearchTrackAdapter.setNewData(tracks));
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }
    @Override
    protected boolean enableSwipeBack() {
        return false;
    }
     @Override
     public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
