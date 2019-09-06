package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RadioUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.PlayRadioAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayRadioViewModel;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.opensdk.util.ModelUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: Thomas.
 * Date: 2019/9/6 10:25
 * Email: 1071931588@qq.com
 * Description:
 */
@Route(path = AppConstants.Router.Home.F_PLAY_RADIIO_LIST)
public class PlayRadioListFragment extends BaseMvvmFragment<PlayRadioViewModel> {

    @Autowired(name = KeyCode.Home.RADIO_ID)
    public String mRadioId;
    private MagicIndicator magicIndicator;
    private String[] tabs = {"昨天", "今天", "明天"};
    private ViewPager viewpager;
    private RecyclerView rvYestoday, rvToday, rvTomorrow;
    private PlayRadioAdapter mYestodayAdapter, mTodayAdapter, mTomorrowAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_play_radio_list;
    }

    @Override
    protected void initView(View view) {
        magicIndicator = fd(R.id.magic_indicator);
        viewpager = fd(R.id.viewpager);

        rvYestoday = new RecyclerView(mContext);
        rvToday = new RecyclerView(mContext);
        rvTomorrow = new RecyclerView(mContext);

        rvYestoday.setLayoutManager(new LinearLayoutManager(mContext));
        rvToday.setLayoutManager(new LinearLayoutManager(mContext));
        rvTomorrow.setLayoutManager(new LinearLayoutManager(mContext));

        rvYestoday.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvToday.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvTomorrow.setOverScrollMode(View.OVER_SCROLL_NEVER);

        rvYestoday.setBackgroundColor(Color.WHITE);
        rvToday.setBackgroundColor(Color.WHITE);
        rvTomorrow.setBackgroundColor(Color.WHITE);

        rvYestoday.setPadding(0,8,0,0);
        rvToday.setPadding(0,8,0,0);
        rvTomorrow.setPadding(0,8,0,0);

        rvYestoday.setHasFixedSize(true);
        rvToday.setHasFixedSize(true);
        rvTomorrow.setHasFixedSize(true);

        mYestodayAdapter=new PlayRadioAdapter(R.layout.home_item_program_list);
        mTodayAdapter=new PlayRadioAdapter(R.layout.home_item_program_list);
        mTomorrowAdapter=new PlayRadioAdapter(R.layout.home_item_program_list);

        mYestodayAdapter.bindToRecyclerView(rvYestoday);
        mTodayAdapter.bindToRecyclerView(rvToday);
        mTomorrowAdapter.bindToRecyclerView(rvTomorrow);

        viewpager.setAdapter(new ListPagerAdapter());
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), viewpager, 80));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
        viewpager.setCurrentItem(1);
    }

    @Override
    public void initData() {
        mViewModel._getSchedules(mRadioId);
    }
    @Override
    public void initViewObservable() {
        mViewModel.getYestodaySingleLiveEvent().observe(this, schedules -> mYestodayAdapter.setNewData(schedules));
        mViewModel.getTodaySingleLiveEvent().observe(this, schedules -> mTodayAdapter.setNewData(schedules));
        mViewModel.getTomorrowSingleLiveEvent().observe(this, schedules -> mTomorrowAdapter.setNewData(schedules));
    }
    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"电台界面列表"};
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public Class<PlayRadioViewModel> onBindViewModel() {
        return PlayRadioViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }



    class ListPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = null;
            switch (position) {
                case 0:
                    view = rvYestoday;
                    break;
                case 1:
                    view = rvToday;
                    break;
                case 2:
                    view = rvTomorrow;
                    break;
            }
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
}
