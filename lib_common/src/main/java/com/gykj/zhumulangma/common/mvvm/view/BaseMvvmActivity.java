package com.gykj.zhumulangma.common.mvvm.view;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.ViewModelProviders;

import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;

import java.lang.reflect.ParameterizedType;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:MvvmActivity基类
 */
public abstract class BaseMvvmActivity<DB extends ViewDataBinding,VM extends BaseViewModel> extends BaseActivity<DB> {
    protected VM mViewModel;

    @Override
    public void initParam() {
        initViewModel();
        initBaseViewObservable();
        initViewObservable();
    }

    @Override
    public void initView() {
        showInitView();
    }

    private void initViewModel() {
        mViewModel = createViewModel();
    }

    public VM createViewModel() {
        Class<VM> tClass = (Class<VM>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        return ViewModelProviders.of(this, onBindViewModelFactory()).get(tClass);
    }

    public abstract Factory onBindViewModelFactory();

    public abstract void initViewObservable();

    protected void initBaseViewObservable() {
        mViewModel.getShowInitViewEvent().observe(this, (Observer<Void>) show -> showInitView());
        mViewModel.getShowLoadingViewEvent().observe(this, (Observer<String>) this::showLoadingView);
        mViewModel.getShowEmptyViewEvent().observe(this, (Observer<Void>) show -> showEmptyView());
        mViewModel.getShowErrorViewEvent().observe(this, (Observer<Void>) show -> showErrorView());
        mViewModel.getFinishSelfEvent().observe(this, (Observer<Void>) v -> finish());
        mViewModel.getClearStatusEvent().observe(this, (Observer<Void>) v -> clearStatus());
    }

}
