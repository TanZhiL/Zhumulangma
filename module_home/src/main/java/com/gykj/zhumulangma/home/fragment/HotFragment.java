package com.gykj.zhumulangma.home.fragment;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TBannerImageAdapter;
import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.HotSkeleton;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.activity.AlbumListActivity;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.adapter.HotLikeAdapter;
import com.gykj.zhumulangma.home.adapter.HotMusicAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentHotBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HotViewModel;
import com.kingja.loadsir.callback.Callback;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.HOT_COLUMN_ID;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:热门
 */
public class HotFragment extends BaseRefreshFragment<HomeFragmentHotBinding, HotViewModel, Album>
        implements OnBannerListener, View.OnClickListener {

    private HotLikeAdapter mLikeAdapter;
    private AlbumAdapter mStoryAdapter;
    private AlbumAdapter mBabyAdapter;
    private HotMusicAdapter mMusicAdapter;
    private AlbumAdapter mColumnAdapter;
    private String mColumnTitle;

    public HotFragment() {

    }


    @Override
    public int onBindLayout() {
        return R.layout.home_fragment_hot;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initView() {
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

        mBinding.llYy.setOnClickListener(this);
        mBinding.llGxxy.setOnClickListener(this);
        mBinding.llQgsh.setOnClickListener(this);
        mBinding.llRw.setOnClickListener(this);
        mBinding.llIt.setOnClickListener(this);
        mBinding.llEnglish.setOnClickListener(this);

        mBinding.likeRefresh.setOnClickListener(this);
        mBinding.layoutAd.setOnClickListener(this);
        mBinding.ihLike.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.LIKE)
                        .withString(KeyCode.Home.TITLE, "猜你喜欢")));
        mBinding.storyRefresh.setOnClickListener(this);
        mBinding.ihStory.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, 3)
                        .withString(KeyCode.Home.TITLE, "有声小说")));
        mBinding.babyRefresh.setOnClickListener(this);
        mBinding.ihBaby.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, 6)
                        .withString(KeyCode.Home.TITLE, "宝贝最爱")));
        mBinding.musicRefresh.setOnClickListener(this);
        mBinding.ihMusic.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, 2)
                        .withString(KeyCode.Home.TITLE, "音乐好时光")));
        mBinding.ihRadio.setOnClickListener(view ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                        .withString(KeyCode.Home.COLUMN, HOT_COLUMN_ID)
                        .withString(KeyCode.Home.TITLE, mColumnTitle)));

        mBinding.radioRefresh.setOnClickListener(this);
        mBinding.topicRefresh.setOnClickListener(this);
        mLikeAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mLikeAdapter.getItem(position).getId())));
        mStoryAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mStoryAdapter.getItem(position).getId())));
        mBabyAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mBabyAdapter.getItem(position).getId())));
        mMusicAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mMusicAdapter.getItem(position).getId())));
        mColumnAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mColumnAdapter.getItem(position).getId())));
        mBinding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int bottom = mBinding.banner.getBottom();
            if (scrollY > bottom) {
                mBinding.banner.stop();
            } else {
                mBinding.banner.start();
            }
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
        mBinding.banner.addBannerLifecycleObserver(this);
        mBinding.banner.setIndicator(new CircleIndicator(mActivity));
        mBinding.banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT);
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
        mColumnAdapter = new AlbumAdapter(R.layout.home_item_album);
        mBinding.rvRadio.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvRadio.setHasFixedSize(true);
        mColumnAdapter.bindToRecyclerView(mBinding.rvRadio);
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
        mViewModel.getBannerEvent().observe(this, bannerV2s -> {
            mBinding.banner.setAdapter(new TBannerImageAdapter(bannerV2s));
            mBinding.banner.setOnBannerListener(this);
        });
        mViewModel.getLikesEvent().observe(this, albums -> mLikeAdapter.setNewData(albums));
        mViewModel.getStorysEvent().observe(this, albums -> mStoryAdapter.setNewData(albums));
        mViewModel.getBadysEvent().observe(this, albums -> mBabyAdapter.setNewData(albums));
        mViewModel.getMusicsEvent().observe(this, albums -> mMusicAdapter.setNewData(albums));
        mViewModel.getColumnEvent().observe(this, radios -> mColumnAdapter.setNewData(radios));
        mViewModel.getColumnNameEvent().observe(this, s -> {
            mColumnTitle = s;
            mBinding.ihRadio.setTitle(s);
        });
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
                if (mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
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
            mViewModel.getColumnList();
        } else if (id == R.id.fl_rank) {
            RouteHelper.navigateTo(Constants.Router.Home.F_RANK);
        } else if (id == R.id.ll_yy) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 2)
                    .withString(KeyCode.Home.TITLE, "音乐"));
        } else if (id == R.id.ll_it) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 18)
                    .withString(KeyCode.Home.TITLE, "IT科技"));
        } else if (id == R.id.ll_qgsh) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 10)
                    .withString(KeyCode.Home.TITLE, "情感生活"));
        } else if (id == R.id.ll_rw) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 39)
                    .withString(KeyCode.Home.TITLE, "人文"));
        } else if (id == R.id.ll_english) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 38)
                    .withString(KeyCode.Home.TITLE, "英语"));
        }else if (id == R.id.ll_gxxy) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 40)
                    .withString(KeyCode.Home.TITLE, "国学学院"));
        } else if (id == R.id.layout_ad) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Discover.F_WEB)
                    .withString(KeyCode.Discover.PATH, "https://m.ximalaya.com/"));
        }
    }

    @Override
    public void OnBannerClick(Object data, int position) {
        BannerBean bannerV2 = mViewModel.getBannerEvent().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getBannerContentId()));
                break;
            case 3:
                mViewModel.playTrack(bannerV2.getBannerContentId());
                break;
            case 1:
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, bannerV2.getBannerContentId()));
            case 4:
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Discover.F_WEB)
                        .withLong(KeyCode.Discover.PATH, bannerV2.getBannerContentId()));
                break;
        }
    }
}
