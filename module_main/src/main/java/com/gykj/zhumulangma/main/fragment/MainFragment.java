package com.gykj.zhumulangma.main.fragment;


import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.main.R;
import com.gykj.zhumulangma.main.databinding.MainFragmentMainBinding;
import com.next.easynavigation.view.EasyNavigationBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

@Route(path = Constants.Router.Main.F_MAIN)
public class MainFragment extends BaseFragment<MainFragmentMainBinding> implements EasyNavigationBar.OnTabClickListener {

    private String[] tabText = {"首页", "我听", "", "发现", "我的"};

    private @DrawableRes
    int[] normalIcon = {R.drawable.main_tab_home_normal, R.drawable.main_tab_litsten_normal
            , R.drawable.main_tab_play, R.drawable.main_tab_find_normal, R.drawable.main_tab_user_normal};
    private @DrawableRes
    int[] selectIcon = {R.drawable.main_tab_home_press, R.drawable.main_tab_listen_press
            , R.drawable.main_tab_play, R.drawable.main_tab_find_press, R.drawable.main_tab_user_press};

    private onRootShowListener mShowListener;

    public MainFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.main_fragment_main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    public void initView(View view) {
        List<Fragment> fragments = new ArrayList<>();

        Object home = ARouter.getInstance().build(Constants.Router.Home.F_MAIN).navigation();
        if (null != home) {
            fragments.add((Fragment) home);
        }
        Object listen = ARouter.getInstance().build(Constants.Router.Listen.F_MAIN).navigation();
        if (null != listen) {
            fragments.add((Fragment) listen);
        }
        Object discover = ARouter.getInstance().build(Constants.Router.Discover.F_MAIN).navigation();
        if (null != listen) {
            fragments.add((Fragment) discover);
        }
        Object user = ARouter.getInstance().build(Constants.Router.User.F_MAIN).navigation();
        if (null != listen) {
            fragments.add((Fragment) user);
        }
        mBinding.enb.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .lineHeight(1)
                .mode(EasyNavigationBar.MODE_ADD)
                .fragmentManager(getChildFragmentManager())
                .normalTextColor(getResources().getColor(R.color.colorGray))   //Tab未选中时字体颜色
                .selectTextColor(getResources().getColor(R.color.colorPrimary))   //Tab选中时字体颜色
                .tabTextSize(11)   //Tab文字大小
                .iconSize(27)
                .addIconSize(0)//取消中间图标
                .navigationHeight(50)
                .onTabClickListener(this)
                .build();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (mShowListener != null)
            mShowListener.onRootShow(true);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (mShowListener != null)
            mShowListener.onRootShow(false);
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    public void setShowListener(onRootShowListener showListener) {
        mShowListener = showListener;
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public boolean onTabClickEvent(View view, int i) {
        switch (i){
            case 0:
                EventBus.getDefault().post(new FragmentEvent(EventCode.Home.TAB_REFRESH));
                break;
            case 1:
                EventBus.getDefault().post(new FragmentEvent(EventCode.Listen.TAB_REFRESH));
                break;
            case 2:
                //中间按钮
                break;
            case 3:
                EventBus.getDefault().post(new FragmentEvent(EventCode.Discover.TAB_REFRESH));
                break;
            case 4:
                EventBus.getDefault().post(new FragmentEvent(EventCode.User.TAB_REFRESH));
                break;
        }
        return false;
    }

    public interface onRootShowListener {
        void onRootShow(boolean isVisible);
    }

}
