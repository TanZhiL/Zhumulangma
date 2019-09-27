package com.gykj.zhumulangma.common.mvvm.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.arch.lifecycle.ViewModelProviders;

import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;

/**
 * Author: Thomas.
 * Date: 2019/9/10 8:23
 * Email: 1071931588@qq.com
 * Description:MvvmActivity基类
 */
public abstract class BaseMvvmActivity<VM extends BaseViewModel> extends BaseActivity {
    protected VM mViewModel;

    @Override
    public void initParam() {
        initViewModel();
        initBaseViewObservable();
        initViewObservable();
    }

    private void initViewModel() {
        mViewModel = createViewModel();
        getLifecycle().addObserver(mViewModel);
    }

    public VM createViewModel() {
        return ViewModelProviders.of(this, onBindViewModelFactory()).get(onBindViewModel());
    }

    public abstract Class<VM> onBindViewModel();

    public abstract Factory onBindViewModelFactory();

    public abstract void initViewObservable();

    protected void initBaseViewObservable() {
        mViewModel.getShowInitViewEvent().observe(this, (Observer<Void>) show -> showInitView());
        mViewModel.getShowLoadingViewEvent().observe(this, (Observer<String>) tip -> showLoadingView(tip));
        mViewModel.getShowEmptyViewEvent().observe(this, (Observer<Void>) show -> showEmptyView());
        mViewModel.getShowErrorViewEvent().observe(this, (Observer<Void>) show -> showErrorView());
        mViewModel.getFinishSelfEvent().observe(this, (Observer<Void>) v -> pop());
        mViewModel.getClearStatusEvent().observe(this, (Observer<Void>) v -> clearStatus());
    }

}
