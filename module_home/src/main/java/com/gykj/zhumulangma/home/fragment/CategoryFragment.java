package com.gykj.zhumulangma.home.fragment;


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
    protected boolean enableSwipeBack() {
        return false;
    }
    @Override
    public int onBindLayout() {
        return R.layout.home_fragment_category;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean enableLazy() {
        return true;
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }
}
