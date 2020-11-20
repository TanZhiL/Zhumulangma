package com.gykj.zhumulangma.home.fragment;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TBannerImageAdapter;
import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.FineAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentFineBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.FineViewModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:精品
 */
public class FineFragment extends BaseRefreshFragment<HomeFragmentFineBinding, FineViewModel, Album> implements
        View.OnClickListener, OnBannerListener {

    private FineAdapter mDailyAdapter;
    private FineAdapter mBookAdapter;
    private FineAdapter mClassroomAdapter;


    public FineFragment() {

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
        initDaily();
        initBook();
        initClassRoom();
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.dailyRefresh.setOnClickListener(this);
        mBinding.bookRefresh.setOnClickListener(this);
        mBinding.classroomRefresh.setOnClickListener(this);
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
        mViewModel.getDailysEvent().observe(this, albums -> mDailyAdapter.setNewData(albums));
        mViewModel.getBooksEvent().observe(this, albums -> mBookAdapter.setNewData(albums));
        mViewModel.getClassRoomsEvent().observe(this, albums -> mClassroomAdapter.setNewData(albums));
    }

    private void initBanner() {
        mBinding.banner.addBannerLifecycleObserver(this);
        mBinding.banner.setIndicator(new CircleIndicator(mActivity));
        mBinding.banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT);
        mBinding.banner.setOnBannerListener(this);
    }

    private void initDaily() {

        mDailyAdapter = new FineAdapter(R.layout.home_item_fine);
        mBinding.rvDaily.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvDaily.setHasFixedSize(true);
        mDailyAdapter.bindToRecyclerView(mBinding.rvDaily);
    }

    private void initBook() {
        mBookAdapter = new FineAdapter(R.layout.home_item_fine);
        mBinding.rvBook.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvBook.setHasFixedSize(true);
        mBookAdapter.bindToRecyclerView(mBinding.rvBook);

    }

    private void initClassRoom() {
        mClassroomAdapter = new FineAdapter(R.layout.home_item_fine);
        mBinding.rvClassroom.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvClassroom.setHasFixedSize(true);
        mClassroomAdapter.bindToRecyclerView(mBinding.rvClassroom);
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public Class<FineViewModel> onBindViewModel() {
        return FineViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.daily_refresh == id) {
            mViewModel.getDailyList();
        } else if (R.id.book_refresh == id) {
            mViewModel.getBookList();
        } else if (R.id.classroom_refresh == id) {
            mViewModel.getClassRoomList();
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
        switch (bannerV2.getBanner_content_type()) {
            case 2:
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getBanner_content_id()));
                break;
            case 3:
                mViewModel.play(bannerV2.getBanner_content_id());
                break;
            case 1:
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, bannerV2.getBanner_content_id()));
            case 4:
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Discover.F_WEB)
                        .withLong(KeyCode.Discover.PATH, bannerV2.getBanner_content_id()));
                break;
        }
    }
}
