package com.gykj.zhumulangma.discover.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.discover.R;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Discover.F_MAIN)
public class MainDiscoverFragment extends BaseFragment implements View.OnClickListener {

    @Override
    protected int onBindLayout() {
        return R.layout.discover_fragment_main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }
    @Override
    protected void initView(View view) {


    }

    @Override
    public void initListener() {
        super.initListener();
      fd(R.id.cl_ffjp).setOnClickListener(this);
      fd(R.id.cl_qmld).setOnClickListener(this);
      fd(R.id.cl_tyq).setOnClickListener(this);
      fd(R.id.cl_dkzb).setOnClickListener(this);
      fd(R.id.cl_wd).setOnClickListener(this);
      fd(R.id.cl_sc).setOnClickListener(this);
      fd(R.id.cl_yx).setOnClickListener(this);
      fd(R.id.cl_hd).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    protected void onLeftIconClick(View v) {
        super.onLeftIconClick(v);
        navigateTo(AppConstants.Router.User.F_MESSAGE);
    }
    @Override
    protected void onRight1Click(View v) {
        super.onRight1Click(v);
        navigateTo(AppConstants.Router.Home.F_SEARCH);
    }
    @Override
    protected int onBindBarLeftStyle() {
        return BarStyle.LEFT_ICON;
    }

    @Override
    protected int onBindBarRightStyle() {
        return BarStyle.RIGHT_ICON;
    }

    @Override
    protected Integer onBindBarLeftIcon() {
        return R.drawable.ic_common_message;
    }

    @Override
    protected Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_search};
    }

    @Override
    protected String[] onBindBarTitleText() {
        return  new String[]{"发现"};
    }

    @Override
    public void onClick(View v) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Discover.F_WEB)
                .withString(KeyCode.Discover.PATH, v.getTag().toString())
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Discover.F_WEB, (ISupportFragment) navigation)));
    }
}
