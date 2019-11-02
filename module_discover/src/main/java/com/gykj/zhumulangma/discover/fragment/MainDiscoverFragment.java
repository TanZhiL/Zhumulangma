package com.gykj.zhumulangma.discover.fragment;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.discover.R;
import com.gykj.zhumulangma.discover.databinding.DiscoverFragmentMainBinding;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = Constants.Router.Discover.F_MAIN)
public class MainDiscoverFragment extends BaseFragment<DiscoverFragmentMainBinding> implements View.OnClickListener {

    @Override
    protected int onBindLayout() {
        return R.layout.discover_fragment_main;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void initView() {
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.clFfjp.setOnClickListener(this);
        mBinding.clQmld.setOnClickListener(this);
        mBinding.clTyq.setOnClickListener(this);
        mBinding.clDkzb.setOnClickListener(this);
        mBinding.clWd.setOnClickListener(this);
        mBinding.clSc.setOnClickListener(this);
        mBinding.clYx.setOnClickListener(this);
        mBinding.clHd.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public void onLeftIconClick(View v) {
        super.onLeftIconClick(v);
        RouteUtil.navigateTo(Constants.Router.User.F_MESSAGE);
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        RouteUtil.navigateTo(Constants.Router.Home.F_SEARCH);
    }

    @Override
    public SimpleBarStyle onBindBarLeftStyle() {
        return SimpleBarStyle.LEFT_ICON;
    }

    @Override
    public SimpleBarStyle onBindBarRightStyle() {
        return SimpleBarStyle.RIGHT_ICON;
    }

    @Override
    public Integer onBindBarLeftIcon() {
        return R.drawable.ic_common_message;
    }

    @Override
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_search};
    }

    @Override
    public String[] onBindBarTitleText() {
        return new String[]{"发现"};
    }

    @Override
    public void onClick(View v) {
        Object navigation = ARouter.getInstance().build(Constants.Router.Discover.F_WEB)
                .withString(KeyCode.Discover.PATH, v.getTag().toString())
                .navigation();
        EventBus.getDefault().post(new ActivityEvent(
                EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Discover.F_WEB, (ISupportFragment) navigation)));
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Discover.TAB_REFRESH:

                break;
        }
    }
}
