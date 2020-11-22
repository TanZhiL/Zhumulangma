package com.gykj.zhumulangma.home.fragment;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TBannerImageAdapter;
import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.activity.AlbumListActivity;
import com.gykj.zhumulangma.home.adapter.HotLikeAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentFineBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.ChildViewModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_DHSJ_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_GXJD_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_JDGS_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_JZZQ_ID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.CHILD_QZEG_ID;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:精品
 */
public class ChildFragment extends BaseRefreshFragment<HomeFragmentFineBinding, ChildViewModel, Album> implements
        View.OnClickListener, OnBannerListener {

    private HotLikeAdapter mJDGSAdapter;
    private HotLikeAdapter mDHSJAdapter;
    private HotLikeAdapter mGXJDAdapter;
    private HotLikeAdapter mQZEGAdapter;
    private HotLikeAdapter mJZZQAdapter;
    private String mJDGSName;
    private String mDHSJame;
    private String mGXJDName;
    private String mQZEGName;
    private String mJZZQName;

    public ChildFragment() {

    }


    @Override
    public int onBindLayout() {
        return R.layout.home_fragment_fine;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initView() {
        initBanner();
        initJDGS();
        initDHSJ();
        initGXJD();
        initQZEG();
        initJZZQ();
    }

    private void initJDGS() {
        mJDGSAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvDaily.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvDaily.setHasFixedSize(true);
        mJDGSAdapter.bindToRecyclerView(mBinding.rvDaily);
    }

    private void initDHSJ() {
        mDHSJAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvBook.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvBook.setHasFixedSize(true);
        mDHSJAdapter.bindToRecyclerView(mBinding.rvBook);
    }


    private void initGXJD() {
        mGXJDAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvClassroom.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvClassroom.setHasFixedSize(true);
        mGXJDAdapter.bindToRecyclerView(mBinding.rvClassroom);
    }
    private void initQZEG() {
        mQZEGAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvSing.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvSing.setHasFixedSize(true);
        mQZEGAdapter.bindToRecyclerView(mBinding.rvSing);
    } 
    private void initJZZQ() {
        mJZZQAdapter = new HotLikeAdapter(R.layout.home_item_hot_like);
        mBinding.rvParent.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.rvParent.setHasFixedSize(true);
        mJZZQAdapter.bindToRecyclerView(mBinding.rvParent);
    }
    @Override
    public void initListener() {
        super.initListener();
        mBinding.dailyRefresh.setOnClickListener(this);
        mBinding.ihDaily.setOnClickListener(v ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                .withString(KeyCode.Home.COLUMN, CHILD_JDGS_ID)
                .withString(KeyCode.Home.TITLE, mJDGSName)));
        mBinding.bookRefresh.setOnClickListener(this);
        mBinding.ihBook.setOnClickListener(v ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                .withString(KeyCode.Home.COLUMN, CHILD_DHSJ_ID)
                .withString(KeyCode.Home.TITLE, mDHSJame)));
        mBinding.classroomRefresh.setOnClickListener(this);
        mBinding.ihClassroom.setOnClickListener(v ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                .withString(KeyCode.Home.COLUMN, CHILD_GXJD_ID)
                .withString(KeyCode.Home.TITLE, mGXJDName)));
        mBinding.ihSing.setOnClickListener(v ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                .withString(KeyCode.Home.COLUMN, CHILD_QZEG_ID)
                .withString(KeyCode.Home.TITLE, mQZEGName)));
        mBinding.ihParent.setOnClickListener(v ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                .withString(KeyCode.Home.COLUMN, CHILD_JZZQ_ID)
                .withString(KeyCode.Home.TITLE, mJZZQName)));

        mBinding.llPhb.setOnClickListener(this);
        mBinding.llGs.setOnClickListener(this);
        mBinding.llHs.setOnClickListener(this);
        mBinding.llEg.setOnClickListener(this);
        mBinding.llDh.setOnClickListener(this);
        mBinding.llXk.setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, null);
    }


    @Override
    public void initData() {
        mViewModel.init();
        String notice = "本页面为付费内容,目前仅提供浏览功能,暂时不可操作!";
        mBinding.marqueeView.setContent(notice);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getBannerV2Event().observe(this, bannerV2s -> {
            mBinding.banner.setAdapter(new TBannerImageAdapter(bannerV2s));
            mBinding.banner.setOnBannerListener(this);
        });
        mViewModel.getJDGSEvent().observe(this, albums -> {
            mJDGSAdapter.setNewData(albums);
        });
        mViewModel.getDHSJEvent().observe(this, albums -> mDHSJAdapter.setNewData(albums));
        mViewModel.getGXJDEvent().observe(this, albums -> mGXJDAdapter.setNewData(albums));
        mViewModel.getSingEvent().observe(this, albums -> mQZEGAdapter.setNewData(albums));
        mViewModel.getParentEvent().observe(this, albums -> mJZZQAdapter.setNewData(albums));
        mViewModel.getJDGSNameEvent().observe(this, s -> {
            mJDGSName = s;
            mBinding.ihDaily.setTitle(s);
        });
        mViewModel.getDHSJNameEvent().observe(this, s -> {
            mDHSJame = s;
            mBinding.ihBook.setTitle(s);
        });
        mViewModel.getGXJDNameEvent().observe(this, s -> {
            mGXJDName = s;
            mBinding.ihClassroom.setTitle(s);
        });
        mViewModel.getQZEGNameEvent().observe(this, s -> {
            mQZEGName = s;
            mBinding.ihSing.setTitle(s);
        });
        mViewModel.getJZZQNameEvent().observe(this, s -> {
            mJZZQName = s;
            mBinding.ihParent.setTitle(s);
        });
    }

    private void initBanner() {
        mBinding.banner.addBannerLifecycleObserver(this);
        mBinding.banner.setIndicator(new CircleIndicator(mActivity));
        mBinding.banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT);
        mBinding.banner.setOnBannerListener(this);
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public Class<ChildViewModel> onBindViewModel() {
        return ChildViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.daily_refresh == id) {
            mViewModel.getJDGSList();
        } else if (R.id.book_refresh == id) {
            mViewModel.getDHSJList();
        } else if (R.id.classroom_refresh == id) {
            mViewModel.getGXJDList();
        } else if (R.id.sing_refresh == id) {
            mViewModel.getSingList();
        } else if (R.id.parent_refresh == id) {
            mViewModel.getParentList();
        }else if (R.id.ll_phb == id) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                .withInt(KeyCode.Home.CATEGORY, 6)
                .withString(KeyCode.Home.TITLE, "排行榜"));
        }else if (R.id.ll_gs == id) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 6)
                    .withString(KeyCode.Home.TAG, "故事")
                    .withString(KeyCode.Home.TITLE, "故事"));
        }else if (R.id.ll_hs == id) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 6)
                    .withString(KeyCode.Home.TAG, "哄睡")
                    .withString(KeyCode.Home.TITLE, "哄睡"));
        }else if (R.id.ll_eg == id) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 6)
                    .withString(KeyCode.Home.TAG, "儿歌")
                    .withString(KeyCode.Home.TITLE, "儿歌"));
        }else if (R.id.ll_dh == id) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 6)
                    .withString(KeyCode.Home.TAG, "动画")
                    .withString(KeyCode.Home.TITLE, "动画"));
        }else if (R.id.ll_xk == id) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.CATEGORY, 6)
                    .withString(KeyCode.Home.TAG, "趣学科学")
                    .withString(KeyCode.Home.TITLE, "学科"));
        }
    }

    @Override
    protected void onRevisible() {
        super.onRevisible();
        if (mBinding != null) {
            mBinding.marqueeView.continueRoll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding != null) {
            mBinding.marqueeView.stopRoll();
        }
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
        }
    }

    @Override
    public void OnBannerClick(Object data, int position) {
        BannerBean bannerV2 = mViewModel.getBannerV2Event().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getBannerContentId()));
                break;
            case 3:
                mViewModel.play(bannerV2.getBannerContentId());
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
