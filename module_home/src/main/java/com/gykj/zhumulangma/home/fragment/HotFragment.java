package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.extra.GlideImageLoader;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.HotSkeleton;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.adapter.HotLikeAdapter;
import com.gykj.zhumulangma.home.adapter.HotMusicAdapter;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HotViewModel;
import com.kingja.loadsir.callback.Callback;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;
/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:热门
 */
public class HotFragment extends BaseRefreshMvvmFragment<HotViewModel, Album> implements OnBannerListener,
        View.OnClickListener {

    private HotLikeAdapter mLikeAdapter;
    private AlbumAdapter mStoryAdapter;
    private AlbumAdapter mBabyAdapter;
    private HotMusicAdapter mMusicAdapter;
    private RadioAdapter mRadioAdapter;

    private Banner banner;
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
        mView.setBackground(null);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        flRank = fd(R.id.fl_rank);
        initBanner();
        initLike();
        initStory();
        initBaby();
        initMusic();
        initRadio();
    }


    @Override
    public void initListener() {
        super.initListener();
        flRank.setOnClickListener(this);
        fd(R.id.like_refresh).setOnClickListener(this);
        fd(R.id.layout_ad).setOnClickListener(this);
        fd(R.id.ih_like).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, AlbumListFragment.LIKE)
                    .withString(KeyCode.Home.TITLE, "猜你喜欢")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));

        });
        fd(R.id.story_refresh).setOnClickListener(this);
        fd(R.id.ih_story).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, 3)
                    .withString(KeyCode.Home.TITLE, "有声小说")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
        fd(R.id.baby_refresh).setOnClickListener(this);
        fd(R.id.ih_baby).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, 6)
                    .withString(KeyCode.Home.TITLE, "宝贝最爱")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
        fd(R.id.music_refresh).setOnClickListener(this);
        fd(R.id.ih_music).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, 2)
                    .withString(KeyCode.Home.TITLE, "音乐好时光")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
        fd(R.id.ih_radio).setOnClickListener(view -> {
            Object o = ARouter.getInstance().build(Constants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, RadioListFragment.INTERNET)
                    .withString(KeyCode.Home.TITLE, "网络台")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        });

        fd(R.id.radio_refresh).setOnClickListener(this);
        fd(R.id.topic_refresh).setOnClickListener(this);
        mLikeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mLikeAdapter.getItem(position).getId())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        });
        mStoryAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mStoryAdapter.getItem(position).getId())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        });
        mBabyAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mBabyAdapter.getItem(position).getId())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        });
        mMusicAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mMusicAdapter.getItem(position).getId())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        });
        mRadioAdapter.setOnItemClickListener((adapter, view, position) -> {
            mViewModel.playRadio(mRadioAdapter.getItem(position));
        });
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh( fd(R.id.refreshLayout),null);
    }

    @Override
    public void initData() {
        mViewModel.init();
    }

    private void initBanner() {
        banner = fd(R.id.banner);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setDelayTime(3000);
        banner.setOnBannerListener(this);
    }

    private void initLike() {
        RecyclerView rvLike = fd(R.id.rv_like);
        mLikeAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        rvLike.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rvLike.setHasFixedSize(true);
        mLikeAdapter.bindToRecyclerView(rvLike);

    }

    private void initStory() {
        RecyclerView rvStory = fd(R.id.rv_story);
        mStoryAdapter = new AlbumAdapter(R.layout.home_item_album);
        rvStory.setLayoutManager(new LinearLayoutManager(mActivity));
        rvStory.setHasFixedSize(true);
        mStoryAdapter.bindToRecyclerView(rvStory);
    }

    private void initBaby() {
        RecyclerView rvBaby = fd(R.id.rv_baby);
        mBabyAdapter = new AlbumAdapter(R.layout.home_item_album);
        rvBaby.setLayoutManager(new LinearLayoutManager(mActivity));
        rvBaby.setHasFixedSize(true);
        mBabyAdapter.bindToRecyclerView(rvBaby);

    }

    private void initMusic() {
        RecyclerView rvMusic = fd(R.id.rv_music);
        mMusicAdapter = new HotMusicAdapter(R.layout.home_item_hot_music);
        rvMusic.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rvMusic.setHasFixedSize(true);
        mMusicAdapter.bindToRecyclerView(rvMusic);

    }

    private void initRadio() {
        RecyclerView rvRadio = fd(R.id.rv_radio);
        mRadioAdapter = new RadioAdapter(R.layout.home_item_radio);
        rvRadio.setLayoutManager(new LinearLayoutManager(mActivity));
        rvRadio.setHasFixedSize(true);
        mRadioAdapter.bindToRecyclerView(rvRadio);

    }

    @Override
    public boolean enableSimplebar() {
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
        mViewModel.getBannerV2Event().observe(this, bannerV2s -> {
            List<String> images = new ArrayList<>();
            for (BannerV2 bannerV2 : bannerV2s) {
                images.add(bannerV2.getBannerUrl());
            }
            banner.setImages(images).setImageLoader(new GlideImageLoader()).start();
        });
        mViewModel.getLikesEvent().observe(this, albums -> mLikeAdapter.setNewData(albums));
        mViewModel.getStorysEvent().observe(this, albums -> mStoryAdapter.setNewData(albums));
        mViewModel.getBadysEvent().observe(this, albums -> mBabyAdapter.setNewData(albums));
        mViewModel.getMusicsEvent().observe(this, albums -> mMusicAdapter.setNewData(albums));
        mViewModel.getRadiosEvent().observe(this, radios -> mRadioAdapter.setNewData(radios));
    }

    @Override
    public void OnBannerClick(int position) {
        BannerV2 bannerV2 = mViewModel.getBannerV2Event().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getAlbumId())
                        .navigation();
                EventBus.getDefault().post(new ActivityEvent(
                        EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
                break;
            case 3:
                mViewModel.playTrack(bannerV2.getTrackId());

                break;
            case 1:
                Object navigation1 = ARouter.getInstance().build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, bannerV2.getBannerUid())
                        .navigation();
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                        new NavigateBean(Constants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation1)));

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
    protected boolean enableLazy() {
        return true;
    }

    @Override
    public Callback getInitStatus() {
        return new HotSkeleton();
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Home.TAB_REFRESH:
                if(isSupportVisible()&&mBaseLoadService.getCurrentCallback()!= getInitStatus().getClass()){
                    fd(R.id.nsv).scrollTo(0,0);
                    ((SmartRefreshLayout)fd(R.id.refreshLayout)).autoRefresh();
                }
                break;
            case EventCode.Main.LOGINSUCC:
                fd(R.id.nsv).scrollTo(0,0);
                ((SmartRefreshLayout)fd(R.id.refreshLayout)).autoRefresh();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.story_refresh) {
            mViewModel.getHotStoryList();
        } else if (id == R.id.baby_refresh) {
            mViewModel.getHotBabyList();
        } else if (id == R.id.music_refresh) {
            mViewModel.getHotMusicList();
        } else if (id == R.id.radio_refresh) {
            mViewModel.getRadioList();
        } else if (id == R.id.fl_rank) {
            RouteUtil.navigateTo(Constants.Router.Home.F_RANK);
        }else if (id == R.id.layout_ad) {
            Object navigation = ARouter.getInstance().build(Constants.Router.Discover.F_WEB)
                    .withString(KeyCode.Discover.PATH, "https://m.ximalaya.com/")
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Discover.F_WEB, (ISupportFragment) navigation)));
        }
    }
}
