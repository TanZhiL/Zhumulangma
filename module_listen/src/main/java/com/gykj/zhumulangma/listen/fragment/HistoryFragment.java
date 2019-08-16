package com.gykj.zhumulangma.listen.fragment;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.listen.R;

/**
 * Author: Thomas.
 * Date: 2019/8/16 8:44
 * Email: 1071931588@qq.com
 * Description:
 */
@Route(path = AppConstants.Router.Listen.F_HISTORY)
public class HistoryFragment extends BaseFragment {


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
        return new String[]{"播放历史"};
    }


    @Override
    protected Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_listen_delete};
    }
}
