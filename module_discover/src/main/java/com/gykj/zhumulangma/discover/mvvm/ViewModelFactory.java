package com.gykj.zhumulangma.discover.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

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

     /*   if (modelClass.isAssignableFrom(FeedbackViewModel.class)) {
            return (T) new FeedbackViewModel(mApplication, new FeedbackModel(mApplication),new BaseModel(mApplication));
        }else if (modelClass.isAssignableFrom(AcceptViewModel.class)) {
            return (T) new AcceptViewModel(mApplication, new AcceptModel(mApplication));
        }*/
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}