package com.gykj.zhumulangma.home.activity;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.adapter.TPagerAdapter;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.databinding.CommonLayoutRefreshListBinding;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.extra.ViewPagerHelper;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshActivity;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.RankFreeAdapter;
import com.gykj.zhumulangma.home.adapter.RankPaidAdapter;
import com.gykj.zhumulangma.home.databinding.HomeActivityRankBinding;
import com.gykj.zhumulangma.home.databinding.HomeLayoutRankBarCenterBinding;
import com.gykj.zhumulangma.home.dialog.RankCategoryPopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RankViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.kingja.loadsir.callback.Callback;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 10:21
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:排行榜
 */
@Route(path = Constants.Router.Home.F_RANK)
public class RankActivity extends BaseRefreshActivity<HomeActivityRankBinding, RankViewModel, Album> implements
        RankCategoryPopup.onSelectedListener, RankCategoryPopup.onPopupDismissingListener {

    private RankFreeAdapter mFreeAdapter;
    private RankPaidAdapter mPaidAdapter;
    private String[] mTabs = {"免费榜"};

    private String mCId = "0";

    private CommonLayoutRefreshListBinding mFreeBind;
    private CommonLayoutRefreshListBinding mPaidBind;
    //下拉中间视图
    private HomeLayoutRankBarCenterBinding mBindBarCenter;
    private RankCategoryPopup mCategoryPopup;

    public RankActivity() {
    }

    @Override
    public int onBindLayout() {
        return R.layout.home_activity_rank;
    }

    @Override
    public void initView() {
        super.initView();
        mBindBarCenter.ivDown.setVisibility(View.VISIBLE);
        mBindBarCenter.tvTitle.setText("热门");
        mFreeBind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.common_layout_refresh_list, null, false);
        mPaidBind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.common_layout_refresh_list, null, false);

        mFreeBind.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mPaidBind.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mFreeAdapter = new RankFreeAdapter(R.layout.home_item_rank_free);
        mPaidAdapter = new RankPaidAdapter(R.layout.home_item_rank_paid);
        mFreeBind.recyclerview.setHasFixedSize(true);
        mPaidBind.recyclerview.setHasFixedSize(true);
        mFreeAdapter.bindToRecyclerView(mFreeBind.recyclerview);
        mPaidAdapter.bindToRecyclerView(mPaidBind.recyclerview);

        mBinding.viewpager.setAdapter(new TPagerAdapter(mFreeBind.getRoot()));
        final CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(mTabs), mBinding.viewpager, 125));
        mBinding.magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mBinding.magicIndicator, mBinding.viewpager);

        mCategoryPopup = new RankCategoryPopup(this, this);
        mCategoryPopup.setDismissingListener(this);

    }

    @Override
    public void initListener() {
        super.initListener();
        RxView.clicks(mBindBarCenter.getRoot())
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> switchCategory());
        mFreeAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mFreeAdapter.getItem(position).getId())));
        mPaidAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mPaidAdapter.getItem(position).getId())));

    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mFreeBind.refreshLayout, mFreeAdapter);
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
        if (mCategoryPopup.isShow()) {
            mCategoryPopup.dismiss();
        } else {
            mBindBarCenter.ivDown.animate().rotation(180).setDuration(200);
            new XPopup.Builder(this).atView(mSimpleTitleBar).popupPosition(PopupPosition.Bottom).asCustom(mCategoryPopup).show();
        }
    }

    @Override
    public Class<RankViewModel> onBindViewModel() {
        return RankViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(getApplication());
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitFreeEvent().observe(this, albums -> mFreeAdapter.setNewData(albums));

        mViewModel.getPaidSingleLiveEvent().observe(this, albums -> {
            if (null == albums || (mPaidAdapter.getData().size() == 0 && albums.size() == 0)) {
                showEmptyView();
                return;
            }
            if (albums.size() > 0) {
                mPaidAdapter.addData(albums);
                mPaidBind.refreshLayout.finishLoadMore();
            } else {
                mPaidBind.refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
    }

    @Override
    public void onSelected(String category_id, String category_name) {
        if (mCId.equals(category_id)) {
            return;
        }
        mBindBarCenter.tvTitle.setText(category_name);
        mCId = category_id;
        mViewModel.setCid(mCId);
        mFreeAdapter.setNewData(null);
        mViewModel.init();
    }

    @Override
    public void onDismissing() {
        mBindBarCenter.ivDown.animate().rotation(0).setDuration(200);
    }

    class RankPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 1;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = mFreeBind.getRoot();
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
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_share};
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        share(null);
    }

    @Override
    public SimpleBarStyle onBindBarCenterStyle() {
        return SimpleBarStyle.CENTER_CUSTOME;
    }

    @Override
    public SimpleBarStyle onBindBarRightStyle() {
        return SimpleBarStyle.RIGHT_ICON;
    }

    @Override
    public View onBindBarCenterCustome() {
        mBindBarCenter = DataBindingUtil.inflate(getLayoutInflater(), R.layout.home_layout_rank_bar_center, null, false);
        return mBindBarCenter.getRoot();
    }

    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
