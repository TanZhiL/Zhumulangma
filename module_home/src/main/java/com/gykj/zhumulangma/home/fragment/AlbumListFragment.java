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
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AlbumListViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;
/**
 * Author: Thomas.
 * Date: 2019/8/14 10:21
 * Email: 1071931588@qq.com
 * Description:专辑列表页
 */
@Route(path = AppConstants.Router.Home.F_ALBUM_LIST)
public class AlbumListFragment extends BaseRefreshMvvmFragment<AlbumListViewModel, Album> implements
        BaseQuickAdapter.OnItemClickListener {
    //猜你喜欢
    public static final int LIKE = 0;
    //付费精品
    public static final int PAID = -1;
    //主播专辑
    public static final int ANNOUNCER = 999;
    @Autowired(name = KeyCode.Home.TYPE)
    public int mType;
    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long mAnnouncerId;
    @Autowired(name = KeyCode.Home.TITLE)
    public String mTitle;
    private AlbumAdapter mAlbumAdapter;

    private SmartRefreshLayout refreshLayout;

    public AlbumListFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = fd(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        mAlbumAdapter = new AlbumAdapter(R.layout.home_item_album);
        mAlbumAdapter.bindToRecyclerView(recyclerView);
        refreshLayout = fd(R.id.refreshLayout);
    }

    @Override
    public void initListener() {
        super.initListener();
        mAlbumAdapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout,mAlbumAdapter);
    }

    @Override
    public void initData() {
        setTitle(new String[]{mTitle});
        mViewModel.setType(mType);
        mViewModel.setAnnouncerId(mAnnouncerId);
        mViewModel.init();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitAlbumsEvent().observe(this, albums -> mAlbumAdapter.setNewData(albums));
    }
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getItem(position).getId())
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
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
        if (mType == ANNOUNCER|| mType == LIKE) {
            return null;
        }
        return new Integer[]{R.drawable.ic_common_search};
    }


    @Override
    protected boolean lazyEnable() {
        return false;
    }
}
