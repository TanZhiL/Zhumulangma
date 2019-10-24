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
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.common.util.SystemUtil;
import com.gykj.zhumulangma.common.widget.CircleProgressBar;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.DownloadAlbumAdapter;
import com.gykj.zhumulangma.listen.adapter.DownloadTrackAdapter;
import com.gykj.zhumulangma.listen.adapter.DownloadingAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.DownloadViewModel;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.ComparatorUtil;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.BaseRuntimeException;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:下载页
 */
@Route(path = AppConstants.Router.Listen.F_DOWNLOAD)
public class DownloadFragment extends BaseMvvmFragment<DownloadViewModel> implements
        BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    @Autowired(name = KeyCode.Listen.TAB_INDEX)
    public int mTabIndex;

    private DownloadAlbumAdapter mAlbumAdapter;
    private DownloadTrackAdapter mTrackAdapter;
    private DownloadingAdapter mDownloadingAdapter;
    private Handler mHandler = new Handler();

    private TextView tvMemory;
    private MagicIndicator magicIndicator;
    private ViewGroup layoutDetail1, layoutDetail2, layoutDetail3;
    private XmDownloadManager mDownloadManager=XmDownloadManager.getInstance();


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
        layoutDetail1 = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.listen_layout_download_album, null);
        layoutDetail2 = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.listen_layout_download_track, null);
        layoutDetail3 = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.listen_layout_downloading, null);


        RecyclerView rvAlbum = layoutDetail1.findViewById(R.id.recyclerview);
        rvAlbum.setHasFixedSize(true);
        rvAlbum.setLayoutManager(new LinearLayoutManager(mActivity));
        mAlbumAdapter = new DownloadAlbumAdapter(R.layout.listen_item_download_album);
        mAlbumAdapter.bindToRecyclerView(rvAlbum);
        mAlbumAdapter.setEmptyView(R.layout.common_layout_empty);

        RecyclerView rvTrack = layoutDetail2.findViewById(R.id.recyclerview);
        rvTrack.setHasFixedSize(true);
        rvTrack.setLayoutManager(new LinearLayoutManager(mActivity));
        mTrackAdapter = new DownloadTrackAdapter(R.layout.listen_item_download_track);
        mTrackAdapter.bindToRecyclerView(rvTrack);
        mTrackAdapter.setEmptyView(R.layout.common_layout_empty);

        RecyclerView rvRecommend = layoutDetail3.findViewById(R.id.recyclerview);
        rvRecommend.setHasFixedSize(true);
        rvRecommend.setLayoutManager(new LinearLayoutManager(mActivity));
        mDownloadingAdapter = new DownloadingAdapter(R.layout.listen_item_downloading);
        mDownloadingAdapter.bindToRecyclerView(rvRecommend);
        mDownloadingAdapter.setEmptyView(R.layout.common_layout_empty);

        tvMemory = fd(R.id.tv_memory);
        ViewPager viewpager = fd(R.id.viewpager);
        viewpager.setAdapter(new DownloadPagerAdapter());
        final CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), viewpager, 50));
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
        viewpager.setCurrentItem(mTabIndex);

        if (!mDownloadManager.haveDowningTask()) {
            ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部开始");
            ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_download);
        } else {
            ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部暂停");
            ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_pause);
        }
        ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
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


        layoutDetail2.findViewById(R.id.tv_sort).setOnClickListener(this);
        layoutDetail2.findViewById(R.id.iv_sort).setOnClickListener(this);
        layoutDetail2.findViewById(R.id.tv_delete).setOnClickListener(this);
        layoutDetail2.findViewById(R.id.iv_delete).setOnClickListener(this);

        layoutDetail3.findViewById(R.id.tv_all).setOnClickListener(this);
        layoutDetail3.findViewById(R.id.iv_all).setOnClickListener(this);
        layoutDetail3.findViewById(R.id.tv_clear).setOnClickListener(this);
        layoutDetail3.findViewById(R.id.tv_clear).setOnClickListener(this);

        mDownloadManager.addDownloadStatueListener(downloadStatueListener);
        XmPlayerManager.getInstance(mActivity).addPlayerStatusListener(playerStatusListener);
    }

    @Override
    public void initData() {
        tvMemory.setText(getString(R.string.memory,
                mDownloadManager.getHumanReadableDownloadOccupation(IDownloadManager.Auto),
                SystemUtil.getRomTotalSize(mActivity)));
        mAlbumAdapter.setNewData(mDownloadManager.getDownloadAlbums(true));

        List<Track> downloadTracks = mDownloadManager.getDownloadTracks(true);
        Collections.sort(downloadTracks, ComparatorUtil.comparatorByUserSort(true));

        mTrackAdapter.setNewData(downloadTracks);
        mDownloadingAdapter.setNewData(mDownloadManager.getDownloadTracks(false));
        if (mDownloadManager.getDownloadTracks(false).size() > 0) {
            layoutDetail3.findViewById(R.id.cl_action).setVisibility(View.VISIBLE);
        }
        if (mDownloadManager.getDownloadTracks(true).size() > 0) {
            layoutDetail2.findViewById(R.id.cl_action).setVisibility(View.VISIBLE);
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
                mDownloadManager.clearDownloadedAlbum(mAlbumAdapter.getItem(position).getAlbumId(), null);
                mAlbumAdapter.remove(position);
            }
        } else if (adapter == mTrackAdapter) {
            if (id == R.id.ll_delete) {
                mDownloadManager.clearDownloadedTrack(mTrackAdapter.getItem(position).getDataId());
                mTrackAdapter.remove(position);
                mHandler.postDelayed(()-> mAlbumAdapter.setNewData(mDownloadManager.getDownloadAlbums(true)),100);

            }
        } else if (adapter == mDownloadingAdapter) {
            if (id == R.id.ll_delete) {
                try {
                    mDownloadManager.cancelDownloadSingleTrack(mDownloadingAdapter.getItem(position).getDataId());
                    mDownloadingAdapter.remove(position);
                    if (mDownloadingAdapter.getData().size() == 0) {
                        layoutDetail3.findViewById(R.id.cl_action).setVisibility(View.GONE);
                    } else {
                        mHandler.postDelayed(() -> {
                            if (!mDownloadManager.haveDowningTask()) {
                                ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部开始");
                                ((ImageView)
                                        layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_download);
                            } else {
                                ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部暂停");
                                ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_pause);
                            }
                            ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
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


    private void updateDownloadStatus(Track track) {
        int index = mDownloadingAdapter.getData().indexOf(track);
        if (index != -1) {
            ImageView ivStatus = (ImageView) mDownloadingAdapter.getViewByPosition(index, R.id.iv_status);
            TextView tvStatus = (TextView) mDownloadingAdapter.getViewByPosition(index, R.id.tv_status);
            CircleProgressBar progressBar = (CircleProgressBar) mDownloadingAdapter.getViewByPosition(index, R.id.cpb_progress);
            if (ivStatus != null && tvStatus != null && progressBar != null) {
                DownloadState downloadStatus = mDownloadManager
                        .getSingleTrackDownloadStatus(track.getDataId());
                if (downloadStatus == DownloadState.WAITING) {
                    if (mDownloadManager.haveDowningTask()) {
                        ivStatus.setImageResource(R.drawable.ic_listen_waiting);
                        tvStatus.setText("待下载");
                        tvStatus.setTextColor(getResources().getColor(R.color.colorGray));
                        progressBar.setSecondColor(mActivity.getResources().getColor(R.color.colorGray));
                    } else {
                        ivStatus.setImageResource(R.drawable.ic_listen_pause);
                        tvStatus.setText("下载中");
                        tvStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                        progressBar.setSecondColor(mActivity.getResources().getColor(R.color.colorPrimary));
                    }
                } else if (downloadStatus == DownloadState.STARTED) {
                    ivStatus.setImageResource(R.drawable.ic_listen_pause);
                    tvStatus.setText("下载中");
                    tvStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                    progressBar.setSecondColor(mActivity.getResources().getColor(R.color.colorPrimary));
                } else if (downloadStatus == DownloadState.STOPPED) {
                    ivStatus.setImageResource(R.drawable.ic_listen_download);
                    tvStatus.setText("已暂停");
                    tvStatus.setTextColor(getResources().getColor(R.color.colorGray));
                    progressBar.setSecondColor(mActivity.getResources().getColor(R.color.colorGray));
                }
            }else {
                mDownloadingAdapter.notifyItemChanged(index);
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mDownloadingAdapter) {

            try {
                if (mDownloadManager.getSingleTrackDownloadStatus(
                        mDownloadingAdapter.getItem(position).getDataId()) != DownloadState.STARTED) {
                    mDownloadManager.resumeDownloadSingleTrack(mDownloadingAdapter.getItem(position).getDataId());
                } else {
                    mDownloadManager.pauseDownloadSingleTrack(mDownloadingAdapter.getItem(position).getDataId());
                }

                mHandler.postDelayed(() -> {
                    if (!mDownloadManager.haveDowningTask()) {
                        ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部开始");
                        ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_download);
                    } else {
                        ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部暂停");
                        ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_pause);
                    }
                    ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
                }, 200);

            } catch (Exception e) {
                //当下载完成的瞬间,会被移除,造成越界异常
                e.printStackTrace();
            }
        } else if (adapter == mTrackAdapter) {
            XmPlayerManager.getInstance(mActivity).playList(mTrackAdapter.getData(), position);
            RouteUtil.navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
        } else if (adapter == mAlbumAdapter) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Listen.F_DOWNLOAD_ALBUM)
                    .withLong(KeyCode.Listen.ALBUMID, mAlbumAdapter.getItem(position).getAlbumId())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Listen.F_DOWNLOAD_ALBUM, (ISupportFragment) navigation)));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_all || id == R.id.iv_all) {
            if (mDownloadManager.haveDowningTask()) {
                mDownloadManager.pauseAllDownloads(null);
                ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部开始");
                ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_download);
            } else {
                mDownloadManager.resumeAllDownloads(null);
                ((TextView) layoutDetail3.findViewById(R.id.tv_all)).setText("全部暂停");
                ((ImageView) layoutDetail3.findViewById(R.id.iv_all)).setImageResource(R.drawable.ic_listen_pause);
            }
        } else if (id == R.id.iv_clear || id == R.id.tv_clear) {
            mDownloadManager.cancelAllDownloads(new IDoSomethingProgress() {
                @Override
                public void begin() {
                }

                @Override
                public void success() {
                    mDownloadingAdapter.getData().clear();
                    layoutDetail3.findViewById(R.id.cl_action).setVisibility(View.GONE);
                    ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
                }

                @Override
                public void fail(BaseRuntimeException e) {
                    Log.d(TAG, "fail() called with: e = [" + e + "]");
                }
            });

        } else if (id == R.id.tv_sort || id == R.id.iv_sort) {
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_DOWNLOAD_SORT);
        } else if (id == R.id.tv_delete || id == R.id.iv_delete) {
            RouteUtil.navigateTo(AppConstants.Router.Listen.F_DOWNLOAD_DELETE);
        }
    }

    private void updatePlayStatus(int currPos, int duration) {
        Track track = XmPlayerManager.getInstance(mActivity).getCurrSoundIgnoreKind(true);
        if (null == track) {
            return;
        }
        int index = mTrackAdapter.getData().indexOf(track);
        if (index != -1) {
            TextView tvHasplay = (TextView) mTrackAdapter.getViewByPosition(index, R.id.tv_hasplay);
            if (null != tvHasplay && mTrackAdapter.getItem(index).getDataId() == track.getDataId()) {
                tvHasplay.setText(getString(R.string.hasplay, 100 * currPos / duration));
            }else {
                mTrackAdapter.notifyItemChanged(index);
            }
        }
    }

    @Override
    public  void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Listen.DOWNLOAD_SORT:
            case EventCode.Listen.DOWNLOAD_DELETE:
                List<Track> downloadTracks = mDownloadManager.getDownloadTracks(true);
                Collections.sort(downloadTracks, ComparatorUtil.comparatorByUserSort(true));
                mTrackAdapter.setNewData(downloadTracks);
                mHandler.postDelayed(()-> mAlbumAdapter.setNewData(mDownloadManager.getDownloadAlbums(true)),100);
                if (downloadTracks.size() == 0) {
                    layoutDetail2.findViewById(R.id.cl_action).setVisibility(View.GONE);
                }
                break;
        }
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
    public int onBindBarCenterStyle() {
        return SimpleBarStyle.CENTER_CUSTOME;
    }

    @Override
    public View onBindBarCenterCustome() {
        magicIndicator = new MagicIndicator(mActivity);
        FrameLayout frameLayout = new FrameLayout(mActivity);
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
        mDownloadManager.removeDownloadStatueListener(downloadStatueListener);
        XmPlayerManager.getInstance(mActivity).removePlayerStatusListener(playerStatusListener);
    }

    private IXmPlayerStatusListener playerStatusListener = new IXmPlayerStatusListener() {
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

    };
    private IXmDownloadTrackCallBack downloadStatueListener=new IXmDownloadTrackCallBack() {
        @Override
        public void onWaiting(Track track) {
            updateDownloadStatus(track);
        }

        @Override
        public void onStarted(Track track) {
            updateDownloadStatus(track);
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
            ((TextView) layoutDetail3.findViewById(R.id.tv_count)).setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
            mAlbumAdapter.setNewData(mDownloadManager.getDownloadAlbums(true));
            mTrackAdapter.addData(track);
            if (mDownloadManager.getDownloadTracks(false).size() > 0) {
                layoutDetail3.findViewById(R.id.cl_action).setVisibility(View.VISIBLE);
            }
            if (mDownloadManager.getDownloadTracks(true).size() > 0) {
                layoutDetail2.findViewById(R.id.cl_action).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError(Track track, Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onCancelled(Track track, Callback.CancelledException e) {
            updateDownloadStatus(track);
        }

        @Override
        public void onProgress(Track track, long l, long l1) {
            updateDownloadStatus(track);
            int index = mDownloadingAdapter.getData().indexOf(track);
            if (index != -1) {
                CircleProgressBar progressBar = (CircleProgressBar) mDownloadingAdapter.getViewByPosition(index, R.id.cpb_progress);
                if (progressBar != null) {
                    progressBar.setSecondColor(mActivity.getResources().getColor(R.color.colorPrimary));
                    progressBar.setProgress((int) (l1 * 100 / l));
                }else {
                    mDownloadingAdapter.notifyItemChanged(index);
                }
            }

        }

        @Override
        public void onRemoved() {
        }
    };

}
