package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AnnouncerAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerListViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/11 11:40
 * Email: 1071931588@qq.com
 * Description:
 */

@Route(path = AppConstants.Router.Home.F_ANNOUNCER_LIST)
public class AnnouncerListFragment extends BaseRefreshMvvmFragment<AnnouncerListViewModel, Announcer>
        implements BaseQuickAdapter.OnItemClickListener, OnLoadMoreListener {

    @Autowired(name = KeyCode.Home.CATEGORY_ID)
    public long categoryId;
    @Autowired(name = KeyCode.Home.TITLE)
    public String title;
    private RecyclerView rv;
    private SmartRefreshLayout refreshLayout;
    private AnnouncerAdapter mAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void initView(View view) {
        rv=fd(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setHasFixedSize(true);
        mAdapter = new AnnouncerAdapter(R.layout.home_item_announcer);
        mAdapter.bindToRecyclerView(rv);
        setTitle(new String[]{title});
        refreshLayout = view.findViewById(R.id.refreshLayout);
    }

    @Override
    public void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    @Override
    public void initData() {
        mViewModel.setCategoryId(categoryId);
        mViewModel.getAnnouncerList(categoryId);
    }



    @Override
    public void initViewObservable() {}

    @Override
    protected void onLoadMoreSucc(List<Announcer> list) {
        super.onLoadMoreSucc(list);
        mAdapter.addData(list);
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
                .withLong(KeyCode.Home.ANNOUNCER_ID, mAdapter.getItem(position).getAnnouncerId())
                .withString(KeyCode.Home.ANNOUNCER_NAME, mAdapter.getItem(position).getNickname())
                .navigation();
        NavigateBean navigateBean = new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation);
        navigateBean.launchMode=STANDARD;
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.NAVIGATE,navigateBean));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getAnnouncerList(categoryId);
    }

    @Override
    public Class<AnnouncerListViewModel> onBindViewModel() {
        return AnnouncerListViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }


}
