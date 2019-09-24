package com.gykj.zhumulangma.listen;

import com.gykj.zhumulangma.common.mvvm.view.BaseActivity;
import com.gykj.zhumulangma.listen.fragment.MainListenFragment;

public class MainActivity extends BaseActivity {
    @Override
    protected int onBindLayout() {
        return R.layout.common_activity_main;
    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);
        if (findFragment(MainListenFragment.class) == null) {
            loadRootFragment(R.id.fl_container,new MainListenFragment());  // 加载根Fragment
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
