package com.gykj.zhumulangma.home.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.databinding.HomeFragmentCategoryBinding;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:分类
 */
public class CategoryFragment extends BaseFragment<HomeFragmentCategoryBinding> {


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
    protected void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }
}
