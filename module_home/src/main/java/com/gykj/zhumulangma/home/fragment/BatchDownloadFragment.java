package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.DownloadTrackAdapter;
import com.gykj.zhumulangma.home.adapter.TrackPagerAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.BatchDownloadViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/12 8:49
 * Email: 1071931588@qq.com
 * Description:
 */

@Route(path = AppConstants.Router.Home.F_BATCH_DOWNLOAD)
public class BatchDownloadFragment extends BaseMvvmFragment<BatchDownloadViewModel> implements OnRefreshLoadMoreListener,
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {


    @Autowired(name = KeyCode.Home.ALBUMID)
    public long mAlbumId;

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FrameLayout flMask;
    private RecyclerView rvPager;
    private TrackPagerAdapter mPagerAdapter;

    private DownloadTrackAdapter mDownloadTrackAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_batch_download;
    }

    @Override
    protected void initView(View view) {

        recyclerView = fd(R.id.rv);
        refreshLayout = fd(R.id.refreshLayout);
        flMask = fd(R.id.fl_mask);
        rvPager = fd(R.id.rv_pager);

        flMask = fd(R.id.fl_mask);
        rvPager = fd(R.id.rv_pager);
        rvPager.setLayoutManager(new GridLayoutManager(mContext, 4));
        mPagerAdapter = new TrackPagerAdapter(R.layout.home_item_pager);
        mPagerAdapter.bindToRecyclerView(rvPager);

        mDownloadTrackAdapter = new DownloadTrackAdapter(R.layout.home_item_download_track);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        mDownloadTrackAdapter.bindToRecyclerView(recyclerView);
    }

    @Override
    public void initListener() {
        super.initListener();
        refreshLayout.setOnRefreshLoadMoreListener(this);
        mDownloadTrackAdapter.setOnItemClickListener(this);
        mDownloadTrackAdapter.setOnItemChildClickListener(this);
        fd(R.id.ll_select).setOnClickListener(this);
        flMask.setOnClickListener(this);
        mPagerAdapter.setOnItemClickListener(this);
        rvPager.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                changePageStatus();
            }
        });
    }

    @Override
    public void initData() {
        mViewModel.getTrackList(String.valueOf(mAlbumId));
    }

    @Override
    public void initViewObservable() {
        mViewModel.getTracksInitSingleLiveEvent().observe(this, tracks -> {
            setPager(tracks.getTotalCount());
            mDownloadTrackAdapter.setNewData(tracks.getTracks());
        });

        mViewModel.getTracksUpSingleLiveEvent().observe(this, tracks -> {
            if (tracks == null || CollectionUtils.isEmpty(tracks.getTracks())) {
                if (0 == mDownloadTrackAdapter.getData().size()) {
                    showNoDataView(true);
                } else {
                    refreshLayout.finishRefresh();
                }
            } else {
                mDownloadTrackAdapter.addData(0, tracks.getTracks());
                refreshLayout.finishRefresh();
            }
        });
        mViewModel.getTracksMoreSingleLiveEvent().observe(this, tracks -> {
            if (CollectionUtils.isEmpty(tracks.getTracks())) {
                if (0 == mDownloadTrackAdapter.getData().size()) {
                    showNoDataView(true);
                } else {
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }
            } else {
                mDownloadTrackAdapter.addData(tracks.getTracks());
                refreshLayout.finishLoadMore();
            }
        });
    }
    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.HIDE_GP));
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.SHOW_GP));
    }
    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"批量下载"};
    }

    @Override
    protected Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_download};
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getTrackList(String.valueOf(mAlbumId), false);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mViewModel.getTrackList(String.valueOf(mAlbumId), true);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mDownloadTrackAdapter) {

        } else {
            switchCategory();
            mViewModel.getTrackList(String.valueOf(mAlbumId),  position + 1);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (R.id.ll_select == id || R.id.fl_mask == id) {
            switchCategory();
        }
    }


    private void changePageStatus() {
        for (int i = 0; i < mPagerAdapter.getData().size(); i++) {
            TextView viewByPosition = (TextView) mPagerAdapter.getViewByPosition(i, R.id.tv_page);
            if (viewByPosition != null) {
                if (mViewModel.getUpTrackPage() <= i && i <= mViewModel.getCurTrackPage() - 2) {
                    viewByPosition.setBackgroundResource(R.drawable.shap_common_primary);
                    viewByPosition.setTextColor(Color.WHITE);
                } else {
                    viewByPosition.setBackgroundResource(R.drawable.shap_common_defualt);
                    viewByPosition.setTextColor(getResources().getColor(R.color.textColorPrimary));
                }
            }
        }
    }

    private void switchCategory() {
        if (flMask.getVisibility() == View.VISIBLE) {
            rvPager.animate().translationY(-rvPager.getHeight()).setDuration(200).withEndAction(() -> {
                flMask.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                fd(R.id.cl_action).setVisibility(View.VISIBLE);
            });
            fd(R.id.iv_select_page).animate().rotationBy(180).setDuration(200);
        } else {
            refreshLayout.setVisibility(View.GONE);
            fd(R.id.cl_action).setVisibility(View.GONE);
            flMask.setVisibility(View.VISIBLE);
            rvPager.setTranslationY(-rvPager.getHeight() == 0 ? -400 : -rvPager.getHeight());
            rvPager.animate().translationY(0).setDuration(200);
            fd(R.id.iv_select_page).animate().rotationBy(180).setDuration(200);
            new Handler().postDelayed(() -> changePageStatus(), 200);
        }
    }

    private void setPager(long totalcount) {
        int pagesize = 50;
        ((TextView) fd(R.id.tv_pagecount)).setText(getString(R.string.pagecount,totalcount));
        List<String> list = new ArrayList<>();

            for (int i = 0; i < totalcount / pagesize; i++) {
                list.add((i * pagesize + 1) + "~" + ((i + 1) * pagesize));
            }
            if (totalcount % pagesize != 0) {
                list.add((totalcount / pagesize * pagesize + 1) + "~" + totalcount);
            }
        mPagerAdapter.setNewData(list);
    }
    @Override
    public boolean onBackPressedSupport() {
        if (flMask.getVisibility() == View.VISIBLE) {
            switchCategory();
        } else {
            pop();
        }
        return true;
    }

    @Override
    public Class<BatchDownloadViewModel> onBindViewModel() {
        return BatchDownloadViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    protected void onRight1Click(View v) {
        super.onRight1Click(v);
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Listen.F_DOWNLOAD)
                .withInt(KeyCode.Listen.TAB_INDEX, 2)
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Listen.F_DOWNLOAD, (ISupportFragment) navigation)));
    }
}
