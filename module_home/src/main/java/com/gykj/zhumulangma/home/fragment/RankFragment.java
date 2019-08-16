package com.gykj.zhumulangma.home.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.NavigatorAdapter;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RankCategotyAdapter;
import com.gykj.zhumulangma.home.adapter.RankFreeAdapter;
import com.gykj.zhumulangma.home.adapter.RankPaidAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RankViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.ranks.RankList;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Home.F_RANK)
public class RankFragment extends BaseMvvmFragment<RankViewModel> implements View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener {


    public RankFragment() {
    }


    private MagicIndicator magicIndicator;
    private ViewPager viewpager;
    private RecyclerView rvCategory;
    private FrameLayout flMask;

    private ViewGroup layoutFree;
    private ViewGroup layoutPaid;
    private RefreshLayout rlFree;
    private RefreshLayout rlPaid;
    private RecyclerView rvFree;
    private RecyclerView rvPaid;
    private RankFreeAdapter mFreeAdapter;
    private RankPaidAdapter mPaidAdapter;

    //下拉中间视图
    private View llbarCenter;
    private View ivCategoryDown;
    private TextView tvTitle;

    private String[] tabs = {"免费榜", "付费榜"};
    private String[] c_labels = {"热门", "音乐", "娱乐", "有声书"
            , "儿童", "3D体验馆", "资讯", "脱口秀"
            , "情感生活", "历史", "人文", "英语"
            , "小语种", "教育培训", "广播剧", "国学书院"
            , "电台", "商业财经", "IT科技", "健康养生"
            , "旅游", "汽车", "动漫游戏", "电影"};
    private String[] c_ids = {"0", "2", "4", "3"
            , "6", "29", "1", "28"
            , "10", "9", "39", "38"
            , "32", "13", "15", "40"
            , "17", "8", "18", "7"
            , "22", "21", "24", "23"};


    private String cid = c_ids[0];


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_rank;
    }

    @Override
    public void initView(View view) {
        magicIndicator = fd(R.id.magic_indicator);
        viewpager = fd(R.id.viewpager);
        rvCategory = fd(R.id.rv_category);
        flMask = fd(R.id.fl_mask);

        ivCategoryDown = llbarCenter.findViewById(R.id.iv_down);
        tvTitle = llbarCenter.findViewById(R.id.tv_title);
        tvTitle.setText(c_labels[0]);
        layoutFree = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.common_layout_refresh_loadmore, null);
        layoutPaid = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.common_layout_refresh_loadmore, null);
        rlFree = layoutFree.findViewById(R.id.refreshLayout);
        rlFree.setEnableRefresh(false);
        rlPaid = layoutPaid.findViewById(R.id.refreshLayout);
        rlPaid.setEnableRefresh(false);
        rvFree = layoutFree.findViewById(R.id.rv);
        rvPaid = layoutPaid.findViewById(R.id.rv);
        rvFree.setLayoutManager(new LinearLayoutManager(mContext));
        rvPaid.setLayoutManager(new LinearLayoutManager(mContext));
        mFreeAdapter = new RankFreeAdapter(R.layout.home_item_rank_free);
        mPaidAdapter = new RankPaidAdapter(R.layout.home_item_rank_paid);
        rvFree.setHasFixedSize(true);
        rvPaid.setHasFixedSize(true);
        mFreeAdapter.bindToRecyclerView(rvFree);
        mPaidAdapter.bindToRecyclerView(rvPaid);

        viewpager.setAdapter(new RankPagerAdapter());
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new NavigatorAdapter(Arrays.asList(tabs), viewpager, 125));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);

        rvCategory.setLayoutManager(new GridLayoutManager(mContext, 4));
        RankCategotyAdapter categotyAdapter = new RankCategotyAdapter(R.layout.home_item_rank_category,
                Arrays.asList(c_labels));
        rvCategory.setHasFixedSize(true);
        categotyAdapter.bindToRecyclerView(rvCategory);
        categotyAdapter.setOnItemClickListener(this);


    }

    @Override
    public void initListener() {
        super.initListener();

        llbarCenter.setOnClickListener(this);
        flMask.setOnClickListener(this);

        mFreeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID,mFreeAdapter.getData().get(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL,
                    (ISupportFragment) navigation)));
        });
        mPaidAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID,mPaidAdapter.getData().get(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL,
                    (ISupportFragment) navigation)));
        });
        rlFree.setOnLoadMoreListener(refreshLayout -> mViewModel.getFreeRank(cid));
        rlPaid.setOnLoadMoreListener(refreshLayout -> mViewModel.getPaidRank());
    }

    @Override
    public void initData() {

            mViewModel.getFreeRank(cid);
            mViewModel.getPaidRank();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (view == llbarCenter || id == R.id.fl_mask) {
            switchCategory();
        }
    }

    private void switchCategory() {
        if (flMask.getVisibility() == View.VISIBLE) {

            flMask.animate().withStartAction(() -> flMask.setAlpha(1))
                    .withEndAction(() -> flMask.setVisibility(View.GONE)).alpha(0).setDuration(200);
            ivCategoryDown.animate().rotation(0).setDuration(200);
        } else {
            flMask.setAlpha(0);
            flMask.animate().withStartAction(() -> flMask.setVisibility(View.VISIBLE)).alpha(1).setDuration(200);
            ivCategoryDown.animate().rotation(180).setDuration(200);
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switchCategory();
        tvTitle.setText(c_labels[position]);
        cid = c_ids[position];
        mFreeAdapter.setNewData(null);
        mViewModel.getFreeRank(cid);
    }

    @Override
    public Class<RankViewModel> onBindViewModel() {
        return RankViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getFreeSingleLiveEvent().observe(this, albums -> {
            if (null == albums || (mFreeAdapter.getData().size() == 0 && albums.size() == 0)) {
                showNoDataView(true);
                return;
            }
            if (albums.size() > 0) {
                mFreeAdapter.addData(albums);
                rlFree.finishLoadMore();
            } else {
                rlFree.finishLoadMoreWithNoMoreData();
            }
        });
        mViewModel.getPaidSingleLiveEvent().observe(this, albums -> {
            if (null == albums || (mPaidAdapter.getData().size() == 0 && albums.size() == 0)) {
                showNoDataView(true);
                return;
            }
            if (albums.size() > 0) {
                mPaidAdapter.addData(albums);
                rlPaid.finishLoadMore();
            } else {
                rlPaid.finishLoadMoreWithNoMoreData();
            }
        });
    }

    class RankPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 2;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = position == 0 ? layoutFree : layoutPaid;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    protected Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_share};
    }

    @Override
    protected int onBindBarCenterStyle() {
        return BarStyle.CENTER_CUSTOME;
    }

    @Override
    protected int onBindBarRightStyle() {
        return BarStyle.RIGHT_ICON;
    }

    @Override
    protected View onBindBarCenterCustome() {
        llbarCenter = LayoutInflater.from(mContext).inflate(R.layout.home_layout_rank_bar_center, null);
        return llbarCenter;
    }
}
