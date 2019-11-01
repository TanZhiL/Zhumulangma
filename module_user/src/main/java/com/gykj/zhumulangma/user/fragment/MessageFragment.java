package com.gykj.zhumulangma.user.fragment;


import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.user.R;
import com.gykj.zhumulangma.user.databinding.UserFragmentMessageBinding;


@Route(path = Constants.Router.User.F_MESSAGE)
public class MessageFragment extends BaseFragment<UserFragmentMessageBinding>{



    public MessageFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.user_fragment_message;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public String[] onBindBarTitleText() {
        return new String[]{"消息中心"};
    }

    @Override
    public SimpleBarStyle onBindBarRightStyle() {
        return SimpleBarStyle.RIGHT_ICON;
    }

    @Override
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_settings,R.drawable.ic_common_edit};
    }
}
