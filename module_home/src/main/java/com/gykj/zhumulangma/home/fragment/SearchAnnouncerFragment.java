package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.ListCallback;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AnnouncerAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchAnnouncerViewModel;
import com.kingja.loadsir.callback.Callback;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/13 15:12
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索主播
 */
public class SearchAnnouncerFragment extends BaseRefreshMvvmFragment<SearchAnnouncerViewModel, Announcer>
        implements BaseQuickAdapter.OnItemClickListener {


    private SmartRefreshLayout refreshLayout;
   private AnnouncerAdapter mAnnouncerAdapter;

    public SearchAnnouncerFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackground(null);
        setSwipeBackEnable(false);
    }
    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        mAnnouncerAdapter =new AnnouncerAdapter(R.layout.home_item_announcer);
        mAnnouncerAdapter.bindToRecyclerView(recyclerView);
        mAnnouncerAdapter.setOnItemClickListener(this);
        refreshLayout=view.findViewById(R.id.refreshLayout);
    }




    @Override
    public void initData() {
        String keyword = getArguments().getString(KeyCode.Home.KEYWORD);
        mViewModel.setKeyword(keyword);
       mViewModel.init();
    }



    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
                .withLong(KeyCode.Home.ANNOUNCER_ID, mAnnouncerAdapter.getItem(position).getAnnouncerId())
                .withString(KeyCode.Home.ANNOUNCER_NAME, mAnnouncerAdapter.getItem(position).getNickname())
                .navigation();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation)));
    }
    @Override
    public Class<SearchAnnouncerViewModel> onBindViewModel() {
        return SearchAnnouncerViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitAnnouncersEvent().observe(this, announcers -> mAnnouncerAdapter.setNewData(announcers));
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout, mAnnouncerAdapter);
    }
     @Override
    protected Callback getInitCallBack() {
        return new ListCallback();
    }
}
