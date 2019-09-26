package com.gykj.zhumulangma.listen.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.RecommendAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.SubscribeViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/20 14:23
 * Email: 1071931588@qq.com
 * Description:
 */
public class RecommendFragment  extends BaseMvvmFragment<SubscribeViewModel> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecommendAdapter mRecommendAdapter;
    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }
    @Override
    protected void initView(View view) {
        mRecyclerView = fd(R.id.rv);
        mRefreshLayout = fd(R.id.refreshLayout);
        mRefreshLayout.setEnableLoadMore(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecommendAdapter = new RecommendAdapter(R.layout.listen_item_recommend);
        mRecommendAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    public void initListener() {
        super.initListener();
        mRecommendAdapter.setOnItemChildClickListener(this);
        mRecommendAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel._getGuessLikeAlbum();
    }
    @Override
    public void initViewObservable() {
        mViewModel.getLikeSingleLiveEvent().observe(this, albums -> {
            if (CollectionUtils.isEmpty(albums)) {
                    showEmptyView(true);
            } else {
                mRecommendAdapter.setNewData(albums);
            }
        });

        mViewModel.getSubscribeSingleLiveEvent().observe(this, album -> {
            List<Album> data = mRecommendAdapter.getData();
            int index = data.indexOf(album);
            if(index>-1){
                try {
                    mRecommendAdapter.getViewByPosition(index, R.id.ll_subscribe).setVisibility(View.GONE);
                    mRecommendAdapter.getViewByPosition(index, R.id.ll_unsubscribe).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mViewModel.getUnSubscribeSingleLiveEvent().observe(this, album -> {
            List<Album> data = mRecommendAdapter.getData();
            int index = data.indexOf(album);
            if(index>-1){
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
    protected boolean enableSimplebar() {
        return false;
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if(id==R.id.ll_subscribe){
            mViewModel.subscribe(mRecommendAdapter.getItem(position));
        }else {
            mViewModel.unsubscribe(mRecommendAdapter.getItem(position));
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mRecommendAdapter.getItem(position).getId())
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
    }
}
