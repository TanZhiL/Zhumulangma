package com.gykj.zhumulangma.common.mvvm.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.arch.lifecycle.ViewModelProviders;

import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;

/**
 * Description: <BaseMvvmActivity><br>
 * Author:      mxdl<br>
 * Date:        2019/06/30<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
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
        mViewModel.getShowInitViewEvent().observe(this, (Observer<Boolean>) show -> showInitView(show));
        mViewModel.getShowLoadingViewEvent().observe(this, (Observer<String>) show -> showLoadingView(show));
        mViewModel.getShowEmptyViewEvent().observe(this, (Observer<Boolean>) show -> showEmptyView(show));
        mViewModel.getShowErrorViewEvent().observe(this, (Observer<Boolean>) show -> showErrorView(show));
        mViewModel.getFinishSelfEvent().observe(this, (Observer<Void>) v -> finish());

    }

}
