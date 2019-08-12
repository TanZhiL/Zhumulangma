package com.gykj.zhumulangma.home.fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.NavigatorAdapter;
import com.gykj.zhumulangma.common.adapter.TFragmentPagerAdapter;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.home.R;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;
import com.youth.banner.loader.ImageLoader;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Route(path = AppConstants.Router.Home.F_MAIN)
public class MainHomeFragment extends BaseFragment {


    private MagicIndicator magicIndicator;
    private String[] tabs = {"热门", "分类", "精品", "广播"};
    private List<Fragment> pages = new ArrayList<>();
    private ViewPager viewpager;


    public MainHomeFragment() {
    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_main;
    }

    @Override
    protected void initView(View view) {
        setSwipeBackEnable(false);
        if( StatusBarUtils.supportTransparentStatusBar()){
            fd(R.id.cl_titlebar).setPadding(0, BarUtils.getStatusBarHeight(),0,0);
        }

        viewpager=view.findViewById(R.id.viewpager);
        pages.add(new HotFragment());
        pages.add(new CategoryFragment());
        pages.add(new FineFragment());
        pages.add(new RadioFragment());

        TFragmentPagerAdapter adapter = new TFragmentPagerAdapter(
                getChildFragmentManager(),pages);
        viewpager.setOffscreenPageLimit(4);
        viewpager.setAdapter(adapter);

        magicIndicator=view.findViewById(R.id.magic_indicator);
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new NavigatorAdapter(Arrays.asList(tabs),viewpager,50));
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    public static class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */
            //Glide 加载图片简单用法
            Glide.with(context).load(path).into(imageView);
        }

    }
}
