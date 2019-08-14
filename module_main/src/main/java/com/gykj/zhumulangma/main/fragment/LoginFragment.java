package com.gykj.zhumulangma.main.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.config.API;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.main.R;
import com.gykj.zhumulangma.main.mvvm.MainViewModelFactory;
import com.gykj.zhumulangma.main.mvvm.viewmodel.LoginViewModel;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.concurrent.TimeUnit;


public class LoginFragment extends BaseMvvmFragment<LoginViewModel> implements View.OnLongClickListener {

    private EditText etCode;
    private EditText etDescerName;
    private EditText etDescerPhone;
    private EditText etGraerName;
    private EditText etGraerPhone;
    private TextView tvHostStatus;

    public LoginFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.main_fragment_login;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }
    @Override
    protected void initView(View view) {
        etCode = fd(R.id.et_code);
        etDescerName = fd(R.id.et_descer_name);
        etDescerPhone = fd(R.id.et_descer_phone);
        etGraerName = fd(R.id.et_graer_name);
        etGraerPhone = fd(R.id.et_graer_phone);
        tvHostStatus = fd(R.id.tv_host_status);
        tvHostStatus.setText(SPUtils.getInstance().getInt(AppConstants.SP.HOST)
                == API.HostStatus.OFFLINE ? "内网环境" : "外网环境");
    }

    @Override
    public void initListener() {

        addDisposable(RxView.clicks(fd(R.id.btn_login))
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(unit -> mViewModel._login(etCode.getText().toString(), etDescerName.getText().toString(),
                        etDescerPhone.getText().toString(), etGraerName.getText().toString(),
                        etGraerPhone.getText().toString())));

        tvHostStatus.setOnLongClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel._getUser();
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public Class<LoginViewModel> onBindViewModel() {
        return LoginViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return MainViewModelFactory.getInstance(App.getInstance());
    }

    @Override
    public void initViewObservable() {
        mViewModel.getUserBeanSingleLiveEvent().observe(this, userBean -> {
            if (userBean != null) {
                etCode.setText(userBean.getCode());
                etDescerName.setText(userBean.getDescer_name());
                etDescerPhone.setText(userBean.getDescer_phone());
                etGraerName.setText(userBean.getGraer_name());
                etGraerPhone.setText(userBean.getGraer_phone());
                //自动登陆
//                    mViewModel._login(etCode.getText().toString(),etDescerName.getText().toString(),etDescerPhone.getText().toString(),
//                            etGraerName.getText().toString(),etGraerPhone.getText().toString());
                start(new MainFragment());
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        /**
         * 切换网络环境
         */
        int status = SPUtils.getInstance().getInt(AppConstants.SP.HOST);
        if(status==API.HostStatus.OFFLINE){
            RetrofitManager.getInstance().setHostStatus(API.HostStatus.ONLINE);
            SPUtils.getInstance().put(AppConstants.SP.HOST,API.HostStatus.ONLINE);
            tvHostStatus.setText("外网环境");
            ToastUtil.showToast("切换至外网环境");
        }else {
            RetrofitManager.getInstance().setHostStatus(API.HostStatus.OFFLINE);
            SPUtils.getInstance().put(AppConstants.SP.HOST,API.HostStatus.OFFLINE);
            tvHostStatus.setText("内网环境");
            ToastUtil.showToast("切换至内网环境");
        }
        return false;
    }
}
