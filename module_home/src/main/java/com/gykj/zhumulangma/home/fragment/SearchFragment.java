package com.gykj.zhumulangma.home.fragment;


import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.home.R;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;

@Route(path = AppConstants.Router.Home.F_SEARCH)
public class SearchFragment extends BaseFragment implements View.OnClickListener {


    public SearchFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_search;
    }

    @Override
    protected void initView(View view) {
        setSwipeBackEnable(false);
        if( StatusBarUtils.supportTransparentStatusBar()){
            fd(R.id.cl_titlebar).setPadding(0, BarUtils.getStatusBarHeight(),0,0);
        }

    }

    @Override
    public void initListener() {
        super.initListener();
        fd(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.iv_back){
            pop();
        }
    }
}
