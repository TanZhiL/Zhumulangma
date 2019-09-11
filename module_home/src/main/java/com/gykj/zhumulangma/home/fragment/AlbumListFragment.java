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
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AlbumListViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Home.F_ALBUM_LIST)
public class AlbumListFragment extends BaseMvvmFragment<AlbumListViewModel> implements BaseQuickAdapter.OnItemClickListener,
        OnLoadMoreListener {
    //猜你喜欢
    public static final int LIKE = 0;
    //付费精品
    public static final int PAID = -1;
    //主播专辑
    public static final int ANNOUNCER = 999;
    @Autowired(name = KeyCode.Home.TYPE)
    public int type;
    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long announcerId;
    @Autowired(name = KeyCode.Home.TITLE)
    public String title;
    private RecyclerView rv;
    private SmartRefreshLayout refreshLayout;
    private AlbumAdapter mAdapter;

    public AlbumListFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void initView(View view) {
        rv = fd(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setHasFixedSize(true);
        mAdapter = new AlbumAdapter(R.layout.home_item_album);
        mAdapter.bindToRecyclerView(rv);

        refreshLayout = view.findViewById(R.id.refreshLayout);
    }

    @Override
    public void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    @Override
    public void initData() {
        setTitle(new String[]{title});
        if (type == LIKE) {
            mViewModel._getGuessLikeAlbum();
            refreshLayout.setNoMoreData(true);
        } else if (type == PAID) {
            mViewModel._getPaidList();
        } else if (type == ANNOUNCER) {
            mViewModel._getAlbumList(announcerId);
        } else {
            mViewModel._getAlbumList(String.valueOf(type));
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mAdapter.getData().get(position).getId())
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (type == PAID) {
            mViewModel._getPaidList();
        } else if (type == ANNOUNCER) {
            mViewModel._getAlbumList(announcerId);
        } else {
            mViewModel._getAlbumList(String.valueOf(type));
        }
    }


    @Override
    public Class<AlbumListViewModel> onBindViewModel() {
        return AlbumListViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    protected Integer[] onBindBarRightIcon() {
        if (type == ANNOUNCER) {
            return null;

        }
        return new Integer[]{R.drawable.ic_common_search};
    }

    @Override
    public void initViewObservable() {
        mViewModel.getAlbumSingleLiveEvent().observe(this, albums -> {

            if (null == albums || (mAdapter.getData().size() == 0 && albums.size() == 0)) {
                showNoDataView(true);
                return;
            }
            if (albums.size() > 0) {
                mAdapter.addData(albums);
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
        mViewModel.getLikeSingleLiveEvent().observe(this, albums -> {

            if (null == albums || (mAdapter.getData().size() == 0 && albums.size() == 0)) {
                showNoDataView(true);
                return;
            }
            if (albums.size() > 0) {
                mAdapter.addData(albums);
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }
}
