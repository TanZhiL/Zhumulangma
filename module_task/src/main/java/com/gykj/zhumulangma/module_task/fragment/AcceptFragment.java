package com.gykj.zhumulangma.module_task.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.Application;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.module_task.R;
import com.gykj.zhumulangma.module_task.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.module_task.mvvm.viewmodel.AcceptViewModel;

@Route(path = AppConstants.Router.Task.F_ACCEPT)
public class AcceptFragment extends BaseMvvmFragment<AcceptViewModel> {

    @Autowired(name = KeyCode.Task.TYPE)
    public String type;

    private EditText etPersonName;
    private EditText etPersonPhone;
    private EditText etTaskContent;
    public AcceptFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.task_fragment_accept;
    }

    @Override
    protected void initView(View view) {
        etPersonName=fd(R.id.et_person_name);
        etPersonPhone=fd(R.id.et_person_phone);
        etTaskContent=fd(R.id.et_task_content);

    }

    @Override
    public void initData() {
        mViewModel.accept(type);
    }

    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"任务接收"};
    }

    @Override
    public Class<AcceptViewModel> onBindViewModel() {
        return AcceptViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(Application.getInstance());
    }

    @Override
    public void initViewObservable() {
        mViewModel.getTaskBeanSingleLiveEvent().observe(this, taskBean -> {
            etPersonName.setText(taskBean.getTask_person_name());
            etPersonPhone.setText(taskBean.getTask_person_phone());
            etTaskContent.setText(taskBean.getTask_content());
        });
    }

}
