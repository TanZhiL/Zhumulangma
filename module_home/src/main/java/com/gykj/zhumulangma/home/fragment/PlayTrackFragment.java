package com.gykj.zhumulangma.home.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProvider;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
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
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentPlayTrackBinding;
import com.gykj.zhumulangma.home.dialog.CommentPopup;
import com.gykj.zhumulangma.home.dialog.PlaySchedulePopup;
import com.gykj.zhumulangma.home.dialog.PlayTempoPopup;
import com.gykj.zhumulangma.home.dialog.PlayTrackPopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayTrackViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmDataCallback;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.yokeyword.fragmentation.ISupportFragment;

import static com.gykj.zhumulangma.home.dialog.PlayTempoPopup.TEMPO_LABLES;
import static com.gykj.zhumulangma.home.dialog.PlayTempoPopup.TEMPO_VALUES;
import static com.lxj.xpopup.enums.PopupAnimation.TranslateFromBottom;

@Route(path = Constants.Router.Home.F_PLAY_TRACK)
public class PlayTrackFragment extends BaseMvvmFragment<HomeFragmentPlayTrackBinding,PlayTrackViewModel> implements
        NestedScrollView.OnScrollChangeListener, View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener, OnSeekChangeListener,
        PlaySchedulePopup.onSelectedListener, PlayTrackPopup.onActionListener,
        IXmDataCallback, PlayTempoPopup.onTempoSelectedListener, View.OnTouchListener {

    private ImageView whiteLeft;
    private ImageView transLeft;

    private AlbumAdapter mAlbumAdapter;
    private Track mTrack;
    private boolean isPlaying;
    private PlaySchedulePopup mSchedulePopup;
    private PlayTrackPopup mPlayTrackPopup;
    private CommentPopup mCommentPopup;
    private boolean isUp;
    private XmPlayerManager mPlayerManager = XmPlayerManager.getInstance(mActivity);



    public PlayTrackFragment() {

    }

    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
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
    protected void initView() {

        mBinding.rvRelative.setLayoutManager(new LinearLayoutManager(mActivity));
        mAlbumAdapter = new AlbumAdapter(R.layout.home_item_album_line);
        mAlbumAdapter.bindToRecyclerView(mBinding.rvRelative);
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
        mSchedulePopup = new PlaySchedulePopup(mActivity, this);
        mPlayTrackPopup = new PlayTrackPopup(mActivity, this);
        mCommentPopup = new CommentPopup(mActivity);

    }


    private void initBar() {

        transLeft = mBinding.ctbTrans.getLeftCustomView().findViewById(R.id.iv_left);
        ImageView transRight1 = mBinding.ctbTrans.getRightCustomView().findViewById(R.id.iv1_right);
        ImageView transRight2 = mBinding.ctbTrans.getRightCustomView().findViewById(R.id.iv2_right);


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
        transRight2.setOnClickListener(this);
        whiteLeft = mBinding.ctbWhite.getLeftCustomView().findViewById(R.id.iv_left);
        ImageView whiteRight1 = mBinding.ctbWhite.getRightCustomView().findViewById(R.id.iv1_right);
        ImageView whiteRight2 = mBinding.ctbWhite.getRightCustomView().findViewById(R.id.iv2_right);
        TextView tvTitle = mBinding.ctbWhite.getCenterCustomView().findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("歌曲详情");

        whiteLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        whiteLeft.setVisibility(View.VISIBLE);
        whiteLeft.setRotation(-90);
        whiteRight1.setImageResource(R.drawable.ic_common_more);
        whiteRight1.setVisibility(View.VISIBLE);
        whiteRight2.setImageResource(R.drawable.ic_common_share);
        whiteRight2.setVisibility(View.VISIBLE);
        whiteRight2.setOnClickListener(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.nsv.setOnScrollChangeListener(this);
        whiteLeft.setOnClickListener(this);
        transLeft.setOnClickListener(this);
        mBinding.flPlayPause.setOnClickListener(this);
        mBinding.clAlbum.setOnClickListener(this);
        mBinding.tvPlayList.setOnClickListener(this);
        mBinding.ivPlayList.setOnClickListener(this);
        mBinding.ivSchedule.setOnClickListener(this);
        mBinding.includeAnnouncer.clAnnouncer.setOnClickListener(this);
        mBinding.tvComment.setOnClickListener(this);
        mBinding.ivBg.setOnClickListener(this);
        mBinding.clAction.setOnClickListener(this);
        mBinding.lavNext.setOnClickListener(this);
        mBinding.lavPre.setOnClickListener(this);
        mBinding.tvSchedule.setOnClickListener(this);
        mBinding.tvPre15.setOnClickListener(this);
        mBinding.tvNext15.setOnClickListener(this);
        mBinding.tvTempo.setOnClickListener(this);
        mAlbumAdapter.setOnItemClickListener(this);
        mBinding.isbProgress.setOnSeekChangeListener(this);
        mBinding.isbProgress.setOnTouchListener(this);
        mPlayerManager.addPlayerStatusListener(playerStatusListener);
        mPlayerManager.addAdsStatusListener(adsStatusListener);

        mBinding.tvMoreRelative.setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, AlbumListFragment.LIKE)
                    .withString(KeyCode.Home.TITLE, "更多推荐")
                    .navigation();
            NavigateBean navigateBean = new NavigateBean(Constants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o);
            navigateBean.launchMode = STANDARD;
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, navigateBean));

        });
        RxView.clicks(mBinding.llSubscribe)
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    if (mTrack != null)
                        mViewModel.subscribe(String.valueOf(mTrack.getAlbum().getAlbumId()));
                });
        RxView.clicks(mBinding.llUnsubscribe)
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    if (mTrack != null)
                        mViewModel.unsubscribe(mTrack.getAlbum().getAlbumId());
                });
        RxView.clicks(mBinding.ivLike)
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    if (mTrack != null)
                        mViewModel.like(mTrack);
                });
        RxView.clicks(mBinding.ivUnlike)
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    if (mTrack != null)
                        mViewModel.unlike(mTrack);
                });
    }

    @Override
    public void initData() {
        Track currSoundIgnoreKind = mPlayerManager.getCurrSoundIgnoreKind(true);
        if (null != currSoundIgnoreKind) {
            mTrack = currSoundIgnoreKind;

            Glide.with(this).load(TextUtils.isEmpty(currSoundIgnoreKind.getCoverUrlLarge())
                    ? currSoundIgnoreKind.getAlbum().getCoverUrlLarge() : currSoundIgnoreKind.getCoverUrlLarge()).into(mBinding.ivBg);
            Glide.with(this).load(currSoundIgnoreKind.getAnnouncer().getAvatarUrl()).into(mBinding.includeAnnouncer.ivAnnouncerCover);
            Glide.with(this).load(currSoundIgnoreKind.getAlbum().getCoverUrlMiddle()).into(mBinding.ivAlbumCover);

            mBinding.tvTrackName.setText(currSoundIgnoreKind.getTrackTitle());
            mBinding.includeAnnouncer.tvAnnouncerName.setText(currSoundIgnoreKind.getAnnouncer().getNickname());


            mBinding.includeAnnouncer.tvVip.setVisibility(currSoundIgnoreKind.getAnnouncer().isVerified() ? View.VISIBLE : View.GONE);
            mBinding.tvAlbumName.setText(currSoundIgnoreKind.getAlbum().getAlbumTitle());
            mBinding.tvTrackIntro.setText(currSoundIgnoreKind.getTrackIntro());
            mBinding.tvPlaycountCreatetime.setText(getString(R.string.playcount_createtime,
                    ZhumulangmaUtil.toWanYi(currSoundIgnoreKind.getPlayCount())) + "\u3000\u3000" +
                    TimeUtils.millis2String(currSoundIgnoreKind.getCreatedAt(), new SimpleDateFormat("yyyy-MM-dd")));
            mBinding.tvFavoriteCount.setText(getString(R.string.favorite_count,
                    ZhumulangmaUtil.toWanYi(currSoundIgnoreKind.getFavoriteCount())));
            mBinding.tvCommentCount.setText(getString(R.string.comment_count,
                    ZhumulangmaUtil.toWanYi(currSoundIgnoreKind.getCommentCount())));

            mBinding.tvDuration.setText(ZhumulangmaUtil.secondToTimeE(currSoundIgnoreKind.getDuration()));

            mViewModel.getRelativeAlbums(String.valueOf(mTrack.getDataId()));
            mViewModel.getAnnouncer(mTrack.getAnnouncer().getAnnouncerId());
            mBinding.isbProgress.setMax(currSoundIgnoreKind.getDuration());
            if (mPlayerManager.isPlaying()) {
                mBinding.tvCurrent.setText(ZhumulangmaUtil.secondToTimeE(
                        mPlayerManager.getPlayCurrPositon() / 1000));
                mBinding.isbProgress.setProgress((float) mPlayerManager.getPlayCurrPositon() / 1000);
            } else {
                mBinding.tvCurrent.setText(ZhumulangmaUtil.secondToTimeE(0));
                mBinding.isbProgress.setProgress(0);
            }
            mViewModel.getSubscribe(String.valueOf(mTrack.getAlbum().getAlbumId()));
            mViewModel.getFavorite(String.valueOf(mTrack.getDataId()));
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(this::scheduleTime, 0);
        mBinding.tvActionDuration.setText(ZhumulangmaUtil.secondToTimeE(currSoundIgnoreKind.getDuration()));
        mBinding.tvActionCur.setText(ZhumulangmaUtil.secondToTimeE(mPlayerManager.getPlayCurrPositon() / 1000));
        mBinding.tvTempo.setText(TEMPO_LABLES[Arrays.binarySearch(TEMPO_VALUES, XmPlayerManager.getInstance(mActivity).getTempo())]);
    }

    private void scheduleTime() {
        int type = SPUtils.getInstance().getInt(Constants.SP.PLAY_SCHEDULE_TYPE, 0);
        long time = SPUtils.getInstance().getLong(Constants.SP.PLAY_SCHEDULE_TIME, 0);

        if (type == 0) {
            mBinding.tvSchedule.setText("定时");
            mHandler.removeCallbacksAndMessages(null);
        } else if (type == 1) {
            mHandler.postDelayed(this::scheduleTime, 1000);
            mBinding.tvSchedule.setText(ZhumulangmaUtil.secondToTimeE(mPlayerManager.getDuration() / 1000 -
                    mPlayerManager.getPlayCurrPositon() / 1000));
        } else {
            if (System.currentTimeMillis() < time) {
                mHandler.postDelayed(this::scheduleTime, 1000);
                mBinding.tvSchedule.setText(ZhumulangmaUtil.secondToTimeE((time - System.currentTimeMillis()) / 1000));
            } else {
                mHandler.removeCallbacksAndMessages(null);
                mBinding.tvSchedule.setText("定时");
            }
        }
    }

    @Override
    public void initViewObservable() {
        mViewModel.getSubscribeEvent().observe(this, aBoolean -> {
            mBinding.llSubscribe.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
            mBinding.llUnsubscribe.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
        });
        mViewModel.getFavoriteEvent().observe(this, aBoolean -> {
            mBinding.ivLike.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
            mBinding.ivUnlike.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
        });
        mViewModel.getAlbumsEvent().observe(this, albums -> mAlbumAdapter.setNewData(albums));

        mViewModel.getAnnouncerEvent().observe(this, announcer -> {
            String vsignature = announcer.getVsignature();
            if (TextUtils.isEmpty(vsignature)) {
                mBinding.includeAnnouncer.tvVsignature.setVisibility(View.GONE);
            } else {
                mBinding.includeAnnouncer.tvVsignature.setVisibility(View.VISIBLE);
                mBinding.includeAnnouncer.tvVsignature.setText(vsignature);
            }
            mBinding.includeAnnouncer.tvFollowingCount.setText(getString(R.string.following_count,
                    ZhumulangmaUtil.toWanYi(announcer.getFollowerCount())));
        });
    }


    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == whiteLeft || v == transLeft) {
            pop();
        } else if (R.id.cl_album == id) {
            if (null != mTrack) {
                Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mTrack.getAlbum().getAlbumId())
                        .navigation();
                NavigateBean navigateBean = new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation);
                navigateBean.launchMode = STANDARD;
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE, navigateBean));
            }
        } else if (R.id.lav_pre == id) {
            mBinding.lavPre.playAnimation();
            if (mPlayerManager.getPlayMode() == XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM) {
                mPlayerManager.playPre();
                return;
            }
            if (mPlayerManager.hasPreSound()) {
                mPlayerManager.playPre();
            } else {
                ToastUtil.showToast("没有更多");
            }
        } else if (R.id.lav_next == id) {
            mBinding.lavNext.playAnimation();
            if (mPlayerManager.getPlayMode() == XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM) {
                mPlayerManager.playNext();
                return;
            }
            if (mPlayerManager.hasNextSound()) {
                mPlayerManager.playNext();
            } else {
                ToastUtil.showToast("没有更多");
            }
        } else if (R.id.fl_play_pause == id) {
            if (mPlayerManager.isPlaying()) {
                mPlayerManager.pause();
            } else {
                mPlayerManager.play();
            }
        } else if (R.id.tv_schedule == id || R.id.iv_schedule == id) {
            new XPopup.Builder(getContext()).setPopupCallback(new SimpleCallback() {
                @Override
                public void beforeShow() {
                    super.beforeShow();
                    mSchedulePopup.getScheduleAdapter().notifyDataSetChanged();
                }
            }).asCustom(mSchedulePopup).show();
        } else if (R.id.tv_play_list == id || R.id.iv_play_list == id) {
            new XPopup.Builder(getContext()).popupAnimation(TranslateFromBottom).setPopupCallback(
                    new SimpleCallback() {
                        @Override
                        public void onCreated() {
                            super.onCreated();
                            mPlayerManager.setPlayListChangeListener(PlayTrackFragment.this);
                            mPlayTrackPopup.getTrackAdapter().setNewData(mPlayerManager.getPlayList());
                            mPlayTrackPopup.getRecyclerView().scrollToPosition(mPlayerManager.getCurrentIndex());
                        }

                        @Override
                        public void onShow() {
                            super.onShow();
                            XmDownloadManager.getInstance().addDownloadStatueListener(downloadStatueListener);
                        }

                        @Override
                        public void onDismiss() {
                            XmDownloadManager.getInstance().removeDownloadStatueListener(downloadStatueListener);
                        }
                    }).enableDrag(false).asCustom(mPlayTrackPopup).show();
        } else if (v == mBinding.ivBg) {
            mBinding.clAction.setVisibility(View.VISIBLE);
        } else if (v == mBinding.clAction) {
            mBinding.clAction.setVisibility(View.GONE);
        } else if (v == mBinding.tvPre15) {
            mPlayerManager.seekTo(mPlayerManager.getPlayCurrPositon() - 15 * 1000);
        } else if (v == mBinding.tvNext15) {
            mPlayerManager.seekTo(mPlayerManager.getPlayCurrPositon() + 15 * 1000);
        } else if (v == mBinding.tvTempo) {
            new XPopup.Builder(getContext()).asCustom(new PlayTempoPopup(mActivity, this)).show();
        } else if (id == R.id.cl_announcer) {
            Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                    .withLong(KeyCode.Home.ANNOUNCER_ID, mTrack.getAnnouncer().getAnnouncerId())
                    .withString(KeyCode.Home.ANNOUNCER_NAME, mTrack.getAnnouncer().getNickname())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                    new NavigateBean(Constants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation)));
        } else if (R.id.tv_comment == id) {
            new XPopup.Builder(mActivity).autoOpenSoftInput(true).popupAnimation(TranslateFromBottom)
                    .dismissOnTouchOutside(false).enableDrag(false).asCustom(mCommentPopup).show();
        } else if (R.id.iv2_right == id) {
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHARE));
        }
    }

    @Override
    public void onNewBundle(Bundle args) {
        super.onNewBundle(args);
        if (mPlayTrackPopup.getTrackAdapter() != null) {
            mPlayTrackPopup.getTrackAdapter().setNewData(mPlayerManager.getPlayList());
            mPlayTrackPopup.getRecyclerView().scrollToPosition(
                    mPlayerManager.getPlayList().indexOf(mPlayerManager.getCurrSoundIgnoreKind(true)));
        }
    }

    private void updateDownloadStatus(Track track) {
        List<Track> tracks = mPlayTrackPopup.getTrackAdapter().getData();
        int index = mPlayTrackPopup.getTrackAdapter().getData().indexOf(track);

        if (index != -1) {
            DownloadState downloadStatus = XmDownloadManager.getInstance()
                    .getSingleTrackDownloadStatus(tracks.get(index).getDataId());

            View ivDownload = mPlayTrackPopup.getTrackAdapter().getViewByPosition(index, R.id.iv_download);
            View progress = mPlayTrackPopup.getTrackAdapter().getViewByPosition(index, R.id.progressBar);
            View ivDownloadSucc = mPlayTrackPopup.getTrackAdapter().getViewByPosition(index, R.id.iv_downloadsucc);
            if (ivDownload == null || progress == null || ivDownloadSucc == null) {
                mPlayTrackPopup.getTrackAdapter().notifyItemChanged(index);
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
        if (mPlayTrackPopup.getTrackAdapter() == null) {
            return;
        }
        Track track = mPlayerManager.getCurrSoundIgnoreKind(true);
        if (null == track) {
            return;
        }
        List<Track> tracks = mPlayTrackPopup.getTrackAdapter().getData();

        for (int i = 0; i < tracks.size(); i++) {
            LottieAnimationView lavPlaying = (LottieAnimationView) mPlayTrackPopup.getTrackAdapter()
                    .getViewByPosition(i, R.id.lav_playing);
            TextView tvTitle = (TextView) mPlayTrackPopup.getTrackAdapter()
                    .getViewByPosition(i, R.id.tv_title);
            if (null != lavPlaying && tvTitle != null) {
                if (tracks.get(i).getDataId() == track.getDataId()) {
                    lavPlaying.setVisibility(View.VISIBLE);
                    tvTitle.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
                    if (XmPlayerManager.getInstance(mActivity).isPlaying()) {
                        lavPlaying.playAnimation();
                    } else {
                        lavPlaying.pauseAnimation();
                    }
                } else {
                    lavPlaying.cancelAnimation();
                    tvTitle.setTextColor(mActivity.getResources().getColor(R.color.colorPrimaryDark));
                    lavPlaying.setVisibility(View.GONE);
                }
            } else {
                mPlayTrackPopup.getTrackAdapter().notifyItemChanged(i);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayerManager.removePlayerStatusListener(playerStatusListener);
        mPlayerManager.removeAdsStatusListener(adsStatusListener);
        XmDownloadManager.getInstance().removeDownloadStatueListener(downloadStatueListener);
        mPlayerManager.setPlayListChangeListener(null);
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
        Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getItem(position).getId())
                .navigation();
        NavigateBean navigateBean = new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation);
        navigateBean.launchMode = STANDARD;
        EventBus.getDefault().post(new ActivityEvent(
                EventCode.Main.NAVIGATE, navigateBean));
    }


    @Override
    public void onSeeking(SeekParams seekParams) {

        TextView indicator = mBinding.isbProgress.getIndicator().getTopContentView().findViewById(R.id.tv_indicator);
        indicator.setText(ZhumulangmaUtil.secondToTimeE(seekParams.progress)
                + "/" + ZhumulangmaUtil.secondToTimeE((long) seekParams.seekBar.getMax()));
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
        isTouch = true;
    }

    //解决点击进度条跳动的问题
    private boolean isTouch;

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        mPlayerManager.seekTo(seekBar.getProgress() * 1000);
        mHandler.postDelayed(touchRunable, 200);
    }

    private void playAnim() {
        if (!isPlaying) {

            mBinding.lavPlayPause.setMinAndMaxFrame(55, 90);
            mBinding.lavPlayPause.loop(false);
            mBinding.lavPlayPause.playAnimation();
            mBinding.lavBuffering.cancelAnimation();
            mBinding.lavBuffering.setVisibility(View.INVISIBLE);
            mBinding.lavPlayPause.setVisibility(View.VISIBLE);
            mBinding.lavPlayPause.addAnimatorListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    playingAnim();
                    mBinding.lavPlayPause.removeAnimatorListener(this);
                }
            });
        }
    }

    private void playingAnim() {
        mBinding.lavPlayPause.removeAllAnimatorListeners();
        isPlaying = true;
        mBinding.lavPlayPause.setMinAndMaxFrame(90, 170);
        mBinding.lavPlayPause.loop(true);
        mBinding.lavPlayPause.playAnimation();
        mBinding.lavBuffering.cancelAnimation();
        mBinding.lavBuffering.setVisibility(View.INVISIBLE);
        mBinding.lavPlayPause.setVisibility(View.VISIBLE);
    }

    private void bufferingAnim() {

        mBinding.lavPlayPause.cancelAnimation();
        mBinding.lavBuffering.playAnimation();
        isPlaying = false;
        mBinding.lavPlayPause.setVisibility(View.INVISIBLE);
        mBinding.lavBuffering.setVisibility(View.VISIBLE);
    }

    private void pauseAnim() {
        mBinding.lavBuffering.cancelAnimation();
        mBinding.lavPlayPause.removeAllAnimatorListeners();
        isPlaying = false;
        mBinding.lavPlayPause.setMinAndMaxFrame(180, 210);
        mBinding.lavPlayPause.loop(false);
        mBinding.lavPlayPause.playAnimation();
        mBinding.lavBuffering.setVisibility(View.INVISIBLE);
        mBinding.lavPlayPause.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.HIDE_GP));
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHOW_GP));
    }

    @Override
    public boolean onBackPressedSupport() {
        if (super.onBackPressedSupport()) {
            return true;
        } else if (mSchedulePopup != null && mSchedulePopup.getPickerView() != null && mSchedulePopup.getPickerView().isShowing()) {
            mSchedulePopup.getPickerView().dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onSelected(int type, long time) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(this::scheduleTime, 0);
    }

    @Override
    public void onRefresh() {
        isUp = true;
        mPlayerManager.getPrePlayList();
        List<Track> playList = mPlayerManager.getPlayList();
        mPlayTrackPopup.getRefreshLayout().finishRefresh();
        if (mPlayTrackPopup.getTrackAdapter().getData().size() != playList.size()) {
            mPlayTrackPopup.getTrackAdapter().addData(0,
                    playList.subList(0, playList.size() - mPlayTrackPopup.getTrackAdapter().getData().size()));
        }
    }

    @Override
    public void onLoadMore() {
        isUp = false;
        mPlayerManager.getNextPlayList();
    }

    @Override
    public void onSort() {
        mPlayerManager.permutePlayList();
        mPlayTrackPopup.getTrackAdapter().setNewData(mPlayerManager.getPlayList());
        mPlayTrackPopup.getRecyclerView().scrollToPosition(mPlayerManager.getCurrentIndex());
    }

    @Override
    public void onTrackItemClick(BaseQuickAdapter adapter, View view, int position) {
        mPlayerManager.play(position);
    }


    @Override
    public void onDataReady(List<Track> list, boolean b, boolean b1) throws RemoteException {
        if (isUp) {
            mPlayTrackPopup.getRefreshLayout().finishRefresh();
            mPlayTrackPopup.getTrackAdapter().addData(0, list);
        } else {
            if (CollectionUtils.isEmpty(list)) {
                mPlayTrackPopup.getRefreshLayout().finishLoadMoreWithNoMoreData();
            } else {
                mPlayTrackPopup.getRefreshLayout().finishLoadMore();
                mPlayTrackPopup.getTrackAdapter().addData(list);
            }
        }
    }

    @Override
    public void onError(int i, String s, boolean b) {
        mHandler.post(() -> ToastUtil.showToast(s));
        if (isUp) {
            mPlayTrackPopup.getRefreshLayout().finishRefresh();
        } else {
            mPlayTrackPopup.getRefreshLayout().finishLoadMore();
        }

    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    @Override
    public void onTempoSelected(String tempo) {
        mBinding.tvTempo.setText(tempo);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mHandler.removeCallbacks(touchRunable);
            isTouch = true;
        }
        return false;
    }

    private Runnable touchRunable = new Runnable() {
        @Override
        public void run() {
            isTouch = false;
        }
    };

    @Override
    public void onScrollChange(NestedScrollView nestedScrollView, int i, int scrollY, int i2, int i3) {
        mBinding.ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(scrollY, SizeUtils.dp2px(100),
                mBinding.clController.getTop() - SizeUtils.dp2px(80)));
        mBinding.ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(scrollY, SizeUtils.dp2px(100),
                mBinding. clController.getTop() - SizeUtils.dp2px(80)));
    }

    private IXmPlayerStatusListener playerStatusListener = new IXmPlayerStatusListener() {
        @Override
        public void onPlayStart() {
            updatePlayStatus();
            if (!mPlayerManager.isBuffering()) {

                playAnim();
            }

        }

        @Override
        public void onPlayPause() {
            updatePlayStatus();
            pauseAnim();
        }

        @Override
        public void onPlayStop() {
            updatePlayStatus();
            pauseAnim();
        }

        @Override
        public void onSoundPlayComplete() {
            updatePlayStatus();

            if (SPUtils.getInstance().getInt(Constants.SP.PLAY_SCHEDULE_TYPE, 0) == 1) {
                SPUtils.getInstance().put(Constants.SP.PLAY_SCHEDULE_TYPE, 0);
            } else if (!mPlayerManager.hasNextSound()) {
                pauseAnim();
            }
        }

        @Override
        public void onSoundPrepared() {
            updatePlayStatus();
        }

        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
            if (playableModel1 != null) {
                updatePlayStatus();
                initData();
            } else {
                pauseAnim();
            }
        }

        @Override
        public void onBufferingStart() {
            if (mPlayerManager.isPlaying()) {
                bufferingAnim();
            }
        }

        @Override
        public void onBufferingStop() {
            if (mPlayerManager.isPlaying()) {
                playAnim();
            } else {
                pauseAnim();
            }
        }

        @Override
        public void onBufferProgress(int i) {
        }

        @Override
        public void onPlayProgress(int i, int i1) {
            mBinding.tvCurrent.setText(ZhumulangmaUtil.secondToTimeE(i / 1000));
            mBinding.tvActionCur.setText(ZhumulangmaUtil.secondToTimeE(i / 1000));
            if (!isTouch) {
                mBinding.isbProgress.setProgress((float) i / 1000);
            }
        }

        @Override
        public boolean onError(XmPlayerException e) {
            return false;
        }
    };
    private IXmAdsStatusListener adsStatusListener = new IXmAdsStatusListener() {
        @Override
        public void onStartGetAdsInfo() {
        }

        @Override
        public void onGetAdsInfo(AdvertisList advertisList) {
        }


        @Override
        public void onAdsStartBuffering() {
            bufferingAnim();
        }

        @Override
        public void onAdsStopBuffering() {
        }


        @Override
        public void onStartPlayAds(Advertis advertis, int i) {
            bufferingAnim();
        }

        @Override
        public void onCompletePlayAds() {
        }

        @Override
        public void onError(int i, int i1) {
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
}
