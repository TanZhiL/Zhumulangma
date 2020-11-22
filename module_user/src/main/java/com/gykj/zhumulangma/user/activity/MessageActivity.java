package com.gykj.zhumulangma.user.activity;


import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.mvvm.view.BaseActivity;
import com.gykj.zhumulangma.user.R;
import com.gykj.zhumulangma.user.databinding.UserActivityMessageBinding;


@Route(path = Constants.Router.User.F_MESSAGE)
public class MessageActivity extends BaseActivity<UserActivityMessageBinding> {



    public MessageActivity() {

    }


    @Override
    public int onBindLayout() {
        return R.layout.user_activity_message;
    }

    @Override
    public void initView() {

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
