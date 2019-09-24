package com.gykj.zhumulangma.listen.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.FavoriteAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.FavoriteViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Author: Thomas.
 * Date: 2019/8/16 8:45
 * Email: 1071931588@qq.com
 * Description:
 */
@Route(path = AppConstants.Router.Listen.F_FAVORITE)
public class FavoriteFragment extends BaseMvvmFragment<FavoriteViewModel> implements OnRefreshLoadMoreListener,
        BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private FavoriteAdapter mFavoriteAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView = fd(R.id.rv);
        mRefreshLayout = fd(R.id.refreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mFavoriteAdapter = new FavoriteAdapter(R.layout.listen_item_favorite);
        mFavoriteAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    public void initListener() {
        super.initListener();
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
        mFavoriteAdapter.setOnItemChildClickListener(this);
        mFavoriteAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getFavorites();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getFavoritesSingleLiveEvent().observe(this, favoriteBeans -> {

            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                if (CollectionUtils.isEmpty(favoriteBeans)) {
                    showNoDataView(true);
                }
                mFavoriteAdapter.setNewData(favoriteBeans);
                mRefreshLayout.finishRefresh();
            } else {
                if (null == favoriteBeans || (mFavoriteAdapter.getData().size() == 0 && favoriteBeans.size() == 0)) {
                    showNoDataView(true);
                    mRefreshLayout.finishLoadMore();
                    return;
                }
                if (favoriteBeans.size() > 0) {
                    mFavoriteAdapter.addData(favoriteBeans);
                    mRefreshLayout.finishLoadMore();
                } else {
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });

    }

    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"我喜欢的声音"};
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getFavorites();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.refresh();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        mViewModel.unlike(mFavoriteAdapter.getItem(position).getTrack());
        mFavoriteAdapter.remove(position);
        if(mFavoriteAdapter.getData().size()==0){
            showNoDataView(true);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Track track = mFavoriteAdapter.getItem(position).getTrack();
        mViewModel.play(track.getAlbum().getAlbumId(),track.getDataId());
    }

    @Override
    public Class<FavoriteViewModel> onBindViewModel() {
        return FavoriteViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

}
