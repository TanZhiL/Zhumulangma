package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.adapter.AnnouncerTrackAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.TrackListViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Author: Thomas.
 * Date: 2019/9/11 11:40
 * Email: 1071931588@qq.com
 * Description:
 */

@Route(path = AppConstants.Router.Home.F_TRACK_LIST)
public class TrackListFragment extends BaseMvvmFragment<TrackListViewModel> implements BaseQuickAdapter.OnItemClickListener, OnLoadMoreListener {

    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long announcerId;
    @Autowired(name = KeyCode.Home.TITLE)
    public String title;
    private RecyclerView rv;
    private SmartRefreshLayout refreshLayout;
    private AnnouncerTrackAdapter mAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void initView(View view) {
        rv=fd(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setHasFixedSize(true);
        mAdapter = new AnnouncerTrackAdapter(R.layout.home_item_announcer_track);
        mAdapter.bindToRecyclerView(rv);
        setTitle(new String[]{title});
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
        mViewModel.getTrack(announcerId);
    }
    @Override
    public void initViewObservable() {
        mViewModel.getTrackListSingleLiveEvent().observe(this, tracks -> {

            if (null == tracks || (mAdapter.getData().size() == 0 && tracks.size() == 0)) {
                showNoDataView(true);
                return;
            }
            if (tracks.size() > 0) {
                mAdapter.addData(tracks);
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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Track track = mAdapter.getData().get(position);
        mViewModel.play(track.getAlbum().getAlbumId(),track.getDataId());
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getTrack(announcerId);
    }

    @Override
    public Class<TrackListViewModel> onBindViewModel() {
        return TrackListViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }


}
