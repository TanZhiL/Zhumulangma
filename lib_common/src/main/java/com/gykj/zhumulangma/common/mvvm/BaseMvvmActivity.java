package com.gykj.zhumulangma.common.mvvm;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.arch.lifecycle.ViewModelProviders;

import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.util.log.TLog;

import me.yokeyword.fragmentation.ISupportFragment;

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
    public VM createViewModel(){
        return ViewModelProviders.of(this,onBindViewModelFactory()).get(onBindViewModel());
    }
    public abstract Class<VM> onBindViewModel();
    public abstract Factory onBindViewModelFactory();
    public abstract void initViewObservable();

    protected void initBaseViewObservable() {
        mViewModel.getUC().getShowInitLoadViewEvent().observe(this, (Observer<Boolean>) show -> showInitLoadView(show));
        mViewModel.getUC().getShowTransLoadingViewEvent().observe(this, (Observer<String>) show -> {
            TLog.v("MYTAG","view postShowTransLoadingViewEvent start...");
            showTransLoadingView(show);
        });
        mViewModel.getUC().getShowNoDataViewEvent().observe(this, (Observer<Boolean>) show -> showNoDataView(show));
        mViewModel.getUC().getShowNetWorkErrViewEvent().observe(this, (Observer<Boolean>) show -> showNetWorkErrView(show));

        mViewModel.getUC().getFinishSelfEvent().observe(this, (Observer<Void>) v -> finish());

        mViewModel.getUC().getStartFragmentEvent().observe(this, (Observer<ISupportFragment>) fragment -> {
            if(null!=fragment){
                start(fragment);
            }
        });
    }

}
