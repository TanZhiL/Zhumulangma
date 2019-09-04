package com.gykj.zhumulangma.home.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.gykj.zhumulangma.common.widget.TScrollView;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.dialog.PlayListPopup;
import com.gykj.zhumulangma.home.dialog.PlaySchedulePopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayTrackViewModel;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.ninetripods.sydialoglib.SYDialog;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmDataCallback;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.yokeyword.fragmentation.ISupportFragment;

import static com.lxj.xpopup.enums.PopupAnimation.TranslateAlphaFromBottom;
import static com.lxj.xpopup.enums.PopupAnimation.TranslateFromBottom;

@Route(path = AppConstants.Router.Home.F_PLAY_TRACK)
public class PlayTrackFragment extends BaseMvvmFragment<PlayTrackViewModel> implements
        TScrollView.OnScrollListener, View.OnClickListener, IXmPlayerStatusListener,
        BaseQuickAdapter.OnItemClickListener, IXmAdsStatusListener, OnSeekChangeListener,
        PlaySchedulePopup.onSelectedListener, PlayListPopup.onActionListener, IXmDownloadTrackCallBack, IXmDataCallback {

    private TScrollView msv;
    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private View c;

    private ImageView whiteLeft;
    private ImageView whiteRight1;
    private ImageView whiteRight2;
    private TextView tvSchedule;
    private ImageView transLeft;
    private ImageView transRight1;
    private ImageView transRight2;
    private IndicatorSeekBar isbProgress;
    private LottieAnimationView lavPlayPause;
    private LottieAnimationView lavBuffering;
    private ImageView ivBg;
    private RecyclerView rvRelative;
    private AlbumAdapter mAlbumAdapter;
    private Track mTrack;
    private boolean isPlaying;

    private Handler mHandler;
    private LottieAnimationView lavPlayNext;
    private LottieAnimationView lavPlayPre;
    private PlaySchedulePopup mSchedulePopup;
    private PlayListPopup mPlayListPopup;

    private boolean isUp;
    private XmPlayerManager mPlayerManager = XmPlayerManager.getInstance(mContext);

    public PlayTrackFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_play_track;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        msv = fd(R.id.msv);
        ctbTrans = fd(R.id.ctb_trans);
        ctbWhite = fd(R.id.ctb_white);
        ivBg = fd(R.id.iv_bg);
        isbProgress = fd(R.id.ib_progress);
        c = fd(R.id.c);
        tvSchedule = fd(R.id.tv_schedule);
        lavPlayPause = fd(R.id.lav_play_pause);
        lavBuffering = fd(R.id.lav_buffering);
        rvRelative = fd(R.id.rv_relative);
        lavPlayNext = fd(R.id.lav_next);
        lavPlayPre = fd(R.id.lav_pre);
        rvRelative.setLayoutManager(new LinearLayoutManager(mContext));
        mAlbumAdapter = new AlbumAdapter(R.layout.home_item_album);
        mAlbumAdapter.bindToRecyclerView(rvRelative);
        initBar();
        new Handler().postDelayed(() -> {
            if (mPlayerManager.isPlaying()) {
                if (mPlayerManager.isAdPlaying()) {
                    bufferingAnim();
                } else {
                    playingAnim();
                }
            }
        }, 100);
        mSchedulePopup = new PlaySchedulePopup(mContext, this);
        mPlayListPopup = new PlayListPopup(mContext, this);
    }


    private void initBar() {

        transLeft = ctbTrans.getLeftCustomView().findViewById(R.id.iv_left);
        transRight1 = ctbTrans.getRightCustomView().findViewById(R.id.iv1_right);
        transRight2 = ctbTrans.getRightCustomView().findViewById(R.id.iv2_right);


        transLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transLeft.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transLeft.setRotation(-90);
        transLeft.setVisibility(View.VISIBLE);

        transRight1.setImageResource(R.drawable.ic_common_more);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transRight1.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transRight1.setVisibility(View.VISIBLE);

        transRight2.setImageResource(R.drawable.ic_common_share);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transRight2.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transRight2.setVisibility(View.VISIBLE);

        whiteLeft = ctbWhite.getLeftCustomView().findViewById(R.id.iv_left);
        whiteRight1 = ctbWhite.getRightCustomView().findViewById(R.id.iv1_right);
        whiteRight2 = ctbWhite.getRightCustomView().findViewById(R.id.iv2_right);
        TextView tvTitle = ctbWhite.getCenterCustomView().findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("歌曲详情");

        whiteLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        whiteLeft.setVisibility(View.VISIBLE);
        whiteLeft.setRotation(-90);
        whiteRight1.setImageResource(R.drawable.ic_common_more);
        whiteRight1.setVisibility(View.VISIBLE);
        whiteRight2.setImageResource(R.drawable.ic_common_share);
        whiteRight2.setVisibility(View.VISIBLE);
    }

    @Override
    public void initListener() {
        super.initListener();
        msv.setOnScrollListener(this);
        whiteLeft.setOnClickListener(this);
        transLeft.setOnClickListener(this);
        fd(R.id.fl_play_pause).setOnClickListener(this);
        fd(R.id.cl_album).setOnClickListener(this);
        fd(R.id.tv_play_list).setOnClickListener(this);
        fd(R.id.iv_play_list).setOnClickListener(this);
        fd(R.id.iv_schedule).setOnClickListener(this);
        lavPlayNext.setOnClickListener(this);
        lavPlayPre.setOnClickListener(this);
        tvSchedule.setOnClickListener(this);
        mAlbumAdapter.setOnItemClickListener(this);
        isbProgress.setOnSeekChangeListener(this);
        mPlayerManager.addPlayerStatusListener(this);
        mPlayerManager.addAdsStatusListener(this);
        fd(R.id.tv_more_relative).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, AlbumListFragment.LIKE)
                    .withString(KeyCode.Home.TITLE, "更多推荐")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));

        });

    }

    @Override
    public void initData() {
        mHandler = new Handler();
        Track currSoundIgnoreKind = mPlayerManager.getCurrSoundIgnoreKind(true);
        if (null != currSoundIgnoreKind) {
            mTrack = currSoundIgnoreKind;
            Glide.with(this).load(currSoundIgnoreKind.getCoverUrlLarge()).into(ivBg);
            Glide.with(this).load(currSoundIgnoreKind.getAnnouncer().getAvatarUrl()).into((ImageView) fd(R.id.iv_announcer_cover));
            Glide.with(this).load(currSoundIgnoreKind.getAlbum().getCoverUrlMiddle()).into((ImageView) fd(R.id.iv_album_cover));

            ((TextView) fd(R.id.tv_track_name)).setText(currSoundIgnoreKind.getTrackTitle());
            ((TextView) fd(R.id.tv_announcer_name)).setText(currSoundIgnoreKind.getAnnouncer().getNickname());
            String vsignature = currSoundIgnoreKind.getAnnouncer().getVsignature();
            if (TextUtils.isEmpty(vsignature)) {
                fd(R.id.tv_vsignature).setVisibility(View.GONE);
            } else {
                ((TextView) fd(R.id.tv_vsignature)).setText(vsignature);
            }
            ((TextView) fd(R.id.tv_following_count)).setText(getString(R.string.following_count,
                    ZhumulangmaUtil.toWanYi(currSoundIgnoreKind.getAnnouncer().getFollowingCount())));
            fd(R.id.tv_vsignature).setVisibility(currSoundIgnoreKind.getAnnouncer().isVerified() ? View.VISIBLE : View.GONE);

            ((TextView) fd(R.id.tv_album_name)).setText(currSoundIgnoreKind.getAlbum().getAlbumTitle());
            ((TextView) fd(R.id.tv_track_intro)).setText(currSoundIgnoreKind.getTrackIntro());
            ((TextView) fd(R.id.tv_playcount_createtime)).setText(getString(R.string.playcount_createtime,
                    ZhumulangmaUtil.toWanYi(currSoundIgnoreKind.getPlayCount()),
                    TimeUtils.millis2String(currSoundIgnoreKind.getCreatedAt(), new SimpleDateFormat("yyyy-MM-dd"))));
            ((TextView) fd(R.id.tv_favorite_count)).setText(getString(R.string.favorite_count,
                    ZhumulangmaUtil.toWanYi(currSoundIgnoreKind.getFavoriteCount())));
            ((TextView) fd(R.id.tv_comment_count)).setText(getString(R.string.comment_count,
                    ZhumulangmaUtil.toWanYi(currSoundIgnoreKind.getCommentCount())));

            ((TextView) fd(R.id.tv_duration)).setText(ZhumulangmaUtil.secondToTimeE(currSoundIgnoreKind.getDuration()));
            mViewModel.getRelativeAlbums(String.valueOf(mTrack.getDataId()));
            isbProgress.setMax(currSoundIgnoreKind.getDuration());
            if (mPlayerManager.isPlaying()) {
                ((TextView) fd(R.id.tv_current)).setText(ZhumulangmaUtil.secondToTimeE(
                        mPlayerManager.getPlayCurrPositon() / 1000));
                isbProgress.setProgress((float) mPlayerManager.getPlayCurrPositon() / 1000);
            } else {
                ((TextView) fd(R.id.tv_current)).setText(ZhumulangmaUtil.secondToTimeE(0));
                isbProgress.setProgress(0);
            }
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(() -> scheduleTime(), 0);

    }

    private void scheduleTime() {
        int type = SPUtils.getInstance().getInt(AppConstants.SP.PLAY_SCHEDULE_TYPE, 0);
        long time = SPUtils.getInstance().getLong(AppConstants.SP.PLAY_SCHEDULE_TIME, 0);

        if (type == 0) {
            tvSchedule.setText("定时");
            mHandler.removeCallbacksAndMessages(null);
        } else if (type == 1) {
            mHandler.postDelayed(() -> scheduleTime(), 1000);
            tvSchedule.setText(ZhumulangmaUtil.secondToTimeE(mPlayerManager.getDuration() / 1000 -
                    mPlayerManager.getPlayCurrPositon() / 1000));
        } else {
            if (System.currentTimeMillis() < time) {
                mHandler.postDelayed(() -> scheduleTime(), 1000);
                tvSchedule.setText(ZhumulangmaUtil.secondToTimeE((time - System.currentTimeMillis()) / 1000));
            } else {
                mHandler.removeCallbacksAndMessages(null);
                tvSchedule.setText("定时");
            }
        }
    }

    @Override
    public void initViewObservable() {
        mViewModel.getAlbumSingleLiveEvent().observe(this, albums -> mAlbumAdapter.setNewData(albums));

    }


    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public void onScroll(int scrollY) {

        ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(scrollY, SizeUtils.dp2px(100), c.getTop() - SizeUtils.dp2px(80)));
        ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(scrollY, SizeUtils.dp2px(100), c.getTop() - SizeUtils.dp2px(80)));

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == whiteLeft || v == transLeft) {
            pop();
        } else if (R.id.cl_album == id) {
            if (null != mTrack) {
                Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mTrack.getAlbum().getAlbumId())
                        .navigation();
                NavigateBean navigateBean = new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation);
                navigateBean.launchMode = STANDARD;
                EventBus.getDefault().post(new BaseActivityEvent<>(
                        EventCode.MainCode.NAVIGATE, navigateBean));
            }
        } else if (R.id.lav_pre == id) {
            lavPlayPre.playAnimation();
            mPlayerManager.playPre();
        } else if (R.id.lav_next == id) {
            lavPlayNext.playAnimation();
            mPlayerManager.playNext();
        } else if (R.id.fl_play_pause == id) {
            if (mPlayerManager.isPlaying()) {
                mPlayerManager.pause();
            } else {
                mPlayerManager.play();
            }
        } else if (R.id.tv_schedule == id || R.id.iv_schedule == id) {
            new XPopup.Builder(getContext()).asCustom(mSchedulePopup).show();
        } else if (R.id.tv_play_list == id || R.id.iv_play_list == id) {
            new XPopup.Builder(getContext()).popupAnimation(TranslateFromBottom).setPopupCallback(new SimpleCallback() {
                @Override
                public void onCreated() {
                    super.onCreated();
                    XmDownloadManager.getInstance().addDownloadStatueListener(PlayTrackFragment.this);
                    mPlayerManager.setPlayListChangeListener(PlayTrackFragment.this);
                    mPlayListPopup.getTrackAdapter().setNewData(mPlayerManager.getPlayList());
                    mPlayListPopup.getRecyclerView().scrollToPosition(mPlayerManager.getCurrentIndex());
                }
            }).enableDrag(false).asCustom(mPlayListPopup).show();
        }
    }

    @Override
    public void onNewBundle(Bundle args) {
        super.onNewBundle(args);
        if (mPlayListPopup.getTrackAdapter() != null) {
            mPlayListPopup.getTrackAdapter().setNewData(mPlayerManager.getPlayList());
            mPlayListPopup.getRecyclerView().scrollToPosition(
                    mPlayerManager.getPlayList().indexOf(mPlayerManager.getCurrSoundIgnoreKind(true)));
        }
    }

    private void updateDownloadStatus(Track track) {
        List<Track> tracks = mPlayListPopup.getTrackAdapter().getData();
        int index = mPlayListPopup.getTrackAdapter().getData().indexOf(track);

        if (index != -1) {
            DownloadState downloadStatus = XmDownloadManager.getInstance()
                    .getSingleTrackDownloadStatus(tracks.get(index).getDataId());

            View ivDownload = mPlayListPopup.getTrackAdapter().getViewByPosition(index, R.id.iv_download);
            View progress = mPlayListPopup.getTrackAdapter().getViewByPosition(index, R.id.progressBar);
            View ivDownloadSucc = mPlayListPopup.getTrackAdapter().getViewByPosition(index, R.id.iv_downloadsucc);
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

    private void updatePlayStatus() {
        if (mPlayListPopup.getTrackAdapter() == null) {
            return;
        }
        Track track = mPlayerManager.getCurrSoundIgnoreKind(true);
        if (null == track) {
            return;
        }
        List<Track> tracks = mPlayListPopup.getTrackAdapter().getData();

        for (int i = 0; i < tracks.size(); i++) {
            LottieAnimationView lavPlaying = (LottieAnimationView) mPlayListPopup.getTrackAdapter()
                    .getViewByPosition(i, R.id.lav_playing);
            TextView tvTitle = (TextView) mPlayListPopup.getTrackAdapter()
                    .getViewByPosition(i, R.id.tv_title);
            if (null != lavPlaying && tvTitle != null) {
                if (tracks.get(i).getDataId() == track.getDataId()) {
                    lavPlaying.setVisibility(View.VISIBLE);
                    tvTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    if (XmPlayerManager.getInstance(mContext).isPlaying()) {
                        lavPlaying.playAnimation();
                    } else {
                        lavPlaying.pauseAnimation();
                    }
                } else {
                    lavPlaying.cancelAnimation();
                    tvTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                    lavPlaying.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onStartGetAdsInfo() {
        Log.d(TAG, "onStartGetAdsInfo() called");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        Log.d(TAG, "onGetAdsInfo() called with: advertisList = [" + advertisList + "]");
    }


    @Override
    public void onAdsStartBuffering() {
        bufferingAnim();
        Log.e(TAG, "onAdsStartBuffering() bufferingAnim");
    }

    @Override
    public void onAdsStopBuffering() {
     /*   if(mPlayerManager.isPlaying()){
            playingAnim();
        }*/
        Log.d(TAG, "onAdsStopBuffering() called");
    }


    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        bufferingAnim();
        Log.e(TAG, "onStartPlayAds() bufferingAnim with: advertis = [" + advertis + "], i = [" + i + "]");
    }

    @Override
    public void onCompletePlayAds() {
        //    playAnim();
        //    Log.e(TAG, "onCompletePlayAds() playAnim");
    }

    @Override
    public void onError(int i, int i1) {
        Log.d(TAG, "onError() called with: i = [" + i + "], i1 = [" + i1 + "]");
    }

    @Override
    public void onPlayStart() {
        updatePlayStatus();
        TLog.d(mPlayerManager.isBuffering());
        if (!mPlayerManager.isBuffering()) {

            playAnim();
            Log.e(TAG, "onPlayStart() playAnim");
        }

    }

    @Override
    public void onPlayPause() {
        updatePlayStatus();
        pauseAnim();
        Log.e(TAG, "onPlayPause() pauseAnim");
    }

    @Override
    public void onPlayStop() {
        updatePlayStatus();
        pauseAnim();
        Log.e(TAG, "onPlayStop() pauseAnim");
    }

    @Override
    public void onSoundPlayComplete() {
        updatePlayStatus();

        if (SPUtils.getInstance().getInt(AppConstants.SP.PLAY_SCHEDULE_TYPE, 0) == 1) {
            SPUtils.getInstance().put(AppConstants.SP.PLAY_SCHEDULE_TYPE, 0);

            //   Log.e(TAG, "onSoundPlayComplete() pauseAnim");
        } else if (!mPlayerManager.hasNextSound()) {
            pauseAnim();
        }
        Log.d(TAG, "onSoundPlayComplete() hasNextSound:" + mPlayerManager.hasNextSound());
    }

    @Override
    public void onSoundPrepared() {
        updatePlayStatus();
        Log.d(TAG, "onSoundPrepared() called");
    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        updatePlayStatus();
        Log.d(TAG, "onSoundSwitch() called with: playableModel = [" + playableModel + "], playableModel1 = [" + playableModel1 + "]");
        initData();
    }

    @Override
    public void onBufferingStart() {
        TLog.d(mPlayerManager.isPlaying());
        if (mPlayerManager.isPlaying()) {
            bufferingAnim();
        }
        Log.e(TAG, "onBufferingStart() bufferingAnim");
    }

    @Override
    public void onBufferingStop() {
        TLog.d(mPlayerManager.isPlaying());
        if (mPlayerManager.isPlaying()) {
            playAnim();
            Log.e(TAG, "onBufferingStop() playAnim");
        } else {
            pauseAnim();
            Log.e(TAG, "onBufferingStop() pauseAnim");
        }
        Log.d(TAG, "onBufferingStop() called");
    }

    @Override
    public void onBufferProgress(int i) {
        Log.d(TAG, "onBufferProgress() called with: i = [" + i + "]");
    }

    @Override
    public void onPlayProgress(int i, int i1) {
        ((TextView) fd(R.id.tv_current)).setText(ZhumulangmaUtil.secondToTimeE(i / 1000));
        if (!isTouch) {
            isbProgress.setProgress((float) i / 1000);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mPlayerManager.removePlayerStatusListener(this);
        mPlayerManager.removeAdsStatusListener(this);
        XmDownloadManager.getInstance().removeDownloadStatueListener(this);
    }

    @Override
    public Class<PlayTrackViewModel> onBindViewModel() {
        return PlayTrackViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getData().get(position).getId())
                .navigation();
        NavigateBean navigateBean = new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation);
        navigateBean.launchMode = STANDARD;
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.MainCode.NAVIGATE, navigateBean));
    }


    @Override
    public void onSeeking(SeekParams seekParams) {

        TextView indicator = isbProgress.getIndicator().getTopContentView().findViewById(R.id.tv_indicator);
        indicator.setText(ZhumulangmaUtil.secondToTimeE(seekParams.progress)
                + "/" + ZhumulangmaUtil.secondToTimeE((long) seekParams.seekBar.getMax()));
    }

    @Override

    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
        isTouch = true;
    }

    boolean isTouch;

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        mPlayerManager.seekTo(seekBar.getProgress() * 1000);
        isTouch = false;
    }

    private void playAnim() {
        if (!isPlaying) {

            lavPlayPause.setMinAndMaxFrame(55, 90);
            lavPlayPause.loop(false);
            lavPlayPause.playAnimation();
            lavBuffering.cancelAnimation();
            lavBuffering.setVisibility(View.GONE);
            lavPlayPause.setVisibility(View.VISIBLE);
            lavPlayPause.addAnimatorListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    playingAnim();
                    lavPlayPause.removeAnimatorListener(this);
                }
            });
        }
    }

    private void playingAnim() {
        lavPlayPause.removeAllAnimatorListeners();
        isPlaying = true;
        lavPlayPause.setMinAndMaxFrame(90, 170);
        lavPlayPause.loop(true);
        lavPlayPause.playAnimation();
        lavBuffering.cancelAnimation();
        lavBuffering.setVisibility(View.GONE);
        lavPlayPause.setVisibility(View.VISIBLE);
    }

    private void bufferingAnim() {

        lavPlayPause.cancelAnimation();
        lavBuffering.playAnimation();
        isPlaying = false;
        lavPlayPause.setVisibility(View.GONE);
        lavBuffering.setVisibility(View.VISIBLE);
    }

    private void pauseAnim() {
        lavBuffering.cancelAnimation();
        lavPlayPause.removeAllAnimatorListeners();
        isPlaying = false;
        lavPlayPause.setMinAndMaxFrame(180, 210);
        lavPlayPause.loop(false);
        lavPlayPause.playAnimation();
        lavBuffering.setVisibility(View.GONE);
        lavPlayPause.setVisibility(View.VISIBLE);
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
    public boolean onBackPressedSupport() {
        if (mSchedulePopup != null && mSchedulePopup.getPickerView() != null && mSchedulePopup.getPickerView().isShowing()) {
            mSchedulePopup.getPickerView().dismiss();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onSelected(int type, long time) {
        Log.d(TAG, "onSelected() called with: type = [" + type + "], time = [" + time + "]");

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(() -> scheduleTime(), 0);
    }

    @Override
    public void onRefresh() {
        isUp=true;
        mPlayerManager.getPrePlayList();
        List<Track> playList = mPlayerManager.getPlayList();
        mPlayListPopup.getRefreshLayout().finishRefresh();
        if (mPlayListPopup.getTrackAdapter().getData().size() != playList.size()) {
            mPlayListPopup.getTrackAdapter().addData(0,
                    playList.subList(0,playList.size()-mPlayListPopup.getTrackAdapter().getData().size()));
        }
    }

    @Override
    public void onLoadMore() {
        isUp=false;
        mPlayerManager.getNextPlayList();
    }

    @Override
    public void onSort() {
        mPlayerManager.permutePlayList();
        mPlayListPopup.getTrackAdapter().setNewData(mPlayerManager.getPlayList());
        mPlayListPopup.getRecyclerView().scrollToPosition(mPlayerManager.getCurrentIndex());
    }

    @Override
    public void onTrackItemClick(BaseQuickAdapter adapter, View view, int position) {
        Log.d(TAG, "onTrackItemClick() called with: adapter = [" + adapter + "], view = [" + view + "], position = [" + position + "]");
        mPlayerManager.play(position);
    }

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

    @Override
    public void onDataReady(List<Track> list, boolean b, boolean b1) throws RemoteException {
    if(isUp){
        mPlayListPopup.getRefreshLayout().finishRefresh();
        mPlayListPopup.getTrackAdapter().addData(0,list);
    }else {
       if(CollectionUtils.isEmpty(list)){
           mPlayListPopup.getRefreshLayout().finishLoadMoreWithNoMoreData();
       } else {
            mPlayListPopup.getRefreshLayout().finishLoadMore();
            mPlayListPopup.getTrackAdapter().addData(list);
        }
    }
    }

    @Override
    public void onError(int i, String s, boolean b) throws RemoteException {
        ToastUtil.showToast(s);
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}
