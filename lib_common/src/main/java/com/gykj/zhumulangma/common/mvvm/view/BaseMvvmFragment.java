package com.gykj.zhumulangma.common.mvvm.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;

import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;

/**
 * Description: <BaseMvpFragment><br>
 * Author:      mxdl<br>
 * Date:        2019/06/30<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public abstract class BaseMvvmFragment<VM extends BaseViewModel> extends BaseFragment {
    protected VM mViewModel;
    protected void initParam() {
        initViewModel();
        initBaseViewObservable();
        initViewObservable();
    }
    protected void initViewModel() {
        mViewModel = createViewModel();
        getLifecycle().addObserver(mViewModel);
    }
    protected VM createViewModel(){
        return ViewModelProviders.of(this,onBindViewModelFactory()).get(onBindViewModel());
    }
    protected abstract Class<VM> onBindViewModel();
    protected abstract ViewModelProvider.Factory onBindViewModelFactory();
    protected abstract void initViewObservable();

    protected void initBaseViewObservable() {
        mViewModel.getShowInitViewEvent().observe(this, (Observer<Boolean>) show -> showInitView(show));
        mViewModel.getShowLoadingViewEvent().observe(this, (Observer<String>) tip -> showLoadingView(tip));
        mViewModel.getShowEmptyViewEvent().observe(this, (Observer<Boolean>) show -> showEmptyView(show));
        mViewModel.getShowErrorViewEvent().observe(this, (Observer<Boolean>) show -> showErrorView(show));
        mViewModel.getFinishSelfEvent().observe(this, (Observer<Void>) v -> pop());
        mViewModel.getClearStatusEvent().observe(this, (Observer<Void>) v -> clearStatus());
    }


}
