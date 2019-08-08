package com.gykj.zhumulangma.home;

import com.gykj.zhumulangma.common.mvvm.BaseActivity;
import com.gykj.zhumulangma.home.fragment.MainHomeFragment;

public class MainActivity extends BaseActivity {
    @Override
    protected int onBindLayout() {
        return R.layout.common_activity_main;
    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);
        if (findFragment(MainHomeFragment.class) == null) {
            loadRootFragment(R.id.fl_container,new MainHomeFragment());  // 加载根Fragment
        }
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }
}
