package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.common.util.log.TLog;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.HotLikeAdapter;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.adapter.HotTopicAdapter;
import com.gykj.zhumulangma.home.adapter.HotMusicAdapter;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HotViewModel;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

public class HotFragment extends BaseMvvmFragment<HotViewModel> implements OnBannerListener, View.OnClickListener {

    private Banner banner;
    private RecyclerView rvTopic;
    private HotTopicAdapter mTopicAdapter;

    private RecyclerView rvLike;
    private HotLikeAdapter mLikeAdapter;

    private RecyclerView rvStory;
    private AlbumAdapter mStoryAdapter;

    private RecyclerView rvBaby;
    private AlbumAdapter mBabyAdapter;

    private RecyclerView rvMusic;
    private HotMusicAdapter mMusicAdapter;

    private RecyclerView rvRadio;
    private RadioAdapter mRadioAdapter;

    private View flRank;

    public HotFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_hot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        initAction();
        initBanner();
        initLike();
        initStory();
        initBaby();
        initMusic();
        initRadio();
        initTopic();
    }

    private void initAction() {
        flRank = fd(R.id.fl_rank);

    }

    @Override
    public void initListener() {
        super.initListener();
        flRank.setOnClickListener(this);
        fd(R.id.like_refresh).setOnClickListener(this);
        fd(R.id.ih_like).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, AlbumListFragment.LIKE)
                    .withString(KeyCode.Home.TITLE, "猜你喜欢")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));

        });
        fd(R.id.story_refresh).setOnClickListener(this);
        fd(R.id.ih_story).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, 3)
                    .withString(KeyCode.Home.TITLE, "有声小说")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
        fd(R.id.baby_refresh).setOnClickListener(this);
        fd(R.id.ih_baby).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, 6)
                    .withString(KeyCode.Home.TITLE, "宝贝最爱")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
        fd(R.id.music_refresh).setOnClickListener(this);
        fd(R.id.ih_music).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, 2)
                    .withString(KeyCode.Home.TITLE, "音乐好时光")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
        fd(R.id.ih_radio).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.INTERNET)
                    .withString(KeyCode.Home.TITLE, "网络台")
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        });

        fd(R.id.radio_refresh).setOnClickListener(this);
        fd(R.id.topic_refresh).setOnClickListener(this);
        mLikeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mLikeAdapter.getData().get(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        });
        mStoryAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mStoryAdapter.getData().get(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        });
        mBabyAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mBabyAdapter.getData().get(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        });
        mMusicAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mMusicAdapter.getData().get(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        });
        mRadioAdapter.setOnItemClickListener((adapter, view, position) -> {
            RadioUtil.getInstance(mContext).playLiveRadioForSDK(mRadioAdapter.getData().get(position));
            navigateTo(AppConstants.Router.Home.F_PLAY_RADIIO);
        });
    }


    @Override
    public void initData() {
        mViewModel.getBannerList();
        mViewModel.getGussLikeList();
        mViewModel.getHotStoryList();
        mViewModel.getHotBabyList();
        mViewModel.getHotMusicList();
        mViewModel.getRadioList();
    //    mViewModel.getTopicList();
    }

    private void initBanner() {
        banner = fd(R.id.banner);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setDelayTime(3000);
        banner.setOnBannerListener(this);
    }

    private void initLike() {
        rvLike = fd(R.id.rv_like);
        mLikeAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        rvLike.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvLike.setHasFixedSize(true);
        mLikeAdapter.bindToRecyclerView(rvLike);

    }

    private void initStory() {

        rvStory = fd(R.id.rv_story);
        mStoryAdapter = new AlbumAdapter(R.layout.home_item_album);
        rvStory.setLayoutManager(new LinearLayoutManager(mContext));
        rvStory.setHasFixedSize(true);
        mStoryAdapter.bindToRecyclerView(rvStory);
    }

    private void initBaby() {
        rvBaby = fd(R.id.rv_baby);
        mBabyAdapter = new AlbumAdapter(R.layout.home_item_album);
        rvBaby.setLayoutManager(new LinearLayoutManager(mContext));
        rvBaby.setHasFixedSize(true);
        mBabyAdapter.bindToRecyclerView(rvBaby);

    }

    private void initMusic() {
        rvMusic = fd(R.id.rv_music);
        mMusicAdapter = new HotMusicAdapter(R.layout.home_item_hot_music);
        rvMusic.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvMusic.setHasFixedSize(true);
        mMusicAdapter.bindToRecyclerView(rvMusic);

    }

    private void initRadio() {

        rvRadio = fd(R.id.rv_radio);
        mRadioAdapter = new RadioAdapter(R.layout.home_item_radio);
        rvRadio.setLayoutManager(new LinearLayoutManager(mContext));
        rvRadio.setHasFixedSize(true);
        mRadioAdapter.bindToRecyclerView(rvRadio);

    }

    private void initTopic() {


        rvTopic = fd(R.id.rv_topic);
        mTopicAdapter = new HotTopicAdapter(R.layout.home_item_hot_topic);
        rvTopic.setLayoutManager(new LinearLayoutManager(mContext));
        rvTopic.setHasFixedSize(true);
        mTopicAdapter.bindToRecyclerView(rvTopic);

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public Class<HotViewModel> onBindViewModel() {
        return HotViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getBannerV2SingleLiveEvent().observe(this, bannerV2s -> {
            List<String> images = new ArrayList<>();
            for (BannerV2 bannerV2 : bannerV2s) {
                images.add(bannerV2.getBannerUrl());
            }
            banner.setImages(images).setImageLoader(new MainHomeFragment.GlideImageLoader()).start();
        });
        mViewModel.getLikeSingleLiveEvent().observe(this, albums -> mLikeAdapter.setNewData(albums));
        mViewModel.getStorySingleLiveEvent().observe(this, albums -> mStoryAdapter.setNewData(albums));
        mViewModel.getBadySingleLiveEvent().observe(this, albums -> mBabyAdapter.setNewData(albums));
        mViewModel.getMusicSingleLiveEvent().observe(this, albums -> mMusicAdapter.setNewData(albums));
        mViewModel.getRadioSingleLiveEvent().observe(this, radios -> mRadioAdapter.setNewData(radios));
        mViewModel.getTopicSingleLiveEvent().observe(this, columns -> mTopicAdapter.setNewData(columns));
    }

    @Override
    public void OnBannerClick(int position) {
        BannerV2 bannerV2 = mViewModel.getBannerV2SingleLiveEvent().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getAlbumId())
                        .navigation();
                EventBus.getDefault().post(new BaseActivityEvent<>(
                        EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
                break;
            case 3:
                mViewModel.play(bannerV2.getTrackId());

                break;
            case 1:
                Object navigation1 = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID,bannerV2.getBannerUid())
                        .navigation();
                EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.NAVIGATE,
                        new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation1)));

                break;
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (banner != null)
            banner.startAutoPlay();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (banner != null)
            banner.stopAutoPlay();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.story_refresh) {
            mViewModel.getHotStoryList();
        } else if (id == R.id.baby_refresh) {
            mViewModel.getHotBabyList();
        } else if (id == R.id.topic_refresh) {
            mViewModel.getTopicList();
        } else if (id == R.id.music_refresh) {
            mViewModel.getHotMusicList();
        } else if (id == R.id.radio_refresh) {
            mViewModel.getRadioList();
        } else if (id == R.id.fl_rank) {
            navigateTo(AppConstants.Router.Home.F_RANK);
        }
    }
}
