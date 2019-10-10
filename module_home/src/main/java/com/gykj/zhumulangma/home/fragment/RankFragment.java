package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.ListCallback;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RankFreeAdapter;
import com.gykj.zhumulangma.home.adapter.RankPaidAdapter;
import com.gykj.zhumulangma.home.dialog.RankCategoryPopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RankViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.kingja.loadsir.callback.Callback;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/8/14 10:21
 * Email: 1071931588@qq.com
 * Description:排行榜
 */
@Route(path = AppConstants.Router.Home.F_RANK)
public class RankFragment extends BaseRefreshMvvmFragment<RankViewModel, Album> implements
        RankCategoryPopup.onSelectedListener, RankCategoryPopup.onPopupDismissingListener {

    private RankFreeAdapter mFreeAdapter;
    private RankPaidAdapter mPaidAdapter;
    private String[] mTabs = {"免费榜"};

    private String mCId = "0";

    private ViewGroup layoutFree;
    private ViewGroup layoutPaid;
    private SmartRefreshLayout rlFree;
    private RefreshLayout rlPaid;
    //下拉中间视图
    private View llbarCenter;
    private View ivCategoryDown;
    private TextView tvTitle;
    private RankCategoryPopup mCategoryPopup;
    public RankFragment() {
    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_rank;
    }

    @Override
    public void initView(View view) {
        MagicIndicator magicIndicator = fd(R.id.magic_indicator);
        ViewPager viewpager = fd(R.id.viewpager);

        ivCategoryDown = llbarCenter.findViewById(R.id.iv_down);
        ivCategoryDown.setVisibility(View.VISIBLE);
        tvTitle = llbarCenter.findViewById(R.id.tv_title);
        tvTitle.setText("热门");
        layoutFree = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.common_layout_refresh_loadmore, null);
        layoutPaid = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.common_layout_refresh_loadmore, null);
        rlFree = layoutFree.findViewById(R.id.refreshLayout);
        rlPaid = layoutPaid.findViewById(R.id.refreshLayout);
        RecyclerView rvFree = layoutFree.findViewById(R.id.recyclerview);
        RecyclerView rvPaid = layoutPaid.findViewById(R.id.recyclerview);
        rvFree.setLayoutManager(new LinearLayoutManager(mActivity));
        rvPaid.setLayoutManager(new LinearLayoutManager(mActivity));
        mFreeAdapter = new RankFreeAdapter(R.layout.home_item_rank_free);
        mPaidAdapter = new RankPaidAdapter(R.layout.home_item_rank_paid);
        rvFree.setHasFixedSize(true);
        rvPaid.setHasFixedSize(true);
        mFreeAdapter.bindToRecyclerView(rvFree);
        mPaidAdapter.bindToRecyclerView(rvPaid);

        viewpager.setAdapter(new RankPagerAdapter());
        final CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(mTabs), viewpager, 125));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);

        mCategoryPopup=new RankCategoryPopup(mActivity,this);
        mCategoryPopup.setDismissingListener(this);

    }

    @Override
    public void initListener() {
        super.initListener();
        addDisposable(RxView.clicks(llbarCenter)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> switchCategory()));
        mFreeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mFreeAdapter.getItem(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL,
                    (ISupportFragment) navigation)));
        });
        mPaidAdapter.setOnItemClickListener((adapter, view, position) -> {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mPaidAdapter.getItem(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL,
                    (ISupportFragment) navigation)));
        });

    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(rlFree,mFreeAdapter);
    }

    @Override
    public void initData() {
      mViewModel.setCid(mCId);
        mViewModel.init();
    }

    /**
     * 显示分类弹窗
     */
    private void switchCategory() {
        if(mCategoryPopup.isShow()){
            mCategoryPopup.dismiss();
        }else {
            ivCategoryDown.animate().rotation(180).setDuration(200);
            new XPopup.Builder(mActivity).atView(fd(R.id.ctb_simple)).popupPosition(PopupPosition.Bottom).asCustom(mCategoryPopup).show();
        }
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
        mViewModel.getInitFreeEvent().observe(this, albums ->mFreeAdapter.setNewData(albums));

        mViewModel.getPaidSingleLiveEvent().observe(this, albums -> {
            if (null == albums || (mPaidAdapter.getData().size() == 0 && albums.size() == 0)) {
                showEmptyView();
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

    @Override
    public void onSelected(String category_id, String category_name) {
        if (mCId.equals(category_id)) {
            return;
        }
        tvTitle.setText(category_name);
        mCId = category_id;
        mViewModel.setCid(mCId);
        mFreeAdapter.setNewData(null);
        mViewModel.init();
    }

    @Override
    public void onDismissing() {
        ivCategoryDown.animate().rotation(0).setDuration(200);
    }

    class RankPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 1;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = layoutFree;
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
    protected void onRight1Click(View v) {
        super.onRight1Click(v);
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.SHARE));
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
        llbarCenter = LayoutInflater.from(mActivity).inflate(R.layout.home_layout_rank_bar_center, null);
        return llbarCenter;
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

     @Override
    protected Callback getInitCallBack() {
        return new ListCallback();
    }
}
