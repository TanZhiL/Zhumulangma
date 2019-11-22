package com.gykj.zhumulangma.home.dialog;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.PlayRadioAdapter;
import com.lxj.xpopup.core.BottomPopupView;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/4 8:38
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class PlayRadioPopup extends BottomPopupView implements View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener{

    private SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:HH:mm", Locale.getDefault());
    private MagicIndicator magicIndicator;
    private String[] tabs = {"昨天", "今天", "明天"};
    private ViewPager viewpager;
    private RecyclerView rvYestoday, rvToday, rvTomorrow;
    private PlayRadioAdapter mYestodayAdapter, mTodayAdapter, mTomorrowAdapter;

    private Context mContext;

    public PlayRadioPopup(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.home_dialog_play_radio;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        magicIndicator = findViewById(R.id.magic_indicator);
        viewpager = findViewById(R.id.viewpager);

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

        rvYestoday.setHasFixedSize(true);
        rvToday.setHasFixedSize(true);
        rvTomorrow.setHasFixedSize(true);

        mYestodayAdapter = new PlayRadioAdapter(R.layout.home_item_play_radio);
        mTodayAdapter = new PlayRadioAdapter(R.layout.home_item_play_radio);
        mTomorrowAdapter = new PlayRadioAdapter(R.layout.home_item_play_radio);

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

        mYestodayAdapter.setOnItemClickListener(this);
        mTodayAdapter.setOnItemClickListener(this);
        mTomorrowAdapter.setOnItemClickListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_close) {
            dismiss();
        }
    }

    List<Schedule> list;
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>(Arrays.asList(new Schedule[mYestodayAdapter.getData().size()]));
            Collections.copy(list, mYestodayAdapter.getData());
            list.addAll(mTodayAdapter.getData());
            list.addAll(mTomorrowAdapter.getData());
        }
        if (adapter == mYestodayAdapter) {
            XmPlayerManager.getInstance(mContext).playSchedule(list, position);
        } else if (adapter == mTodayAdapter) {
            Schedule schedule = mTodayAdapter.getItem(position);
            try {
                long start = sdf.parse(schedule.getStartTime()).getTime();
                if (start > System.currentTimeMillis()) {
                    ToastUtil.showToast("节目尚未开播");
                    return;
                }
                position = mYestodayAdapter.getData().size() + position;
                XmPlayerManager.getInstance(mContext).playSchedule(list, position);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (adapter == mTomorrowAdapter) {
            ToastUtil.showToast("节目尚未开播");
        }
    }

    public PlayRadioAdapter getYestodayAdapter() {
        return mYestodayAdapter;
    }

    public PlayRadioAdapter getTodayAdapter() {
        return mTodayAdapter;
    }

    public PlayRadioAdapter getTomorrowAdapter() {
        return mTomorrowAdapter;
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
