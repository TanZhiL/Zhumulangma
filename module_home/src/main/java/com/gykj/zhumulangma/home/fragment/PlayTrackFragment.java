package com.gykj.zhumulangma.home.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProvider;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.widget.TScrollView;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayTrackViewModel;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Home.F_PLAY_TRACK)
public class PlayTrackFragment extends BaseMvvmFragment<PlayTrackViewModel> implements TScrollView.OnScrollListener, View.OnClickListener, IXmPlayerStatusListener, BaseQuickAdapter.OnItemClickListener {

    private TScrollView msv;
    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private View c;

    private ImageView whiteLeft;
    private ImageView whiteRight1;
    private ImageView whiteRight2;

    private ImageView transLeft;
    private ImageView transRight1;
    private ImageView transRight2;

    private LottieAnimationView lavPlayPause;
    private ImageView ivBg;
    private RecyclerView rvRelative;
    private AlbumAdapter mAlbumAdapter;
    private Track mTrack;

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
        lavPlayPause = fd(R.id.lav_play_pause);
        c = fd(R.id.c);
        rvRelative=fd(R.id.rv_relative);

        rvRelative.setLayoutManager(new LinearLayoutManager(mContext));

        mAlbumAdapter=new AlbumAdapter(R.layout.home_item_album);
        mAlbumAdapter.bindToRecyclerView(rvRelative);
        initBar();
        new Handler().postDelayed(()-> playAnim(),100);
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
        fd(R.id.iv_pre).setOnClickListener(this);
        fd(R.id.iv_next).setOnClickListener(this);
        fd(R.id.fl_play_pause).setOnClickListener(this);
        fd(R.id.cl_album).setOnClickListener(this);
        mAlbumAdapter.setOnItemClickListener(this);

        XmPlayerManager.getInstance(mContext).addPlayerStatusListener(this);
    }

    @Override
    public void initData() {
        Track currSoundIgnoreKind = XmPlayerManager.getInstance(mContext).getCurrSoundIgnoreKind(true);
        if (null != currSoundIgnoreKind) {
            mTrack = currSoundIgnoreKind;
            Glide.with(this).load(currSoundIgnoreKind.getCoverUrlLarge()).into(ivBg);
            Glide.with(this).load(currSoundIgnoreKind.getAnnouncer().getAvatarUrl()).into((ImageView) fd(R.id.iv_announcer_cover));
            Glide.with(this).load(currSoundIgnoreKind.getAlbum().getCoverUrlMiddle()).into((ImageView) fd(R.id.iv_album_cover));

            ((TextView) fd(R.id.tv_track_name)).setText(currSoundIgnoreKind.getTrackTitle());
            ((TextView) fd(R.id.tv_announcer_name)).setText(currSoundIgnoreKind.getAnnouncer().getNickname());
            String vsignature = currSoundIgnoreKind.getAnnouncer().getVsignature();
            if(TextUtils.isEmpty(vsignature)){
                fd(R.id.tv_vsignature).setVisibility(View.GONE);
            }else {
                ((TextView) fd(R.id.tv_vsignature)).setText(vsignature);
            }
            ((TextView) fd(R.id.tv_following_count)).setText(getString(R.string.following_count,
                    ZhumulangmaUtil.toWanYi(currSoundIgnoreKind.getAnnouncer().getFollowingCount())));
            fd(R.id.tv_vsignature).setVisibility(currSoundIgnoreKind.getAnnouncer().isVerified()?View.VISIBLE:View.GONE);

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
            ((TextView) fd(R.id.tv_current)).setText(ZhumulangmaUtil.secondToTimeE(
                    XmPlayerManager.getInstance(mContext).getPlayCurrPositon()/1000));
            mViewModel.getRelativeAlbums(String.valueOf(mTrack.getDataId()));
        }
    }

    @Override
    public void initViewObservable() {
        mViewModel.getAlbumSingleLiveEvent().observe(this, albums -> mAlbumAdapter.addData(albums));
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
                navigateBean.launchMode=STANDARD;
                EventBus.getDefault().post(new BaseActivityEvent<>(
                        EventCode.MainCode.NAVIGATE, navigateBean));
            }
        } else if (R.id.iv_pre == id) {
            XmPlayerManager.getInstance(mContext).playPre();
        } else if (R.id.iv_next == id) {
            XmPlayerManager.getInstance(mContext).playNext();
        } else if (R.id.fl_play_pause == id) {
            if (XmPlayerManager.getInstance(mContext).isPlaying()) {
                XmPlayerManager.getInstance(mContext).pause();
            } else {
                XmPlayerManager.getInstance(mContext).play();
            }
        }
    }

    @Override
    public void onPlayStart() {
      /*  if(!XmPlayerManager.getInstance(mContext).isPlaying()){
        }*/
        playAnim();

    }

    private void playAnim() {
        lavPlayPause.setMinAndMaxFrame(55,90);
        lavPlayPause.loop(false);
        lavPlayPause.playAnimation();
        lavPlayPause.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lavPlayPause.setMinAndMaxFrame(90,170);
                lavPlayPause.loop(true);
                lavPlayPause.playAnimation();
                lavPlayPause.removeAnimatorListener(this);
            }
        });
    }

    @Override
    public void onPlayPause() {
        pauseAnim();
    }

    private void pauseAnim() {
        lavPlayPause.setMinAndMaxFrame(180,210);
        lavPlayPause.loop(false);
        lavPlayPause.playAnimation();
    }

    @Override
    public void onPlayStop() {
        pauseAnim();
    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {

    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        initData();
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
        ((TextView) fd(R.id.tv_current)).setText(ZhumulangmaUtil.secondToTimeE(i/1000));
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XmPlayerManager.getInstance(mContext).removePlayerStatusListener(this);
    }

    @Override
    public Class<PlayTrackViewModel> onBindViewModel() {
        return PlayTrackViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return  ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getData().get(position).getId())
                .navigation();
        NavigateBean navigateBean = new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation);
        navigateBean.launchMode=STANDARD;
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.MainCode.NAVIGATE,navigateBean));
    }
}
