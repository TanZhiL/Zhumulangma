package com.gykj.zhumulangma.listen.fragment;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.listen.R;

/**
 * Author: Thomas.
 * Date: 2019/8/16 8:45
 * Email: 1071931588@qq.com
 * Description:
 */
@Route(path = AppConstants.Router.Listen.F_FAVORITE)
public class FavoriteFragment extends BaseFragment {


    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"我喜欢的"};
    }

}
