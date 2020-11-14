package com.gykj.zhumulangma.listen.fragment;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TPagerAdapter;
import com.gykj.zhumulangma.common.extra.TViewPagerHelper;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.common.util.SystemUtil;
import com.gykj.zhumulangma.common.widget.CircleProgressBar;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.DownloadAlbumAdapter;
import com.gykj.zhumulangma.listen.adapter.DownloadTrackAdapter;
import com.gykj.zhumulangma.listen.adapter.DownloadingAdapter;
import com.gykj.zhumulangma.listen.databinding.ListenFragmentDownloadBinding;
import com.gykj.zhumulangma.listen.databinding.ListenLayoutDownloadAlbumBinding;
import com.gykj.zhumulangma.listen.databinding.ListenLayoutDownloadTrackBinding;
import com.gykj.zhumulangma.listen.databinding.ListenLayoutDownloadingBinding;
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
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:下载页
 */
@Route(path = Constants.Router.Listen.F_DOWNLOAD)
public class DownloadFragment extends BaseMvvmFragment<ListenFragmentDownloadBinding, DownloadViewModel>
        implements BaseQuickAdapter.OnItemChildClickListener,BaseQuickAdapter.OnItemClickListener,
        View.OnClickListener {
    @Autowired(name = KeyCode.Listen.TAB_INDEX)
    public int mTabIndex;

    private DownloadAlbumAdapter mAlbumAdapter;
    private DownloadTrackAdapter mTrackAdapter;
    private DownloadingAdapter mDownloadingAdapter;

    private MagicIndicator magicIndicator;
    private ListenLayoutDownloadAlbumBinding mAlbumBind;
    private ListenLayoutDownloadTrackBinding mTrackBind;
    private ListenLayoutDownloadingBinding mDownloadingBind;

    private XmDownloadManager mDownloadManager = XmDownloadManager.getInstance();


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
    protected void initView() {
        String[] tabs = {"专辑", "声音", "下载中"};
        mAlbumBind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.listen_layout_download_album, null, false);
        mTrackBind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.listen_layout_download_track, null, false);
        mDownloadingBind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.listen_layout_downloading, null, false);


        mAlbumBind.recyclerview.setHasFixedSize(true);
        mAlbumBind.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mAlbumAdapter = new DownloadAlbumAdapter(R.layout.listen_item_download_album);
        mAlbumAdapter.bindToRecyclerView(mAlbumBind.recyclerview);
        mAlbumAdapter.setEmptyView(R.layout.common_layout_empty);


        mTrackBind.recyclerview.setHasFixedSize(true);
        mTrackBind.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mTrackAdapter = new DownloadTrackAdapter(R.layout.listen_item_download_track);
        mTrackAdapter.bindToRecyclerView(mTrackBind.recyclerview);
        mTrackAdapter.setEmptyView(R.layout.common_layout_empty);


        mDownloadingBind.recyclerview.setHasFixedSize(true);
        mDownloadingBind.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mDownloadingAdapter = new DownloadingAdapter(R.layout.listen_item_downloading);
        mDownloadingAdapter.bindToRecyclerView(mDownloadingBind.recyclerview);
        mDownloadingAdapter.setEmptyView(R.layout.common_layout_empty);

        mBinding.viewpager.setAdapter(new TPagerAdapter(
                mAlbumBind.getRoot(),mTrackBind.getRoot(),mDownloadingBind.getRoot()));
        final CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), mBinding.viewpager, 50));
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        TViewPagerHelper.bind(magicIndicator, mBinding.viewpager);
        //viewpager2的坑,在尚未绘制完成前只能使用以下方式代替mBinding.viewpager.setCurrentItem(mTabIndex);
        RecyclerView recyclerView = (RecyclerView)mBinding.viewpager.getChildAt(0);
        recyclerView.scrollToPosition(mTabIndex);

        if (!mDownloadManager.haveDowningTask()) {
            mDownloadingBind.tvAll.setText("全部开始");
            mDownloadingBind.ivAll.setImageResource(R.drawable.ic_listen_download);
        } else {
            mDownloadingBind.tvAll.setText("全部暂停");
            mDownloadingBind.ivAll.setImageResource(R.drawable.ic_listen_pause);
        }
        mDownloadingBind.tvCount.setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
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


        mTrackBind.tvSort.setOnClickListener(this);
        mTrackBind.ivSort.setOnClickListener(this);
        mTrackBind.tvDelete.setOnClickListener(this);
        mTrackBind.ivDelete.setOnClickListener(this);

        mDownloadingBind.tvAll.setOnClickListener(this);
        mDownloadingBind.ivAll.setOnClickListener(this);
        mDownloadingBind.tvClear.setOnClickListener(this);
        mDownloadingBind.ivClear.setOnClickListener(this);

        mDownloadManager.addDownloadStatueListener(downloadStatueListener);
        XmPlayerManager.getInstance(mActivity).addPlayerStatusListener(playerStatusListener);
    }

    @Override
    public void initData() {
        mBinding.tvMemory.setText(getString(R.string.memory,
                mDownloadManager.getHumanReadableDownloadOccupation(IDownloadManager.Auto),
                SystemUtil.getRomTotalSize(mActivity)));
        mAlbumAdapter.setNewData(mDownloadManager.getDownloadAlbums(true));

        mViewModel.getDownloadTracks();

        mDownloadingAdapter.setNewData(mDownloadManager.getDownloadTracks(false));
        if (mDownloadManager.getDownloadTracks(false).size() > 0) {
            mDownloadingBind.clAction.setVisibility(View.VISIBLE);
        }
        if (mDownloadManager.getDownloadTracks(true).size() > 0) {
            mTrackBind.clAction.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void initViewObservable() {
        mViewModel.getTracksEvent().observe(this, tracks -> mTrackAdapter.setNewData(tracks));
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
                mHandler.postDelayed(() -> mAlbumAdapter.setNewData(mDownloadManager.getDownloadAlbums(true)), 100);

            }
        } else if (adapter == mDownloadingAdapter) {
            if (id == R.id.ll_delete) {
                try {
                    mDownloadManager.cancelDownloadSingleTrack(mDownloadingAdapter.getItem(position).getDataId());
                    mDownloadingAdapter.remove(position);
                    if (mDownloadingAdapter.getData().size() == 0) {
                        mDownloadingBind.clAction.setVisibility(View.GONE);
                    } else {
                        mHandler.postDelayed(() -> {
                            if (!mDownloadManager.haveDowningTask()) {
                                mDownloadingBind.tvAll.setText("全部开始");
                                mDownloadingBind.ivAll.setImageResource(R.drawable.ic_listen_download);
                            } else {
                                mDownloadingBind.tvAll.setText("全部暂停");
                                mDownloadingBind.ivAll.setImageResource(R.drawable.ic_listen_pause);
                            }
                            mDownloadingBind.tvCount.setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
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
            } else {
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
                        mDownloadingBind.tvAll.setText("全部开始");
                        mDownloadingBind.ivAll.setImageResource(R.drawable.ic_listen_download);
                    } else {
                        mDownloadingBind.tvAll.setText("全部暂停");
                        mDownloadingBind.ivAll.setImageResource(R.drawable.ic_listen_pause);
                    }
                    mDownloadingBind.tvCount.setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
                }, 200);

            } catch (Exception e) {
                //当下载完成的瞬间,会被移除,造成越界异常
                e.printStackTrace();
            }
        } else if (adapter == mTrackAdapter) {
            XmPlayerManager.getInstance(mActivity).playList(mTrackAdapter.getData(), position);
            RouterUtil.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
        } else if (adapter == mAlbumAdapter) {
            RouterUtil.navigateTo(mRouter.build(Constants.Router.Listen.F_DOWNLOAD_ALBUM)
                    .withLong(KeyCode.Listen.ALBUMID, mAlbumAdapter.getItem(position).getAlbumId()));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_all || id == R.id.iv_all) {
            if (mDownloadManager.haveDowningTask()) {
                mDownloadManager.pauseAllDownloads(null);
                mDownloadingBind.tvAll.setText("全部开始");
                mDownloadingBind.ivAll.setImageResource(R.drawable.ic_listen_download);
            } else {
                mDownloadManager.resumeAllDownloads(null);
                mDownloadingBind.tvAll.setText("全部暂停");
                mDownloadingBind.ivAll.setImageResource(R.drawable.ic_listen_pause);
            }
        } else if (id == R.id.iv_clear || id == R.id.tv_clear) {
            mDownloadManager.cancelAllDownloads(new IDoSomethingProgress() {
                @Override
                public void begin() {
                }

                @Override
                public void success() {
                    mDownloadingAdapter.getData().clear();
                    mDownloadingBind.clAction.setVisibility(View.GONE);
                    mDownloadingBind.tvCount.setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
                }

                @Override
                public void fail(BaseRuntimeException e) {
                    Log.d(TAG, "fail() called with: e = [" + e + "]");
                }
            });

        } else if (id == R.id.tv_sort || id == R.id.iv_sort) {
            RouterUtil.navigateTo(Constants.Router.Listen.F_DOWNLOAD_SORT);
        } else if (id == R.id.tv_delete || id == R.id.iv_delete) {
            RouterUtil.navigateTo(Constants.Router.Listen.F_DOWNLOAD_DELETE);
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
            } else {
                mTrackAdapter.notifyItemChanged(index);
            }
        }
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Listen.DOWNLOAD_SORT:
            case EventCode.Listen.DOWNLOAD_DELETE:
                List<Track> downloadTracks = mDownloadManager.getDownloadTracks(true);
                Collections.sort(downloadTracks, ComparatorUtil.comparatorByUserSort(true));
                mTrackAdapter.setNewData(downloadTracks);
                mHandler.postDelayed(() -> mAlbumAdapter.setNewData(mDownloadManager.getDownloadAlbums(true)), 100);
                if (downloadTracks.size() == 0) {
                    mTrackBind.clAction.setVisibility(View.GONE);
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
                    view = mAlbumBind.getRoot();
                    break;
                case 1:
                    view = mTrackBind.getRoot();
                    break;
                case 2:
                    view = mDownloadingBind.getRoot();
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
    public SimpleBarStyle onBindBarCenterStyle() {
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
    private IXmDownloadTrackCallBack downloadStatueListener = new IXmDownloadTrackCallBack() {
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
                    mDownloadingBind.clAction.setVisibility(View.GONE);
                }
            }
            mDownloadingBind.tvCount.setText("(" + mDownloadManager.getDownloadTrackCount(false) + ")");
            mAlbumAdapter.setNewData(mDownloadManager.getDownloadAlbums(true));
            mTrackAdapter.addData(track);
            if (mDownloadManager.getDownloadTracks(false).size() > 0) {
                mDownloadingBind.clAction.setVisibility(View.VISIBLE);
            }
            if (mDownloadManager.getDownloadTracks(true).size() > 0) {
                mTrackBind.clAction.setVisibility(View.VISIBLE);
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
                } else {
                    mDownloadingAdapter.notifyItemChanged(index);
                }
            }

        }

        @Override
        public void onRemoved() {
        }
    };

}
