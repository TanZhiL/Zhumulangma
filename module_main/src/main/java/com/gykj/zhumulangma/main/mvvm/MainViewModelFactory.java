package com.gykj.zhumulangma.main.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.gykj.zhumulangma.main.mvvm.model.LoginModel;
import com.gykj.zhumulangma.main.mvvm.viewmodel.LoginViewModel;

/**
 * Description: <NewsViewModelFactory><br>
 * Author:      mxdl<br>
 * Date:        2019/7/2<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile MainViewModelFactory INSTANCE;
    private final Application mApplication;

    public static MainViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (MainViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MainViewModelFactory(application);
                }
            }
        }
        return INSTANCE;
    }

    private MainViewModelFactory(Application application) {
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
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
