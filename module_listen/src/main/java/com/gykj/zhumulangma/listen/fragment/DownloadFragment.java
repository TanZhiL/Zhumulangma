package com.gykj.zhumulangma.listen.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.SystemUtil;
import com.gykj.zhumulangma.common.widget.CircleProgressBar;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.DownloadAlbumAdapter;
import com.gykj.zhumulangma.listen.adapter.DownloadTrackAdapter;
import com.gykj.zhumulangma.listen.adapter.DownloadingAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.DownloadViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.BaseRuntimeException;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;
/**
 * Author: Thomas.
 * Date: 2019/9/10 8:23
 * Email: 1071931588@qq.com
 * Description:下载页
 */
@Route(path = AppConstants.Router.Listen.F_DOWNLOAD)
public class DownloadFragment extends BaseMvvmFragment<DownloadViewModel> implements
        BaseQuickAdapter.OnItemChildClickListener, IXmDownloadTrackCallBack,
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener, IXmPlayerStatusListener {
    @Autowired(name = KeyCode.Listen.TAB_INDEX)
    public int mTabIndex;

    private DownloadAlbumAdapter mAlbumAdapter;
    private DownloadTrackAdapter mTrackAdapter;
    private DownloadingAdapter mDownloadingAdapter;
    private Handler mHandler = new Handler();

    private TextView tvMemory;
    private MagicIndicator magicIndicator;
    private ViewGroup layoutDetail1, layoutDetail2, layoutDetail3;


    public DownloadFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.listen_fragment_download;
    }

    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }

    @Override
    protected void initView(View view) {
        String[] tabs = {"专辑", "声音", "下载中"};
        layoutDetail1 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.common_layout_refresh_loadmore, null);
        layoutDetail2 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.common_layout_refresh_loadmore, null);
        layoutDetail3 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.listen_layout_downloading, null);
        ((RefreshLayout) layoutDetail1.findViewById(R.id.refreshLayout)).setEnableRefresh(false);
        ((RefreshLayout) layoutDetail1.findViewById(R.id.refreshLayout)).setEnableLoadMore(false);

        ((RefreshLayout) layoutDetail2.findViewById(R.id.refreshLayout)).setEnableRefresh(false);
        ((RefreshLayout) layoutDetail2.findViewById(R.id.refreshLayout)).setEnableLoadMore(false);

        ((RefreshLayout) layoutDetail3.findViewById(R.id.refreshLayout)).setEnableRefresh(false);
        ((RefreshLayout) layoutDetail3.findViewById(R.id.refreshLayout)).setEnableLoadMore(false);


        RecyclerView rvAlbum = layoutDetail1.findViewById(R.id.rv);
        rvAlbum.setHasFixedSize(true);
        rvAlbum.setLayoutManager(new LinearLayoutManager(mContext));
        mAlbumAdapter = new DownloadAlbumAdapter(R.layout.listen_item_download_album);
        mAlbumAdapter.bindToRecyclerView(rvAlbum);
        mAlbumAdapter.setEmptyView(R.layout.common_layout_empty);

        RecyclerView rvTrack = layoutDetail2.findViewById(R.id.rv);
        rvTrack.setHasFixedSize(true);
        rvTrack.setLayoutManager(new LinearLayoutManager(mContext));
        mTrackAdapter = new DownloadTrackAdapter(R.layout.listen_item_download_track);
        mTrackAdapter.bindToRecyclerView(rvTrack);
        mTrackAdapter.setEmptyView(R.layout.common_layout_empty);

        RecyclerView rvRecommend = layoutDetail3.findViewById(R.id.rv);
        rvRecommend.setHasFixedSize(true);
        rvRecommend.setLayoutManager(new LinearLayoutManager(mContext));
        mDownloadingAdapter = new DownloadingAdapter(R.layout.listen_item_downloading);
        mDownloadingAdapter.bindToRecyclerView(rvRecommend);
        mDownloadingAdapter.setEmptyView(R.layout.common_layout_empty);

        tvMemory = fd(R.id.tv_memory);
        ViewPager viewpager = fd(R.id.viewpager);
        viewpager.setAdapter(new DownloadPagerAdapter());
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), viewpager, 50));
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
        viewpager.setCurrentItem(mTabIndex);

        if (!XmDownloadManager.getInstance().haveDowningTask()) {
            ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部开始");
            ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_download);
        } else {
            ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部暂停");
            ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_pause);
        }
        ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("("+XmDownloadManager.getInstance().getDownloadTrackCount(false)+")");
    }

    @Override
    public void initListener() {
        super.initListener();
        mAlbumAdapter.setOnItemChildClickListener(this);
        mAlbumAdapter.setOnItemClickListener(this);
        mTrackAdapter.setOnItemChildClickListener(this);
        mTrackAdapter.setOnItemClickListener(this);
        mDownloadingAdapter.setOnItemChildClickListener(this);
        mDownloadingAdapter.setOnItemClickListener(this);
        layoutDetail3.findViewById(R.id.tv_all).setOnClickListener(this);
        layoutDetail3.findViewById(R.id.iv_all).setOnClickListener(this);
        layoutDetail3.findViewById(R.id.tv_delete).setOnClickListener(this);
        layoutDetail3.findViewById(R.id.iv_delete).setOnClickListener(this);

        XmDownloadManager.getInstance().addDownloadStatueListener(this);
        XmPlayerManager.getInstance(mContext).addPlayerStatusListener(this);
    }

    @Override
    public void initData() {
        tvMemory.setText(getString(R.string.memory,
                XmDownloadManager.getInstance().getHumanReadableDownloadOccupation(IDownloadManager.Auto),
                SystemUtil.getRomTotalSize(mContext)));
        mAlbumAdapter.setNewData(XmDownloadManager.getInstance().getDownloadAlbums(true));
        mTrackAdapter.setNewData(XmDownloadManager.getInstance().getDownloadTracks(true));
        mDownloadingAdapter.setNewData(XmDownloadManager.getInstance().getDownloadTracks(false));
        if (XmDownloadManager.getInstance().getDownloadTracks(false).size() > 0) {
            layoutDetail3.findViewById(R.id.cl_action).setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void initViewObservable() {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (adapter == mAlbumAdapter) {
            if (id == R.id.ll_delete) {

                XmDownloadManager.getInstance().clearDownloadedAlbum(mAlbumAdapter.getItem(position).getAlbumId(), null);

            }
        } else if (adapter == mTrackAdapter) {
            if (id == R.id.ll_delete) {
                XmDownloadManager.getInstance().clearDownloadedTrack(mTrackAdapter.getItem(position).getDataId());
            }
        } else if (adapter == mDownloadingAdapter) {
            if (id == R.id.ll_delete) {
                try {
                    XmDownloadManager.getInstance().cancelDownloadSingleTrack(mDownloadingAdapter.getItem(position).getDataId());
                    mDownloadingAdapter.remove(position);
                    if (mDownloadingAdapter.getData().size() == 0) {
                        layoutDetail3.findViewById(R.id.cl_action).setVisibility(View.GONE);
                    }else {
                        mHandler.postDelayed(() -> {
                            if (!XmDownloadManager.getInstance().haveDowningTask()) {
                                ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部开始");
                                ((ImageView)
                                        layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_download);
                            } else {
                                ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部暂停");
                                ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_pause);
                            }
                            ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("("+XmDownloadManager.getInstance().getDownloadTrackCount(false)+")");
                        }, 200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Class<DownloadViewModel> onBindViewModel() {
        return DownloadViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void onWaiting(Track track) {
        updateDownloadStatus(track);
        Log.d(TAG, "onWaiting() called with: track = [" + track + "]");
    }

    @Override
    public void onStarted(Track track) {
        updateDownloadStatus(track);
        Log.d(TAG, "onStarted() called with: track = [" + track + "]");
    }


    @Override
    public void onSuccess(Track track) {
        int index = mDownloadingAdapter.getData().indexOf(track);
        if (index != -1) {
            mDownloadingAdapter.remove(index);
            if (mDownloadingAdapter.getData().size() == 0) {
                layoutDetail3.findViewById(R.id.cl_action).setVisibility(View.GONE);
            }
        }
        ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("("+XmDownloadManager.getInstance().getDownloadTrackCount(false)+")");
        mAlbumAdapter.setNewData(XmDownloadManager.getInstance().getDownloadAlbums(true));
        mTrackAdapter.addData(track);
        Log.d(TAG, "onSuccess() called with: track = [" + track + "]");
    }

    @Override
    public void onError(Track track, Throwable throwable) {
        throwable.printStackTrace();
        Log.d(TAG, "onError() called with: track = [" + track + "], throwable = [" + throwable + "]");
    }

    @Override
    public void onCancelled(Track track, Callback.CancelledException e) {
        updateDownloadStatus(track);

        Log.d(TAG, "onCancelled() called with: track = [" + track + "], e = [" + e + "]");
    }

    @Override
    public void onProgress(Track track, long l, long l1) {
        updateDownloadStatus(track);
        int index = mDownloadingAdapter.getData().indexOf(track);
        if (index != -1) {
            CircleProgressBar progressBar = (CircleProgressBar) mDownloadingAdapter.getViewByPosition(index, R.id.cpb_progress);
            if (progressBar != null) {
                progressBar.setSecondColor(mContext.getResources().getColor(R.color.colorPrimary));
                progressBar.setProgress((int) (l1 * 100 / l));
            }
        }

        Log.d(TAG, "onProgress() called with: track = [" + track + "], l = [" + l + "], l1 = [" + l1 + "]");
    }

    @Override
    public void onRemoved() {
        initData();
        Log.d(TAG, "onRemoved() called");
    }

    private void updateDownloadStatus(Track track) {
        int index = mDownloadingAdapter.getData().indexOf(track);
        if (index != -1) {
            ImageView ivStatus = (ImageView) mDownloadingAdapter.getViewByPosition(index, R.id.iv_status);
            TextView tvStatus = (TextView) mDownloadingAdapter.getViewByPosition(index, R.id.tv_status);
            CircleProgressBar progressBar = (CircleProgressBar) mDownloadingAdapter.getViewByPosition(index, R.id.cpb_progress);
            if (ivStatus != null && tvStatus != null && progressBar != null) {
                DownloadState downloadStatus = XmDownloadManager.getInstance()
                        .getSingleTrackDownloadStatus(track.getDataId());
                if (downloadStatus == DownloadState.WAITING) {
                    if (XmDownloadManager.getInstance().haveDowningTask()) {
                        ivStatus.setImageResource(R.drawable.ic_listen_waiting);
                        tvStatus.setText("待下载");
                        tvStatus.setTextColor(getResources().getColor(R.color.colorGray));
                        progressBar.setSecondColor(mContext.getResources().getColor(R.color.colorGray));
                    } else {
                        ivStatus.setImageResource(R.drawable.ic_listen_pause);
                        tvStatus.setText("下载中");
                        tvStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                        progressBar.setSecondColor(mContext.getResources().getColor(R.color.colorPrimary));
                    }
                } else if (downloadStatus == DownloadState.STARTED) {
                    ivStatus.setImageResource(R.drawable.ic_listen_pause);
                    tvStatus.setText("下载中");
                    tvStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                    progressBar.setSecondColor(mContext.getResources().getColor(R.color.colorPrimary));
                } else if (downloadStatus == DownloadState.STOPPED) {
                    ivStatus.setImageResource(R.drawable.ic_listen_download);
                    tvStatus.setText("已暂停");
                    tvStatus.setTextColor(getResources().getColor(R.color.colorGray));
                    progressBar.setSecondColor(mContext.getResources().getColor(R.color.colorGray));
                }
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mDownloadingAdapter) {

            try {
                if (XmDownloadManager.getInstance().getSingleTrackDownloadStatus(
                        mDownloadingAdapter.getItem(position).getDataId()) != DownloadState.STARTED) {
                    XmDownloadManager.getInstance().resumeDownloadSingleTrack(mDownloadingAdapter.getItem(position).getDataId());
                } else {
                    XmDownloadManager.getInstance().pauseDownloadSingleTrack(mDownloadingAdapter.getItem(position).getDataId());
                }

                mHandler.postDelayed(() -> {
                    if (!XmDownloadManager.getInstance().haveDowningTask()) {
                        ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部开始");
                        ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_download);
                    } else {
                        ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部暂停");
                        ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_pause);
                    }
                    ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("("+XmDownloadManager.getInstance().getDownloadTrackCount(false)+")");
                }, 200);

            } catch (Exception e) {
                //当下载完成的瞬间,会被移除,造成越界异常
                e.printStackTrace();
            }
        }else if(adapter==mTrackAdapter){
            XmPlayerManager.getInstance(mContext).playList(mTrackAdapter.getData(),position);
            navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_all || id == R.id.iv_all) {
            if (XmDownloadManager.getInstance().haveDowningTask()) {
                XmDownloadManager.getInstance().pauseAllDownloads(null);
                ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部开始");
                ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_download);
            } else {
                XmDownloadManager.getInstance().resumeAllDownloads(null);
                ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部暂停");
                ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_pause);
            }
        } else if (id == R.id.tv_delete || id == R.id.iv_delete) {
            XmDownloadManager.getInstance().cancelAllDownloads(new IDoSomethingProgress() {
                @Override
                public void begin() {
                    Log.d(TAG, "begin() called");
                }

                @Override
                public void success() {
                    Log.d(TAG, "success() called");
                    mDownloadingAdapter.getData().clear();
                    layoutDetail3.findViewById(R.id.cl_action).setVisibility(View.GONE);
                    ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("("+XmDownloadManager.getInstance().getDownloadTrackCount(false)+")");
                }

                @Override
                public void fail(BaseRuntimeException e) {
                    Log.d(TAG, "fail() called with: e = [" + e + "]");
                }
            });

        }
    }
    private void updatePlayStatus(int currPos, int duration) {
        Track track = XmPlayerManager.getInstance(mContext).getCurrSoundIgnoreKind(true);
        if (null == track) {
            return;
        }
        int index = mTrackAdapter.getData().indexOf(track);
        if (index != -1) {
            TextView tvHasplay = (TextView) mTrackAdapter.getViewByPosition(index, R.id.tv_hasplay);
            if (null != tvHasplay && mTrackAdapter.getItem(index).getDataId() == track.getDataId()) {
                tvHasplay.setText(getString(R.string.hasplay, 100 * currPos / duration));
            }
        }
    }

    @Override
    public void onPlayStart() {

    }

    @Override
    public void onPlayPause() {

    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {

    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int i) {

    }

    @Override
    public void onPlayProgress(int i, int i1) {
        updatePlayStatus(i, i1);
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }


    class DownloadPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = null;
            switch (position) {
                case 0:
                    view = layoutDetail1;
                    break;
                case 1:
                    view = layoutDetail2;
                    break;
                case 2:
                    view = layoutDetail3;
                    break;
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    protected int onBindBarCenterStyle() {
        return BarStyle.CENTER_CUSTOME;
    }

    @Override
    protected View onBindBarCenterCustome() {
        magicIndicator = new MagicIndicator(mContext);
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.addView(magicIndicator);
        ViewGroup.LayoutParams layoutParams = magicIndicator.getLayoutParams();
        layoutParams.width = SizeUtils.dp2px(270);
        magicIndicator.setLayoutParams(layoutParams);

        return frameLayout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        XmDownloadManager.getInstance().removeDownloadStatueListener(this);
        XmPlayerManager.getInstance(mContext).removePlayerStatusListener(this);
    }
}
