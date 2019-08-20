package com.gykj.zhumulangma.common.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;


import com.gykj.zhumulangma.common.R;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.List;

/**
 * Created by 10719
 * on 2019/6/21
 */
public class TabNavigatorAdapter extends CommonNavigatorAdapter {
    private List<String> mtabs;
    private ViewPager viewPager;
    private float lineHeight;

    public TabNavigatorAdapter(List<String> mtabs, ViewPager viewPager, float lineHeight) {
        this.mtabs = mtabs;
        this.viewPager = viewPager;
        this.lineHeight = lineHeight;
    }

    @Override
    public int getCount() {
        return mtabs == null ? 0 : mtabs.size();
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
        simplePagerTitleView.setText(mtabs.get(index));
        simplePagerTitleView.setNormalColor(context.getResources().getColor(R.color.colorGray));
        simplePagerTitleView.setSelectedColor(context.getResources().getColor(R.color.colorPrimary));
        simplePagerTitleView.setTextSize(14);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(index);
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        indicator.setLineHeight(UIUtil.dip2px(context, 2));
        indicator.setLineWidth(UIUtil.dip2px(context, lineHeight));
        indicator.setRoundRadius(UIUtil.dip2px(context, 3));
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
        indicator.setColors(context.getResources().getColor(R.color.colorPrimary));
        return indicator;
    }


}
