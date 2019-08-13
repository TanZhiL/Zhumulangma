package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.FineAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.FineViewModel;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;


public class FineFragment extends BaseMvvmFragment<FineViewModel> implements View.OnClickListener {

    Banner banner;

    RecyclerView rvDaily;
    FineAdapter mDailyAdapter;

    RecyclerView rvBook;
    FineAdapter mBookAdapter;

    RecyclerView rvClassroom;
    FineAdapter mClassroomAdapter;

    public FineFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_fine;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }
    @Override
    protected void initView(View view) {
        initBanner();
        initDaily();
        initBook();
        initClassRoom();
    }

    @Override
    public void initData() {
        mViewModel.getBannerList();
        mViewModel.getDailyList();
        mViewModel.getBookList();
        mViewModel.getClassRoomList();
    }

    private void initBanner() {
        banner = fd(R.id.banner);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setDelayTime(3000);
    }

    private void initDaily() {
        fd(R.id.daily_refresh).setOnClickListener(this);
        rvDaily = fd(R.id.rv_daily);
        mDailyAdapter = new FineAdapter(R.layout.home_item_fine);
        rvDaily.setLayoutManager(new LinearLayoutManager(mContext));
        rvDaily.setHasFixedSize(true);
        mDailyAdapter.bindToRecyclerView(rvDaily);
    }

    private void initBook() {
        fd(R.id.book_refresh).setOnClickListener(this);
        rvBook = fd(R.id.rv_book);
        mBookAdapter = new FineAdapter(R.layout.home_item_fine);
        rvBook.setLayoutManager(new LinearLayoutManager(mContext));
        rvBook.setHasFixedSize(true);
        mBookAdapter.bindToRecyclerView(rvBook);

    }

    private void initClassRoom() {
        fd(R.id.classroom_refresh).setOnClickListener(this);
        rvClassroom = fd(R.id.rv_classroom);
        mClassroomAdapter = new FineAdapter(R.layout.home_item_fine);
        rvClassroom.setLayoutManager(new LinearLayoutManager(mContext));
        rvClassroom.setHasFixedSize(true);
        mClassroomAdapter.bindToRecyclerView(rvClassroom);
    }

    @Override
    protected boolean enableSimplebar() {
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
    public void initViewObservable() {
        mViewModel.getBannerV2SingleLiveEvent().observe(this, bannerV2s -> {
            List<String> images = new ArrayList<>();
            for (BannerV2 bannerV2 : bannerV2s) {
                images.add(bannerV2.getBannerUrl());
            }
            banner.setImages(images).setImageLoader(new MainHomeFragment.GlideImageLoader()).start();
        });
        mViewModel.getDailySingleLiveEvent().observe(this, albums -> mDailyAdapter.setNewData(albums));
        mViewModel.getBookSingleLiveEvent().observe(this, albums -> mBookAdapter.setNewData(albums));
        mViewModel.getClassRoomSingleLiveEvent().observe(this, albums -> mClassroomAdapter.setNewData(albums));
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
        if(banner!=null)
        banner.startAutoPlay();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if(banner!=null)
        banner.stopAutoPlay();
    }
}
