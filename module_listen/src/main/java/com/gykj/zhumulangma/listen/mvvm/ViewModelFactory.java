package com.gykj.zhumulangma.listen.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.gykj.zhumulangma.listen.mvvm.model.HappenModel;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.HappenViewModel;

/**
 * Author: Thomas.
 * Date: 2019/7/30 9:30
 * Email: 1071931588@qq.com
 * Description:
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
        if (modelClass.isAssignableFrom(HappenViewModel.class)) {
            return (T) new HappenViewModel(mApplication, new HappenModel(mApplication));
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}