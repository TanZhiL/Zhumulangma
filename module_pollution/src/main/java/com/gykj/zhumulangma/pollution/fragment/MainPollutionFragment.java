package com.gykj.zhumulangma.pollution.fragment;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.polltion.R;

import me.yokeyword.fragmentation.ISupportFragment;


@Route(path = AppConstants.Router.Pollution.F_MAIN)
public class MainPollutionFragment extends BaseFragment implements View.OnClickListener {


    private View llHappen;
    private View llAccept;
    private View llFeedback;

    @Override
    public int onBindLayout() {
        return R.layout.pollution_fragment_main;
    }
    @Override
    public void initView(View view) {
        llAccept = fd(R.id.ll_accept);
        llHappen = fd(R.id.ll_happen);
        llFeedback = fd(R.id.ll_feedback);

    }

    @Override
    public void initListener() {
        llAccept.setOnClickListener(this);
        llHappen.setOnClickListener(this);
        llFeedback.setOnClickListener(this);
    }
    @Override
    public void initData() {

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_happen) {
            start(new HappenFragment());
        }else if (v.getId() == R.id.ll_accept) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Task.F_ACCEPT)
                    .withString(KeyCode.Task.TYPE,"水质事件").navigation();
            if(null!=navigation){
                start((ISupportFragment) navigation);
            }
        } else if (v.getId() == R.id.ll_feedback) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Task.F_FEEDBACK).navigation();
            if(null!=navigation){
                start((ISupportFragment) navigation);
            }
        }

    }
    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"水质污染处理场景"};
    }
}
