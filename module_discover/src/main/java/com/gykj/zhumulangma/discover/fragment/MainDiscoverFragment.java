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
import com.maiml.library.BaseItemLayout;
import com.maiml.library.config.ConfigAttrs;
import com.maiml.library.config.Mode;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Discover.F_MAIN)
public class MainDiscoverFragment extends BaseFragment implements BaseItemLayout.OnBaseItemClick {
    private BaseItemLayout bilFine;

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

        bilFine = view.findViewById(R.id.bil_fine);
        List<String> valueList = new ArrayList<>();

        valueList.add("付费精品");
        valueList.add("全民朗读");
        valueList.add("听友圈");
        valueList.add("大咖主播");
        valueList.add("问答");
        valueList.add("商城");
        valueList.add("游戏");
        valueList.add("活动");

        List<Integer> resIdList = new ArrayList<>();

        resIdList.add(R.drawable.discover_fine);
        resIdList.add(R.drawable.discover_read);
        resIdList.add(R.drawable.discover_frends);
        resIdList.add(R.drawable.discover_nb);
        resIdList.add(R.drawable.discover_question);
        resIdList.add(R.drawable.discover_shop);
        resIdList.add(R.drawable.discover_game);
        resIdList.add(R.drawable.discover_activity);

        ConfigAttrs attrs = new ConfigAttrs(); // 把全部参数的配置，委托给ConfigAttrs类处理。

        //参数 使用链式方式配置
        attrs.setValueList(valueList)  // 文字 list
                .setResIdList(resIdList) // icon list
                .setIconWidth(23)  //设置icon 的大小
                .setIconHeight(23)
                .setItemMarginTop(1)
                .setRightText(0, "2")
                .setRightText(4, "1")
                .setItemMarginTop(0,0)
                .setItemMarginTop(1, 8)
                .setItemMarginTop(3, 8)
                .setItemMarginTop(5, 8)
                .setItemMode(Mode.ARROW)
                .setItemMode(0, Mode.RED_TEXT)
                .setItemMode(4, Mode.RED_TEXT)
                .setArrowResId(R.drawable.common_arrow_enter); //设置箭头资源值;

        bilFine.setConfigAttrs(attrs)
                .create();

    }

    @Override
    public void initListener() {
        super.initListener();
        bilFine.setOnBaseItemClick(this);
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
    public void onItemClick(int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Discover.F_WEB)
                .withString(KeyCode.Discover.PATH, "https://h5.m.taobao.com/?sprefer=sypc00")
                .navigation();
        EventBus.getDefault().post(new BaseActivityEvent<>(
                EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Discover.F_WEB, (ISupportFragment) navigation)));
    }
}
