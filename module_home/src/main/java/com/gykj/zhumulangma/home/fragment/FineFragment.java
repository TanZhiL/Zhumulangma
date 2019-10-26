package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.extra.GlideImageLoader;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.widget.MarqueeView;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.FineAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.FineViewModel;
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
 * <br/>Description:精品
 */
public class FineFragment extends BaseRefreshMvvmFragment<FineViewModel, Album> implements
        View.OnClickListener, OnBannerListener {

    private FineAdapter mDailyAdapter;
    private FineAdapter mBookAdapter;
    private FineAdapter mClassroomAdapter;

    private Banner banner;
    private MarqueeView marqueeView;

    public FineFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_fine;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackground(null);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        marqueeView=fd(R.id.marqueeView);
        initBanner();
        initDaily();
        initBook();
        initClassRoom();
    }

    @Override
    public void initListener() {
        super.initListener();
        banner.setOnBannerListener(this);
        fd(R.id.daily_refresh).setOnClickListener(this);
        fd(R.id.book_refresh).setOnClickListener(this);
        fd(R.id.classroom_refresh).setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(fd(R.id.refreshLayout), null);
    }


    @Override
    public void initData() {
        mViewModel.init();
        String notice = "本页面为付费内容,目前仅提供浏览功能,暂时不可操作!";
        marqueeView.setContent(notice);
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
        mViewModel.getDailysEvent().observe(this, albums -> mDailyAdapter.setNewData(albums));
        mViewModel.getBooksEvent().observe(this, albums -> mBookAdapter.setNewData(albums));
        mViewModel.getClassRoomsEvent().observe(this, albums -> mClassroomAdapter.setNewData(albums));
    }
    private void initBanner() {
        banner = fd(R.id.banner);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setDelayTime(3000);
    }

    private void initDaily() {

        RecyclerView rvDaily = fd(R.id.rv_daily);
        mDailyAdapter = new FineAdapter(R.layout.home_item_fine);
        rvDaily.setLayoutManager(new LinearLayoutManager(mActivity));
        rvDaily.setHasFixedSize(true);
        mDailyAdapter.bindToRecyclerView(rvDaily);
    }

    private void initBook() {
        RecyclerView rvBook = fd(R.id.rv_book);
        mBookAdapter = new FineAdapter(R.layout.home_item_fine);
        rvBook.setLayoutManager(new LinearLayoutManager(mActivity));
        rvBook.setHasFixedSize(true);
        mBookAdapter.bindToRecyclerView(rvBook);

    }

    private void initClassRoom() {
        RecyclerView rvClassroom = fd(R.id.rv_classroom);
        mClassroomAdapter = new FineAdapter(R.layout.home_item_fine);
        rvClassroom.setLayoutManager(new LinearLayoutManager(mActivity));
        rvClassroom.setHasFixedSize(true);
        mClassroomAdapter.bindToRecyclerView(rvClassroom);
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
    public void onSupportVisible() {
        super.onSupportVisible();
        if (banner != null){
            banner.startAutoPlay();
        }
        if(marqueeView!=null){
            marqueeView.continueRoll();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (banner != null){
            banner.stopAutoPlay();
        }
        if(marqueeView!=null){
            marqueeView.stopRoll();
        }
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Home.TAB_REFRESH:
                if (isSupportVisible() && mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
                    fd(R.id.nsv).scrollTo(0, 0);
                    ((SmartRefreshLayout) fd(R.id.refreshLayout)).autoRefresh();
                }
                break;
        }
    }

    @Override
    public void OnBannerClick(int position) {
        BannerV2 bannerV2 = mViewModel.getBannerV2Event().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getAlbumId())
                        .navigation();
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                        new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
                break;
            case 3:
                mViewModel.play(bannerV2.getTrackId());
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
}
