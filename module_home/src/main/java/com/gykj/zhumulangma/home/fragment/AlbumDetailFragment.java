package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumTagAdapter;
import com.gykj.zhumulangma.home.adapter.AlbumTrackAdapter;
import com.gykj.zhumulangma.home.dialog.TrackPagerPopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AlbumDetailViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.library.flowlayout.FlowLayoutManager;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.AddDownloadException;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/8/14 13:41
 * Email: 1071931588@qq.com
 * Description:专辑详情页
 */
@Route(path = AppConstants.Router.Home.F_ALBUM_DETAIL)
public class AlbumDetailFragment extends BaseRefreshMvvmFragment<AlbumDetailViewModel, Track> implements
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener, TrackPagerPopup.onPopupDismissingListener {

    @Autowired(name = KeyCode.Home.ALBUMID)
    public long mAlbumId;
    private Album mAlbum;
    private String mSort = "time_desc";
    private Track mLastPlay;
    private AlbumTrackAdapter mAlbumTrackAdapter;
    private AlbumTagAdapter mAlbumTagAdapter;
    private TrackPagerPopup mPagerPopup;
    private XmPlayerManager mPlayerManager = XmPlayerManager.getInstance(mContext);

    private SmartRefreshLayout refreshLayout;
    private ViewGroup layoutDetail;
    private ViewGroup layoutTracks;
    private ImageView ivCover;
    private TextView tvAlbum;
    private TextView tvAuthor;
    private TextView tvPlaycount;
    private TextView tvTrackcount;
    private TextView tvSbcount;
    private TextView tvPlay;
    private TextView tvLastplay;



    public AlbumDetailFragment() {
    }

    @Override
    public void initView(View view) {
        String[] tabs = {"简介", "节目"};
        MagicIndicator magicIndicator = fd(R.id.magic_indicator);
        ViewPager viewpager = fd(R.id.viewpager);
        tvPlay = fd(R.id.tv_play);
        ivCover = fd(R.id.iv_cover);
        tvAlbum = fd(R.id.tv_album);
        tvAuthor = fd(R.id.tv_author);
        tvPlaycount = fd(R.id.tv_playcount);
        tvTrackcount = fd(R.id.tv_trackcount);
        tvSbcount = fd(R.id.tv_sbcount);
        tvLastplay = fd(R.id.tv_lastplay);
        layoutDetail = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.home_layout_album_detail, null);
        layoutTracks = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.home_layout_album_track, null);

        RecyclerView recyclerView = layoutTracks.findViewById(R.id.recyclerview);
        refreshLayout = layoutTracks.findViewById(R.id.refreshLayout);

        RecyclerView rvTag = layoutDetail.findViewById(R.id.rv_tag);
        rvTag.setLayoutManager(new FlowLayoutManager());
        rvTag.setHasFixedSize(true);
        mAlbumTagAdapter = new AlbumTagAdapter(R.layout.common_item_tag);
        mAlbumTagAdapter.bindToRecyclerView(rvTag);


        viewpager.setAdapter(new AlbumPagerAdapter());
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), viewpager, 125));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
        viewpager.setCurrentItem(1);

        mAlbumTrackAdapter = new AlbumTrackAdapter(R.layout.home_item_album_track);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        mAlbumTrackAdapter.bindToRecyclerView(recyclerView);
        mPagerPopup=new TrackPagerPopup(mContext,this);
        mPagerPopup.setDismissingListener(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        XmDownloadManager.getInstance().addDownloadStatueListener(mDownloadStatueListener);
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mAlbumTrackAdapter.setOnItemClickListener(this);
        mAlbumTrackAdapter.setOnItemChildClickListener(this);
        layoutDetail.findViewById(R.id.cl_announcer).setOnClickListener(this);
        layoutTracks.findViewById(R.id.ll_select).setOnClickListener(this);
        layoutTracks.findViewById(R.id.ll_download).setOnClickListener(this);
        addDisposable(RxView.clicks(layoutTracks.findViewById(R.id.ll_sort))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    if (mPagerPopup.isShow()) {
                        return;
                    }
                    mSort = "time_desc".equals(mSort) ? "time_asc" : "time_desc";
                    mViewModel.getTrackList(mSort);
                }));
        addDisposable(RxView.clicks(fd(R.id.ll_play))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    TextView tvPlay = fd(R.id.tv_play);
                    tvPlay.setText("继续播放");
                    if (mLastPlay != null) {
                        int index = mAlbumTrackAdapter.getData().indexOf(mLastPlay);
                        if (index != -1) {
                            mLastPlay = mAlbumTrackAdapter.getItem(index);
                            fd(R.id.gp_lastplay).setVisibility(View.VISIBLE);
                            tvLastplay.setText(getString(R.string.lastplay, mLastPlay.getTrackTitle()));
                            XmPlayerManager.getInstance(mContext).playList(mViewModel.getCommonTrackList(),
                                    index);
                            navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
                        } else {
                            mViewModel.getPlayTrackList();
                        }
                    } else {
                        mLastPlay = mAlbumTrackAdapter.getItem(0);
                        fd(R.id.gp_lastplay).setVisibility(View.VISIBLE);
                        tvLastplay.setText(getString(R.string.lastplay, mLastPlay.getTrackTitle()));
                        XmPlayerManager.getInstance(mContext).playList(mViewModel.getCommonTrackList(), 0);
                        navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
                    }
                }));


        addDisposable(RxView.clicks(fd(R.id.ll_subscribe))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> mViewModel.subscribe(mAlbum)));
        addDisposable(RxView.clicks(fd(R.id.ll_unsubscribe))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> mViewModel.unsubscribe(mAlbum)));
    }


    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout, mAlbumTrackAdapter);
    }

    @Override
    public void initData() {
        mViewModel.setAlbumId(mAlbumId);
        mViewModel.setSort(mSort);
        mViewModel.init();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getSubscribeEvent().observe(this, aBoolean -> {
            fd(R.id.ll_subscribe).setVisibility(aBoolean ? View.GONE : View.VISIBLE);
            fd(R.id.ll_unsubscribe).setVisibility(aBoolean ? View.VISIBLE : View.GONE);
        });

        mViewModel.getAlbumEvent().observe(this, album -> {
            mAlbum = album;
            Glide.with(mContext).load(mAlbum.getCoverUrlMiddle()).into(ivCover);
            tvAlbum.setText(mAlbum.getAlbumTitle());
            tvAuthor.setText((String.format(getResources().getString(R.string.zhubo),
                    mAlbum.getAnnouncer().getNickname())));
            tvPlaycount.setText(String.format(getResources().getString(R.string.ci)
                    , ZhumulangmaUtil.toWanYi(mAlbum.getPlayCount())));
            tvTrackcount.setText(String.format(getResources().getString(R.string.gong_ji),
                    mAlbum.getIncludeTrackCount()));
            tvSbcount.setText(String.format(getResources().getString(R.string.sb)
                    , ZhumulangmaUtil.toWanYi(mAlbum.getSubscribeCount())));

            Glide.with(this).load(album.getAnnouncer().getAvatarUrl()).into((ImageView) fd(R.id.iv_announcer_cover));
            ((TextView) layoutDetail.findViewById(R.id.tv_announcer_name)).setText(album.getAnnouncer().getNickname());
            ((TextView) layoutDetail.findViewById(R.id.tv_intro)).setText(album.getAlbumIntro());
            ((TextView) layoutDetail.findViewById(R.id.tv_announcer_name)).setText(album.getAnnouncer().getNickname());
            String vsignature = album.getAnnouncer().getVsignature();
            if (TextUtils.isEmpty(vsignature)) {
                layoutDetail.findViewById(R.id.tv_vsignature).setVisibility(View.GONE);
            } else {
                ((TextView) layoutDetail.findViewById(R.id.tv_vsignature)).setText(vsignature);
            }
            ((TextView) layoutDetail.findViewById(R.id.tv_following_count)).setText(getString(R.string.following_count,
                    ZhumulangmaUtil.toWanYi(album.getAnnouncer().getFollowingCount())));
            layoutDetail.findViewById(R.id.tv_vsignature).setVisibility(album.getAnnouncer().isVerified() ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(album.getAlbumTags())) {
                mAlbumTagAdapter.addData(Arrays.asList(album.getAlbumTags().split(",")));
            }

            setPager(album.getIncludeTrackCount());

        });
        mViewModel.getInitTracksEvent().observe(this, trackList -> mAlbumTrackAdapter.setNewData(trackList.getTracks()));

        mViewModel.getPlayTracksEvent().observe(this, tracks -> {
            setPager(tracks.getTotalCount());
            mAlbumTrackAdapter.setNewData(tracks.getTracks());
            XmPlayerManager.getInstance(mContext).playList(mViewModel.getCommonTrackList(),
                    mAlbumTrackAdapter.getData().indexOf(mLastPlay));
            navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
        });

        mViewModel.getTracksSortEvent().observe(this, tracks -> {
            if (CollectionUtils.isEmpty(tracks.getTracks())) {
                showEmptyView();
            } else {
                setPager(tracks.getTotalCount());
                mAlbumTrackAdapter.setNewData(tracks.getTracks());
            }
        });
        mViewModel.getLastplayEvent().observe(this, track -> {
            if (null != track) {
                tvPlay.setText("继续播放");
                mLastPlay = track;
                fd(R.id.gp_lastplay).setVisibility(View.VISIBLE);
                tvLastplay.setText(getString(R.string.lastplay, track.getTrackTitle()));
            }
        });
    }

    @Override
    protected void onRefreshSucc(List<Track> list) {
        mAlbumTrackAdapter.addData(0, list);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAlbumTrackAdapter) {
            mPlayerManager.playList(mViewModel.getCommonTrackList(), position);
            mLastPlay = mAlbumTrackAdapter.getItem(position);
            fd(R.id.gp_lastplay).setVisibility(View.VISIBLE);
            tvLastplay.setText(getString(R.string.lastplay, mAlbumTrackAdapter.getItem(position).getTrackTitle()));
            navigateTo(AppConstants.Router.Home.F_PLAY_TRACK);
        } else {
           mPagerPopup.dismissWith(()-> mViewModel.getTrackList(position + 1));
        }
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        int id = view.getId();
        if (R.id.iv_download == id) {
            XmDownloadManager.getInstance().downloadSingleTrack(
                    mAlbumTrackAdapter.getItem(position).getDataId(), new IDoSomethingProgress<AddDownloadException>() {
                        @Override
                        public void begin() {
                        }

                        @Override
                        public void success() {
                        }

                        @Override
                        public void fail(AddDownloadException e) {
                            if (e.getCode() == AddDownloadException.CODE_NULL) {
                                ToastUtil.showToast("参数不能为null");
                            } else if (e.getCode() == AddDownloadException.CODE_MAX_OVER) {
                                ToastUtil.showToast("批量下载个数超过最大值");
                            } else if (e.getCode() == AddDownloadException.CODE_NOT_FIND_TRACK) {
                                ToastUtil.showToast("不能找到相应的声音");
                            } else if (e.getCode() == AddDownloadException.CODE_MAX_DOWNLOADING_COUNT) {
                                ToastUtil.showToast("同时下载的音频个数不能超过500");
                            } else if (e.getCode() == AddDownloadException.CODE_DISK_OVER) {
                                ToastUtil.showToast("磁盘已满");
                            } else if (e.getCode() == AddDownloadException.CODE_MAX_SPACE_OVER) {
                                ToastUtil.showToast("下载的音频超过了设置的最大空间");
                            } else if (e.getCode() == AddDownloadException.CODE_NO_PAY_SOUND) {
                                ToastUtil.showToast("下载的付费音频中有没有支付");
                            }
                        }
                    });
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.ll_select == id || R.id.fl_mask == id) {
            switchPager();
        } else if (id == R.id.ll_download) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_BATCH_DOWNLOAD)
                    .withLong(KeyCode.Home.ALBUMID, mAlbumId)
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_BATCH_DOWNLOAD, (ISupportFragment) navigation)));
        } else if (id == R.id.cl_announcer) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
                    .withLong(KeyCode.Home.ANNOUNCER_ID, mAlbum.getAnnouncer().getAnnouncerId())
                    .withString(KeyCode.Home.ANNOUNCER_NAME, mAlbum.getAnnouncer().getNickname())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation)));
        }
    }

    @Override
    protected void onRevisible() {
        super.onRevisible();
        mAlbumTrackAdapter.notifyDataSetChanged();
    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_album_detail;
    }

    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"专辑详情"};
    }

    @Override
    protected Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_more, R.drawable.ic_common_share};
    }

    @Override
    public Class<AlbumDetailViewModel> onBindViewModel() {
        return AlbumDetailViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    /**
     * 设置分页
     *
     * @param totalcount
     */
    private void setPager(long totalcount) {
        int pagesize = 20;

        ((TextView) fd(R.id.tv_pagecount)).setText(getString(R.string.pagecount, totalcount));
        List<String> list = new ArrayList<>();
        if (mSort.equals("time_desc")) {
            for (int i = 0; i < totalcount / pagesize; i++) {
                list.add(totalcount - (i * pagesize) + "~" + (totalcount - ((i + 1) * pagesize) + 1));
            }
            if (totalcount % pagesize != 0) {
                list.add(totalcount - totalcount / pagesize * pagesize + "~1");
            }
        } else {
            for (int i = 0; i < totalcount / pagesize; i++) {
                list.add((i * pagesize + 1) + "~" + ((i + 1) * pagesize));
            }
            if (totalcount % pagesize != 0) {
                list.add((totalcount / pagesize * pagesize + 1) + "~" + totalcount);
            }

        }
        mPagerPopup.getPagerAdapter().setNewData(list);
    }

    /**
     * 更新下载状态
     *
     * @param track
     */
    private void updateDownloadStatus(Track track) {
        List<Track> tracks = mAlbumTrackAdapter.getData();
        int index = mAlbumTrackAdapter.getData().indexOf(track);

        if (index != -1) {
            DownloadState downloadStatus = XmDownloadManager.getInstance()
                    .getSingleTrackDownloadStatus(tracks.get(index).getDataId());
            View ivDownload = mAlbumTrackAdapter.getViewByPosition(index, R.id.iv_download);
            View progress = mAlbumTrackAdapter.getViewByPosition(index, R.id.progressBar);
            View ivDownloadSucc = mAlbumTrackAdapter.getViewByPosition(index, R.id.iv_downloadsucc);
            if (ivDownload == null || progress == null || ivDownloadSucc == null) {
                return;
            }
            switch (downloadStatus) {
                case FINISHED:
                    ivDownloadSucc.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    ivDownload.setVisibility(View.GONE);
                    break;
                case STARTED:
                case WAITING:
                    ivDownloadSucc.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                    ivDownload.setVisibility(View.GONE);
                    break;
                case STOPPED:
                case NOADD:
                case ERROR:
                    ivDownloadSucc.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);
                    ivDownload.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    /**
     * 更新播放状态
     */
    private void updatePlayStatus() {
        if (mPlayerManager.getCurrSound().getKind() != PlayableModel.KIND_TRACK) {
            return;
        }
        Track track = mPlayerManager.getCurrSoundIgnoreKind(true);
        if (null == track) {
            return;
        }
        List<Track> tracks = mAlbumTrackAdapter.getData();

        if (mAlbumId == track.getAlbum().getAlbumId()) {
            mLastPlay = track;
            fd(R.id.gp_lastplay).setVisibility(View.VISIBLE);
            tvLastplay.setText(getString(R.string.lastplay, mLastPlay.getTrackTitle()));
        }
        for (int i = 0; i < tracks.size(); i++) {
            LottieAnimationView lavPlaying = (LottieAnimationView) mAlbumTrackAdapter
                    .getViewByPosition(i, R.id.lav_playing);

            if (null != lavPlaying) {
                if (tracks.get(i).getDataId() == track.getDataId()) {
                    lavPlaying.setVisibility(View.VISIBLE);
                    if (XmPlayerManager.getInstance(mContext).isPlaying()) {
                        lavPlaying.playAnimation();
                    } else {
                        lavPlaying.pauseAnimation();
                    }
                } else {
                    lavPlaying.cancelAnimation();
                    lavPlaying.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 更新播放进度
     */
    private void updatePlayStatus(int currPos, int duration) {
        Track track = mPlayerManager.getCurrSoundIgnoreKind(true);
        if (null == track) {
            return;
        }
        int index = mAlbumTrackAdapter.getData().indexOf(track);
        if (index != -1) {
            TextView tvHasplay = (TextView) mAlbumTrackAdapter.getViewByPosition(index, R.id.tv_hasplay);
            if (null != tvHasplay && mAlbumTrackAdapter.getItem(index).getDataId() == track.getDataId()) {
                tvHasplay.setText(getString(R.string.hasplay, 100 * currPos / duration));
            }
        }
    }

    /**
     * 切换分页界面是否显示
     */
    private void switchPager() {

        if(mPagerPopup.isShow()){
            mPagerPopup.dismiss();
        }else {
            fd(R.id.iv_select_page).animate().rotation(-90).setDuration(200);
            new XPopup.Builder(mContext).atView(layoutTracks.findViewById(R.id.cl_actionbar)).setPopupCallback(new SimpleCallback(){
                @Override
                public void onCreated() {
                    super.onCreated();
                    mPagerPopup.getRvPager().setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            changePageStatus();
                        }
                    });
                }

                @Override
                public void beforeShow() {
                    super.beforeShow();
                    changePageStatus();
                }
            }).popupPosition(PopupPosition.Bottom)
                    .asCustom(mPagerPopup).show();
        }
    /*    if (flMask.getVisibility() == View.VISIBLE) {
            rvPager.animate().translationY(-rvPager.getHeight()).setDuration(200).withEndAction(() -> {
                flMask.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            });
            fd(R.id.iv_select_page).animate().rotationBy(180).setDuration(200);
        } else {
            refreshLayout.setVisibility(View.GONE);
            flMask.setVisibility(View.VISIBLE);
            rvPager.setTranslationY(-rvPager.getHeight() == 0 ? -400 : -rvPager.getHeight());
            rvPager.animate().translationY(0).setDuration(200);
            fd(R.id.iv_select_page).animate().rotationBy(180).setDuration(200);
            new Handler().postDelayed(() -> changePageStatus(), 200);
        }*/
    }

    /**
     * 改变分页选中状态
     */
    private void changePageStatus() {
        for (int i = 0; i < mPagerPopup.getPagerAdapter().getData().size(); i++) {
            TextView viewByPosition = (TextView) mPagerPopup.getPagerAdapter().getViewByPosition(i, R.id.tv_page);
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

    /**
     * 下载状态监听
     */
    private IXmDownloadTrackCallBack mDownloadStatueListener = new IXmDownloadTrackCallBack() {

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
            updateDownloadStatus(track);
        }

        @Override
        public void onError(Track track, Throwable throwable) {
            updateDownloadStatus(track);
            throwable.printStackTrace();
        }

        @Override
        public void onCancelled(Track track, Callback.CancelledException e) {
            updateDownloadStatus(track);
        }

        @Override
        public void onProgress(Track track, long l, long l1) {

        }

        @Override
        public void onRemoved() {
        }
    };
    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onPlayStart() {
            updatePlayStatus();
        }

        @Override
        public void onPlayPause() {
            updatePlayStatus();
        }

        @Override
        public void onPlayStop() {
            updatePlayStatus();
        }

        @Override
        public void onSoundPlayComplete() {
            updatePlayStatus();
        }

        @Override
        public void onSoundPrepared() {
            updatePlayStatus();
        }

        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
            updatePlayStatus();

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        XmDownloadManager.getInstance().removeDownloadStatueListener(mDownloadStatueListener);
        mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
    }

    @Override
    public void onDismissing() {
        fd(R.id.iv_select_page).animate().rotation(90).setDuration(200);
    }

    class AlbumPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 2;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = position == 0 ? layoutDetail : layoutTracks;
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

}
