package com.gykj.zhumulangma.listen.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.event.common.BaseFragmentEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.SubscribeAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.SubscribeViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/10 8:23
 * Email: 1071931588@qq.com
 * Description:订阅
 */
public class SubscribeFragment extends BaseRefreshMvvmFragment<SubscribeViewModel, SubscribeBean>
        implements BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

    private SubscribeAdapter mSubscribeAdapter;

    private View vFooter;
    private SmartRefreshLayout refreshLayout;
    public SubscribeFragment() {
    }


    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = fd(R.id.recyclerview);
        refreshLayout = fd(R.id.refreshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        mSubscribeAdapter = new SubscribeAdapter(R.layout.listen_item_subscribe);
        mSubscribeAdapter.bindToRecyclerView(recyclerView);
        View inflate = LayoutInflater.from(mActivity).inflate(R.layout.listen_layout_subscribe_footer, null);
        vFooter=inflate.findViewById(R.id.cl_content);
        mSubscribeAdapter.addFooterView(inflate);
    }

    @Override
    public void initListener() {
        super.initListener();
        refreshLayout.setOnRefreshLoadMoreListener(this);
        mSubscribeAdapter.setOnItemChildClickListener(this);
        mSubscribeAdapter.setOnItemClickListener(this);
        vFooter.setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout,mSubscribeAdapter);
    }

    @Override
    public void initData() {
        mSubscribeAdapter.setNewData(null);
        mViewModel.getSubscribes();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitSubscribesEvent().observe(this, subscribeBeans ->
                mSubscribeAdapter.setNewData(subscribeBeans));

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (id == R.id.iv_play) {
            mViewModel.play(String.valueOf(mSubscribeAdapter.getItem(position).getAlbumId()));
        }
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mSubscribeAdapter.getItem(position).getAlbum().getId())
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
    }

    @Override
    public void onClick(View v) {
        if(v==vFooter){
            navigateTo(AppConstants.Router.Home.F_RANK);
        }
    }

    @Override
    public <T> void onEvent(BaseFragmentEvent<T> event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Main.TAB_REFRESH:
                if(isSupportVisible()&&mBaseLoadService.getCurrentCallback()!=getInitCallBack().getClass()){
                    ((SmartRefreshLayout)fd(R.id.refreshLayout)).autoRefresh();
                }
                break;
        }
    }
}
