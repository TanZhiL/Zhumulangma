package com.gykj.zhumulangma.listen.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.databinding.CommonLayoutListBinding;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.RecommendAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.SubscribeViewModel;
import com.kingja.loadsir.callback.Callback;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/20 14:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:推荐订阅
 */
public class RecommendFragment extends BaseMvvmFragment<CommonLayoutListBinding, SubscribeViewModel> implements
        BaseQuickAdapter.OnItemChildClickListener, OnRefreshLoadMoreListener {

    private RecommendAdapter mRecommendAdapter;

    @Override
    public int onBindLayout() {
        return R.layout.common_layout_list;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initView() {
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.recyclerview.setHasFixedSize(true);
        mRecommendAdapter = new RecommendAdapter(R.layout.listen_item_recommend);
        mRecommendAdapter.bindToRecyclerView(mBinding.recyclerview);
    }

    @Override
    public void initListener() {
        super.initListener();
        mRecommendAdapter.setOnItemChildClickListener(this);
        mRecommendAdapter.setOnItemClickListener((adapter, view, position) ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mRecommendAdapter.getItem(position).getId())));
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getGuessLikeAlbum();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getLikesEvent().observe(this, albums -> {
            if (CollectionUtils.isEmpty(albums)) {
                showEmptyView();
            } else {
                mRecommendAdapter.setNewData(albums);
            }
        });

        mViewModel.getSubscribeEvent().observe(this, album -> {
            List<Album> data = mRecommendAdapter.getData();
            int index = data.indexOf(album);
            if (index > -1) {
                try {
                    mRecommendAdapter.getViewByPosition(index, R.id.ll_subscribe).setVisibility(View.GONE);
                    mRecommendAdapter.getViewByPosition(index, R.id.ll_unsubscribe).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mViewModel.getUnSubscribeEvent().observe(this, album -> {
            List<Album> data = mRecommendAdapter.getData();
            int index = data.indexOf(album);
            if (index > -1) {
                try {
                    mRecommendAdapter.getViewByPosition(index, R.id.ll_subscribe).setVisibility(View.VISIBLE);
                    mRecommendAdapter.getViewByPosition(index, R.id.ll_unsubscribe).setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Class<SubscribeViewModel> onBindViewModel() {
        return SubscribeViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }


    @Override
    public boolean enableSimplebar() {
        return false;
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (id == R.id.ll_subscribe) {
            mViewModel.subscribe(mRecommendAdapter.getItem(position));
        } else {
            mViewModel.unsubscribe(mRecommendAdapter.getItem(position));
        }
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(true);
    }

    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Listen.TAB_REFRESH:
                if (mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
                    mBinding.recyclerview.scrollToPosition(0);
                    mBinding.refreshLayout.autoRefresh();
                }
                break;
        }
    }
}
