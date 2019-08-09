package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.HotLikeAdapter;
import com.gykj.zhumulangma.home.adapter.HotRadioAdapter;
import com.gykj.zhumulangma.home.adapter.HotStoryAdapter;
import com.gykj.zhumulangma.home.adapter.HotTopicAdapter;
import com.gykj.zhumulangma.home.adapter.MusicAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HotViewModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class HotFragment extends BaseMvvmFragment<HotViewModel> implements OnBannerListener, View.OnClickListener {

    private Banner banner;
    private RecyclerView rvTopic;
    private HotTopicAdapter mTopicAdapter;

    private RecyclerView rvLike;
    private HotLikeAdapter mLikeAdapter;

    private RecyclerView rvStory;
    private HotStoryAdapter mStoryAdapter;

    private RecyclerView rvBaby;
    private HotStoryAdapter mBabyAdapter;

    private RecyclerView rvMusic;
    private MusicAdapter mMusicAdapter;

    private RecyclerView rvRadio;
    private HotRadioAdapter mRadioAdapter;

    private View flRank;

    public HotFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_hot;
    }

    @Override
    protected void initView(View view) {
        setSwipeBackEnable(false);
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
        flRank.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getBannerList();
        mViewModel.getGussLikeList();
        mViewModel.getHotStoryList();
        mViewModel.getHotBabyList();
        mViewModel.getHotMusicList();
        mViewModel.getRadioList();
        mViewModel.getTopicList();
    }

    private void initBanner() {
        banner = fd(R.id.banner);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setDelayTime(3000);
        banner.setOnBannerListener(this);
    }

    private void initLike() {
        fd(R.id.like_refresh).setOnClickListener(this);
        fd(R.id.ih_like).setOnClickListener(view -> {

        });
        rvLike = fd(R.id.rv_like);

        mLikeAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        rvLike.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvLike.setHasFixedSize(true);
        mLikeAdapter.bindToRecyclerView(rvLike);

        mLikeAdapter.setOnItemClickListener((adapter, view, position) -> {
        });
    }

    private void initStory() {
        fd(R.id.story_refresh).setOnClickListener(this);
        fd(R.id.ih_story).setOnClickListener(view -> {
        });
        rvStory = fd(R.id.rv_story);
        mStoryAdapter = new HotStoryAdapter(R.layout.home_item_hot_story);
        rvStory.setLayoutManager(new LinearLayoutManager(mContext));
        rvStory.setHasFixedSize(true);
        mStoryAdapter.bindToRecyclerView(rvStory);
        mStoryAdapter.setOnItemClickListener((adapter, view, position) -> {
        });
    }

    private void initBaby() {
        fd(R.id.baby_refresh).setOnClickListener(this);
        fd(R.id.ih_baby).setOnClickListener(view -> {
        });
        rvBaby = fd(R.id.rv_baby);
        mBabyAdapter = new HotStoryAdapter(R.layout.home_item_hot_story);
        rvBaby.setLayoutManager(new LinearLayoutManager(mContext));
        rvBaby.setHasFixedSize(true);
        mBabyAdapter.bindToRecyclerView(rvBaby);
        mBabyAdapter.setOnItemClickListener((adapter, view, position) -> {
        });

    }

    private void initMusic() {
        fd(R.id.music_refresh).setOnClickListener(this);
        fd(R.id.ih_music).setOnClickListener(view -> {
        });
        rvMusic = fd(R.id.rv_music);
        mMusicAdapter = new MusicAdapter(R.layout.home_item_hot_music);
        rvMusic.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvMusic.setHasFixedSize(true);
        mMusicAdapter.bindToRecyclerView(rvMusic);
        mMusicAdapter.setOnItemClickListener((adapter, view, position) -> {
        });

    }

    private void initRadio() {
        fd(R.id.radio_refresh).setOnClickListener(this);
        rvRadio = fd(R.id.rv_radio);
        mRadioAdapter = new HotRadioAdapter(R.layout.home_item_hot_radio);
        rvRadio.setLayoutManager(new LinearLayoutManager(mContext));
        rvRadio.setHasFixedSize(true);
        mRadioAdapter.bindToRecyclerView(rvRadio);

    }

    private void initTopic() {

        fd(R.id.topic_refresh).setOnClickListener(this);

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

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if(banner!=null)
        banner.startAutoPlay();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if(banner!=null)
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
        }
    }
}
