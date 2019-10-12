package com.gykj.zhumulangma.user.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.gykj.zhumulangma.user.mvvm.model.MainUserModel;
import com.gykj.zhumulangma.user.mvvm.viewmodel.MainUserViewModel;

/**
 * Author: Thomas.
 * <br/>Date: 2019/7/30 9:30
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
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
        if (modelClass.isAssignableFrom(MainUserViewModel.class)) {
            return (T) new MainUserViewModel(mApplication, new MainUserModel(mApplication));
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}