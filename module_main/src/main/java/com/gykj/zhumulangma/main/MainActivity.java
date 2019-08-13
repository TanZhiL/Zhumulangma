package com.gykj.zhumulangma.main;

import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseActivity;
import com.gykj.zhumulangma.common.widget.GlobalPlay;
import com.gykj.zhumulangma.main.fragment.MainFragment;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path=AppConstants.Router.Main.A_MAIN)
public class MainActivity extends BaseActivity implements View.OnClickListener ,MainFragment.onRootShowListener{

    private GlobalPlay globalPlay;
    @Override
    protected int onBindLayout() {
        return R.layout.main_activity_main;
    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);
        if (findFragment(MainFragment.class) == null) {
            MainFragment mainFragment = new MainFragment();
            mainFragment.setShowListener(this);
            loadRootFragment(R.id.fl_container,mainFragment);
        }
        globalPlay=fd(R.id.gp);

    }

    @Override
    public void initListener() {
        globalPlay.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public <T> void onEvent(BaseActivityEvent<T> event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.MainCode.NAVIGATE:
                start((ISupportFragment) event.getData());
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }
    @Override
    public void onRootShow(boolean isVisible) {
        if (isVisible)
            globalPlay.setBackgroundColor(Color.TRANSPARENT);
        else
            globalPlay.setBackground(getResources().getDrawable(R.drawable.shap_common_widget_play));
    }


    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;
    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }


}
