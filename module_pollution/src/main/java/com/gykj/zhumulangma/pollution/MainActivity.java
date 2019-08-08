package com.gykj.zhumulangma.pollution;

import com.gykj.zhumulangma.common.mvvm.BaseActivity;
import com.gykj.zhumulangma.polltion.R;
import com.gykj.zhumulangma.pollution.fragment.MainPollutionFragment;

public class MainActivity extends BaseActivity {
    @Override
    protected int onBindLayout() {
        return R.layout.common_activity_main;
    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);
        if (findFragment(MainPollutionFragment.class) == null) {
            loadRootFragment(R.id.fl_container,new MainPollutionFragment());  // 加载根Fragment
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
