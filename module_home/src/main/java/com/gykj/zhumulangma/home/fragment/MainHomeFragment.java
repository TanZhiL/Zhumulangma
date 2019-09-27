package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.bumptech.glide.Glide;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TFragmentPagerAdapter;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HomeViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.sunfusheng.marqueeview.MarqueeView;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.youth.banner.loader.ImageLoader;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Home.F_MAIN)
public class MainHomeFragment extends BaseMvvmFragment<HomeViewModel> implements View.OnClickListener, MarqueeView.OnItemClickListener {


    private String[] mTabs = {"热门", "分类", "精品","主播", "广播"};
    private List<Fragment> mFragments = new ArrayList<>();
    private MarqueeView<String> mMarqueeView;

    public MainHomeFragment() {
    }


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackgroundColor(Color.WHITE);
        setSwipeBackEnable(false);
    }
    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }
    @Override
    protected void initView(View view) {
        if (StatusBarUtils.supportTransparentStatusBar()) {
            fd(R.id.cl_titlebar).setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        }

        ViewPager viewpager = view.findViewById(R.id.viewpager);
        mFragments.add(new HotFragment());
        mFragments.add(new CategoryFragment());
        mFragments.add(new FineFragment());
        mFragments.add(new AnnouncerFragment());
        mFragments.add(new RadioFragment());

        TFragmentPagerAdapter adapter = new TFragmentPagerAdapter(
                getChildFragmentManager(), mFragments);
        viewpager.setOffscreenPageLimit(4);
        viewpager.setAdapter(adapter);

        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(mTabs), viewpager, 50));
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);

        mMarqueeView=fd(R.id.marqueeView);
    }

    @Override
    public void initListener() {
        super.initListener();
        addDisposable(RxView.clicks(fd(R.id.ll_search)).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    Postcard build = ARouter.getInstance().build(AppConstants.Router.Home.F_SEARCH);
                    if(!CollectionUtils.isEmpty(mMarqueeView.getMessages())){
                        build.withString(KeyCode.Home.HOTWORD,mMarqueeView.getMessages().get(mMarqueeView.getPosition()));
                    }
                    Object navigation = build.navigation();
                    EventBus.getDefault().post(new BaseActivityEvent<>(
                            EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_SEARCH, (ISupportFragment) navigation)));
                }));

        fd(R.id.iv_download).setOnClickListener(this);
        fd(R.id.iv_history).setOnClickListener(this);
        addDisposable(RxView.clicks(fd(R.id.iv_message)).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    navigateTo(AppConstants.Router.User.F_MESSAGE);
                }));
        mMarqueeView.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getHotWords();
    }

    @Override
    protected void onRevisible() {
        super.onRevisible();
        if(CollectionUtils.isEmpty(mMarqueeView.getMessages())){
            mViewModel.getHotWords();
        }
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_download) {
            navigateTo(AppConstants.Router.Listen.F_DOWNLOAD);
        } else if (id == R.id.iv_history) {
            navigateTo(AppConstants.Router.Listen.F_HISTORY);
        }

    }

    @Override
    public Class<HomeViewModel> onBindViewModel() {
        return HomeViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getHotWordsEvent().observe(this, hotWords -> {
            List<String> words=new ArrayList<>(hotWords.size());
            for(HotWord word:hotWords){
                words.add(word.getSearchword());
            }
            mMarqueeView.startWithList(words);
        });
    }

    @Override
    public void onItemClick(int position, TextView textView) {
        Postcard build = ARouter.getInstance().build(AppConstants.Router.Home.F_SEARCH);
        Object navigation = build.withString(KeyCode.Home.HOTWORD,mMarqueeView.getMessages().get(position)).navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_SEARCH, (ISupportFragment) navigation)));
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

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if(mMarqueeView!=null&&!CollectionUtils.isEmpty(mMarqueeView.getMessages())){
            mMarqueeView.startFlipping();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if(mMarqueeView!=null) {
            mMarqueeView.stopFlipping();
        }
    }
    @Override
    protected boolean lazyEnable() {
        return false;
    }
}
