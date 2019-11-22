package com.gykj.zhumulangma.home.fragment;


import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.extra.GlideImageLoader;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.HotSkeleton;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.adapter.HotLikeAdapter;
import com.gykj.zhumulangma.home.adapter.HotMusicAdapter;
import com.gykj.zhumulangma.home.adapter.RadioAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentHotBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HotViewModel;
import com.kingja.loadsir.callback.Callback;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:热门
 */
public class HotFragment extends BaseRefreshMvvmFragment<HomeFragmentHotBinding, HotViewModel, Album> implements OnBannerListener,
        View.OnClickListener {

    private HotLikeAdapter mLikeAdapter;
    private AlbumAdapter mStoryAdapter;
    private AlbumAdapter mBabyAdapter;
    private HotMusicAdapter mMusicAdapter;
    private RadioAdapter mRadioAdapter;

    public HotFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_hot;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void initView() {
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
        mBinding.flRank.setOnClickListener(this);
        mBinding.likeRefresh.setOnClickListener(this);
        mBinding.layoutAd.setOnClickListener(this);
        mBinding.ihLike.setOnClickListener(view ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.TYPE, AlbumListFragment.LIKE)
                        .withString(KeyCode.Home.TITLE, "猜你喜欢")));
        mBinding.storyRefresh.setOnClickListener(this);
        mBinding.ihStory.setOnClickListener(view ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.TYPE, 3)
                        .withString(KeyCode.Home.TITLE, "有声小说")));
        mBinding.babyRefresh.setOnClickListener(this);
        mBinding.ihBaby.setOnClickListener(view ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.TYPE, 6)
                        .withString(KeyCode.Home.TITLE, "宝贝最爱")));
        mBinding.musicRefresh.setOnClickListener(this);
        mBinding.ihMusic.setOnClickListener(view ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.TYPE, 2)
                        .withString(KeyCode.Home.TITLE, "音乐好时光")));
        mBinding.ihRadio.setOnClickListener(view ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_RADIO_LIST)
                        .withInt(KeyCode.Home.TYPE, RadioListFragment.INTERNET)
                        .withString(KeyCode.Home.TITLE, "网络台")));

        mBinding.radioRefresh.setOnClickListener(this);
        mBinding.topicRefresh.setOnClickListener(this);
        mLikeAdapter.setOnItemClickListener((adapter, view, position) ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mLikeAdapter.getItem(position).getId())));
        mStoryAdapter.setOnItemClickListener((adapter, view, position) ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mStoryAdapter.getItem(position).getId())));
        mBabyAdapter.setOnItemClickListener((adapter, view, position) ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mBabyAdapter.getItem(position).getId())));
        mMusicAdapter.setOnItemClickListener((adapter, view, position) ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mMusicAdapter.getItem(position).getId())));
        mRadioAdapter.setOnItemClickListener((adapter, view, position) -> {
            mViewModel.playRadio(mRadioAdapter.getItem(position));
        });
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, null);
    }

    @Override
    public void initData() {
        mViewModel.init();
    }

    private void initBanner() {
        mBinding.banner.setIndicatorGravity(BannerConfig.RIGHT);
        mBinding.banner.setDelayTime(3000);
        mBinding.banner.setOnBannerListener(this);
    }

    private void initLike() {
        mLikeAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvLike.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvLike.setHasFixedSize(true);
        mLikeAdapter.bindToRecyclerView(mBinding.rvLike);

    }

    private void initStory() {
        mStoryAdapter = new AlbumAdapter(R.layout.home_item_album);
        mBinding.rvStory.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvStory.setHasFixedSize(true);
        mStoryAdapter.bindToRecyclerView(mBinding.rvStory);
    }

    private void initBaby() {
        mBabyAdapter = new AlbumAdapter(R.layout.home_item_album);
        mBinding.rvBaby.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvBaby.setHasFixedSize(true);
        mBabyAdapter.bindToRecyclerView(mBinding.rvBaby);

    }

    private void initMusic() {
        mMusicAdapter = new HotMusicAdapter(R.layout.home_item_hot_music);
        mBinding.rvMusic.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvMusic.setHasFixedSize(true);
        mMusicAdapter.bindToRecyclerView(mBinding.rvMusic);

    }

    private void initRadio() {
        mRadioAdapter = new RadioAdapter(R.layout.home_item_radio);
        mBinding.rvRadio.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvRadio.setHasFixedSize(true);
        mRadioAdapter.bindToRecyclerView(mBinding.rvRadio);

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
            mBinding.banner.setImages(images).setImageLoader(new GlideImageLoader()).start();
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
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getAlbumId()));
                break;
            case 3:
                mViewModel.playTrack(bannerV2.getTrackId());

                break;
            case 1:
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, bannerV2.getBannerUid()));
                break;
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (null != mBinding)
            mBinding.banner.startAutoPlay();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (null != mBinding)
            mBinding.banner.stopAutoPlay();
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
        switch (event.getCode()) {
            case EventCode.Home.TAB_REFRESH:
                if (isSupportVisible() && mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
                    mBinding.nsv.scrollTo(0, 0);
                    mBinding.refreshLayout.autoRefresh();
                }
                break;
            case EventCode.Main.LOGINSUCC:
                mViewModel.init();
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
            RouterUtil.navigateTo(Constants.Router.Home.F_RANK);
        } else if (id == R.id.layout_ad) {
            RouterUtil.navigateTo(mRouter.build(Constants.Router.Discover.F_WEB)
                    .withString(KeyCode.Discover.PATH, "https://m.ximalaya.com/"));
        }
    }
}
