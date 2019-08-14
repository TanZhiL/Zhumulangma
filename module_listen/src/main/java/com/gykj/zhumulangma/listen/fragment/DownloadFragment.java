package com.gykj.zhumulangma.listen.fragment;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.util.DisplayUtil;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.NavigatorAdapter;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.util.SystemUtil;
import com.gykj.zhumulangma.listen.R;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDownloadManager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;

@Route(path = AppConstants.Router.Listen.F_DOWNLOAD)
public class DownloadFragment extends BaseFragment{

    TextView tvMemory;
    ViewPager viewpager;

    MagicIndicator magicIndicator;
    String[] tabs = {"专辑", "声音", "下载中"};
    ViewGroup layoutDetail1,layoutDetail2,layoutDetail3;


    public DownloadFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.listen_fragment_download;
    }

    @Override
    protected void initView(View view) {


        layoutDetail1 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.home_layout_album_detail, null);
        layoutDetail2 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.home_layout_album_detail, null);
        layoutDetail3 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.home_layout_album_detail, null);
        tvMemory=fd(R.id.tv_memory);
        viewpager=fd(R.id.viewpager);
        viewpager.setAdapter(new DownloadPagerAdapter());
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new NavigatorAdapter(Arrays.asList(tabs), viewpager, 50));
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
    }

    @Override
    public void initData() {
        tvMemory.setText(getString(R.string.memory,
                XmDownloadManager.getInstance().getHumanReadableDownloadOccupation(IDownloadManager.Auto),
                SystemUtil.getRomTotalSize(mContext)));
    }




    class DownloadPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view=null;
            switch (position){
                case 0:
                    view =  layoutDetail1;
                    break;
                case 1:
                    view =  layoutDetail2;
                    break;
                case 2:
                    view =  layoutDetail3;
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

    @Override
    protected int onBindBarCenterStyle() {
        return BarStyle.CENTER_CUSTOME;
    }

    @Override
    protected View onBindBarCenterCustome() {
        magicIndicator=new MagicIndicator(mContext);
        magicIndicator.setBackgroundColor(Color.WHITE);
        FrameLayout frameLayout=new FrameLayout(mContext);
        frameLayout.addView(magicIndicator);
        ViewGroup.LayoutParams layoutParams = magicIndicator.getLayoutParams();
        layoutParams.width= DisplayUtil.dip2px(mContext,270);
        magicIndicator.setLayoutParams(layoutParams);

        return frameLayout;
    }
}
