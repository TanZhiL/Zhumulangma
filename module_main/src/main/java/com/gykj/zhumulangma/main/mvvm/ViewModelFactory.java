package com.gykj.zhumulangma.main.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.main.mvvm.model.LoginModel;
import com.gykj.zhumulangma.main.mvvm.viewmodel.LoginViewModel;
import com.gykj.zhumulangma.main.mvvm.viewmodel.MainViewModel;

/**
 * Description: <NewsViewModelFactory><br>
 * Author:      mxdl<br>
 * Date:        2019/7/2<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;
    private final Application mApplication;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application);
                }
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application) {
        this.mApplication = application;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(mApplication, new LoginModel(mApplication));
        }else    if (modelClass.isAssignableFrom(MainViewModel.class)) {
          return (T) new MainViewModel(mApplication, new ZhumulangmaModel(mApplication));
      }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
