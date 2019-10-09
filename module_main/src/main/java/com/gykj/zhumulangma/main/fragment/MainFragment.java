package com.gykj.zhumulangma.main.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.main.R;
import com.next.easynavigation.view.EasyNavigationBar;

import java.util.ArrayList;
import java.util.List;

@Route(path = AppConstants.Router.Main.F_MAIN)
public class MainFragment extends BaseFragment {

    private EasyNavigationBar enb;
    private String[] tabText = {"首页", "我听", "", "发现", "我的"};
    private List<String> tabTexts;
    //未选中icon
    private int[] normalIcon = {R.drawable.ic_main_tab_home_normal, R.drawable.ic_main_tab_litsten_normal
            , R.drawable.ic_main_tab_play, R.drawable.ic_main_tab_find_normal, R.drawable.ic_main_tab_user_normal};
    //选中时icon
    private int[] selectIcon = {R.drawable.ic_main_tab_home_press, R.drawable.ic_main_tab_listen_press
            , R.drawable.ic_main_tab_play, R.drawable.ic_main_tab_find_press, R.drawable.ic_main_tab_user_press};

    private List<Fragment> fragments = new ArrayList<>();

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
        tabTexts=new ArrayList<>();
        enb = view.findViewById(R.id.enb);

        Object home = ARouter.getInstance().build(AppConstants.Router.Home.F_MAIN).navigation();
        if (null != home) {
            tabTexts.add("首页");
            fragments.add((Fragment) home);
        }
        Object listen = ARouter.getInstance().build(AppConstants.Router.Listen.F_MAIN).navigation();
        if (null != listen) {
            tabTexts.add("我听");
            fragments.add((Fragment) listen);
        }
        Object discover = ARouter.getInstance().build(AppConstants.Router.Discover.F_MAIN).navigation();
        if (null != listen) {
            tabTexts.add("发现");
            fragments.add((Fragment) discover);
        }
        Object user = ARouter.getInstance().build(AppConstants.Router.User.F_MAIN).navigation();
        if (null != listen) {
            tabTexts.add("我的");
            fragments.add((Fragment) user);
        }
        enb.titleItems(tabText)
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
                .build();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if(mShowListener!=null)
            mShowListener.onRootShow(true);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if(mShowListener!=null)
            mShowListener.onRootShow(false);
    }
    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    public void setShowListener(onRootShowListener showListener) {
        mShowListener = showListener;
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    public interface onRootShowListener {
        void onRootShow(boolean isVisible);
    }

}
