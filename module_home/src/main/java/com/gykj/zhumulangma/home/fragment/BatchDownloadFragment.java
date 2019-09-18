package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
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
import com.gykj.zhumulangma.common.util.SystemUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.DownloadTrackAdapter;
import com.gykj.zhumulangma.home.adapter.TrackPagerAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.BatchDownloadViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.exception.AddDownloadException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener {


    @Autowired(name = KeyCode.Home.ALBUMID)
    public long mAlbumId;

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FrameLayout flMask;
    private RecyclerView rvPager;

    private TrackPagerAdapter mPagerAdapter;

    private DownloadTrackAdapter mDownloadTrackAdapter;
    private long mTotalSize;
    private TextView tvSize;
    private View tvDownload;

    private Handler mHandler = new Handler();

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
        tvSize = fd(R.id.tv_memory);
        tvDownload = fd(R.id.tv_batch_download);
        flMask = fd(R.id.fl_mask);
        rvPager = fd(R.id.rv_pager);
        rvPager.setLayoutManager(new GridLayoutManager(mContext, 4));
        mPagerAdapter = new TrackPagerAdapter(R.layout.home_item_pager);
        mPagerAdapter.bindToRecyclerView(rvPager);

        mDownloadTrackAdapter = new DownloadTrackAdapter(R.layout.home_item_batch_download);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        mDownloadTrackAdapter.bindToRecyclerView(recyclerView);
    }

    @Override
    public void initListener() {
        super.initListener();
        refreshLayout.setOnRefreshLoadMoreListener(this);
        mDownloadTrackAdapter.setOnItemClickListener(this);
        fd(R.id.cb_all).setOnClickListener(this);
        fd(R.id.ll_select).setOnClickListener(this);
        fd(R.id.tv_batch_download).setOnClickListener(this);
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
            ((CheckBox) fd(R.id.cb_all)).setChecked(false);
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
                ((CheckBox) fd(R.id.cb_all)).setChecked(false);
                mDownloadTrackAdapter.addData(0, tracks.getTracks());
                refreshLayout.finishRefresh();
            }
        });
        mViewModel.getTracksMoreSingleLiveEvent().observe(this, tracks -> {
            ((CheckBox) fd(R.id.cb_all)).setChecked(false);
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
    protected void onRevisible() {
        super.onRevisible();
        mDownloadTrackAdapter.notifyDataSetChanged();
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
            Track track = mDownloadTrackAdapter.getItem(position);
            if (XmDownloadManager.getInstance().getSingleTrackDownloadStatus(track.getDataId()) != DownloadState.NOADD) {
                return;
            }
            CheckBox checkBox = view.findViewById(R.id.cb);
            checkBox.setChecked(!checkBox.isChecked());
            boolean checked = checkBox.isChecked();

            if (checked) {
                mTotalSize += track.getDownloadSize();
                mDownloadTrackAdapter.getSelectedTracks().add(track);
            } else {
                mTotalSize -= track.getDownloadSize();
                mDownloadTrackAdapter.getSelectedTracks().remove(track);
            }
            if (mDownloadTrackAdapter.getSelectedTracks().size() > 0) {
                tvSize.setVisibility(View.VISIBLE);
                tvSize.setText(getString(R.string.selected_num,
                        mDownloadTrackAdapter.getSelectedTracks().size(), ZhumulangmaUtil.byte2FitMemorySize(mTotalSize),
                        SystemUtil.getRomTotalSize(mContext)));
                tvDownload.setEnabled(true);
                tvDownload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                tvSize.setVisibility(View.GONE);
                tvDownload.setEnabled(false);
                tvDownload.setBackgroundColor(getResources().getColor(R.color.colorHint));
            }
        } else {
            switchCategory();
            mViewModel.getTrackList(String.valueOf(mAlbumId), position + 1);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.ll_select == id || R.id.fl_mask == id) {
            switchCategory();
        } else if (id == R.id.cb_all) {
            boolean isChecked = ((CheckBox) v).isChecked();
            List<Track> tracks = mDownloadTrackAdapter.getData();
            List<Track> selectedTracks = mDownloadTrackAdapter.getSelectedTracks();
            if (isChecked) {
                for (int i = 0; i < tracks.size(); i++) {
                    if (!selectedTracks.contains(tracks.get(i)) &&
                            XmDownloadManager.getInstance().getSingleTrackDownloadStatus(tracks.get(i).getDataId()) == DownloadState.NOADD) {
                        mTotalSize += tracks.get(i).getDownloadSize();
                        selectedTracks.add(tracks.get(i));
                        mDownloadTrackAdapter.notifyItemChanged(i);
                    }
                }
            } else {
                for (int i = 0; i < tracks.size(); i++) {
                    if (selectedTracks.contains(tracks.get(i))) {
                        mTotalSize -= tracks.get(i).getDownloadSize();
                        selectedTracks.remove(tracks.get(i));
                        mDownloadTrackAdapter.notifyItemChanged(i);
                    }
                }
            }

            if (mDownloadTrackAdapter.getSelectedTracks().size() > 0) {
                tvSize.setVisibility(View.VISIBLE);
                tvSize.setText(getString(R.string.selected_num,
                        mDownloadTrackAdapter.getSelectedTracks().size(), ZhumulangmaUtil.byte2FitMemorySize(mTotalSize),
                        SystemUtil.getRomTotalSize(mContext)));
                tvDownload.setEnabled(true);
                tvDownload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                tvSize.setVisibility(View.GONE);
                tvDownload.setEnabled(false);
                tvDownload.setBackgroundColor(getResources().getColor(R.color.colorHint));
            }

        } else if (R.id.tv_batch_download == id) {
            List<Track> selectedTracks = mDownloadTrackAdapter.getSelectedTracks();
            if (CollectionUtils.isEmpty(selectedTracks)) {
                return;
            }

            Collections.sort(selectedTracks, (o1, o2) ->
                    Integer.compare(o2.getOrderPositionInAlbum(), o1.getOrderPositionInAlbum()));
            List<Long> trackIds = new ArrayList<>();
            Iterator<Track> iterator = selectedTracks.iterator();
            while (iterator.hasNext()) {
                trackIds.add(iterator.next().getDataId());
            }
            TLog.d(trackIds);
            XmDownloadManager.getInstance().downloadTracks(trackIds, true, new IDoSomethingProgress<AddDownloadException>() {
                @Override
                public void begin() {

                }

                @Override
                public void success() {
                    mTotalSize = 0;
                    tvSize.setVisibility(View.GONE);
                    tvDownload.setEnabled(false);
                    ((CheckBox) fd(R.id.cb_all)).setChecked(false);
                    tvDownload.setBackgroundColor(getResources().getColor(R.color.colorHint));
                    ToastUtil.showToast("已加入下载队列");
                    selectedTracks.clear();
                    mDownloadTrackAdapter.notifyDataSetChanged();
                }

                @Override
                public void fail(AddDownloadException e) {
                   if(e.getCode()==AddDownloadException.CODE_NULL){
                       ToastUtil.showToast("参数不能为null");
                   }else if(e.getCode()==AddDownloadException.CODE_MAX_OVER){
                       ToastUtil.showToast("批量下载个数超过最大值");
                   }else if(e.getCode()==AddDownloadException.CODE_NOT_FIND_TRACK){
                       ToastUtil.showToast("不能找到相应的声音");
                   }else if(e.getCode()==AddDownloadException.CODE_MAX_DOWNLOADING_COUNT){
                       ToastUtil.showToast("同时下载的音频个数不能超过500");
                   }else if(e.getCode()==AddDownloadException.CODE_DISK_OVER){
                       ToastUtil.showToast("磁盘已满");
                   }else if(e.getCode()==AddDownloadException.CODE_MAX_SPACE_OVER){
                       ToastUtil.showToast("下载的音频超过了设置的最大空间");
                   }else if(e.getCode()==AddDownloadException.CODE_NO_PAY_SOUND){
                       ToastUtil.showToast("下载的付费音频中有没有支付");
                   }
                }
            });

        }
    }


    private void changePageStatus() {
        for (int i = 0; i < mPagerAdapter.getData().size(); i++) {
            Iterator<Track> iterator = mDownloadTrackAdapter.getSelectedTracks().iterator();
            View ivSelected = mPagerAdapter.getViewByPosition(i, R.id.iv_selected);
            if (ivSelected != null) {
                ivSelected.setVisibility(View.GONE);
                while (iterator.hasNext()) {
                    Track next = iterator.next();
                    int page = next.getOrderPositionInAlbum() / BatchDownloadViewModel.PAGESIEZ;
                    if (page == i) {
                        ivSelected.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
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
            mHandler.postDelayed(() -> changePageStatus(), 200);
        }
    }

    private void setPager(long totalcount) {
        int pagesize = 50;
        ((TextView) fd(R.id.tv_pagecount)).setText(getString(R.string.pagecount, totalcount));
        List<String> list = new ArrayList<>();
        for (int i = 0; i < totalcount / pagesize; i++) {
            list.add(totalcount - (i * pagesize) + "~" + (totalcount - ((i + 1) * pagesize) + 1));
        }
        if (totalcount % pagesize != 0) {
            list.add(totalcount - totalcount / pagesize * pagesize + "~1");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
