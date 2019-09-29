package com.gykj.zhumulangma.home.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.home.R;

/**
 * Author: Thomas.
 * Date: 2019/8/14 13:41
 * Email: 1071931588@qq.com
 * Description:分类
 */
public class CategoryFragment extends BaseFragment {


    public CategoryFragment() {

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackground(null);
        setSwipeBackEnable(false);
    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_category;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }
}
