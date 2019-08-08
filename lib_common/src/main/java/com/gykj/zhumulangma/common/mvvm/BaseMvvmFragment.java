package com.gykj.zhumulangma.common.mvvm;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;

import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Description: <BaseMvpFragment><br>
 * Author:      mxdl<br>
 * Date:        2019/06/30<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public abstract class BaseMvvmFragment<VM extends BaseViewModel> extends BaseFragment {
    protected VM mViewModel;
    public void initParam() {
        initViewModel();
        initBaseViewObservable();
        initViewObservable();
    }
    private void initViewModel() {
        mViewModel = createViewModel();
        getLifecycle().addObserver(mViewModel);
    }
    public VM createViewModel(){
        return ViewModelProviders.of(this,onBindViewModelFactory()).get(onBindViewModel());
    }
    public abstract Class<VM> onBindViewModel();
    public abstract ViewModelProvider.Factory onBindViewModelFactory();
    public abstract void initViewObservable();

    protected void initBaseViewObservable() {
        mViewModel.getUC().getShowInitLoadViewEvent().observe(this, (Observer<Boolean>) show -> showInitLoadView(show));
        mViewModel.getUC().getShowTransLoadingViewEvent().observe(this, (Observer<String>) tip -> showTransLoadingView(tip));
        mViewModel.getUC().getShowNoDataViewEvent().observe(this, (Observer<Boolean>) show -> showNoDataView(show));
        mViewModel.getUC().getShowNetWorkErrViewEvent().observe(this, (Observer<Boolean>) show -> showNetWorkErrView(show));

        mViewModel.getUC().getFinishSelfEvent().observe(this, (Observer<Void>) v -> pop());
        mViewModel.getUC().getStartFragmentEvent().observe(this, (Observer<ISupportFragment>) fragment -> {
            if(null!=fragment){
                start(fragment);
            }
        });
    }


}
