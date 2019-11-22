package com.gykj.zhumulangma.listen.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.listen.mvvm.model.HistoryModel;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.DownloadSortViewModel;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.DownloadViewModel;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.FavoriteViewModel;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.HistoryViewModel;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.SubscribeViewModel;

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
        if (modelClass.isAssignableFrom(HistoryViewModel.class)) {
            return (T) new HistoryViewModel(mApplication, new HistoryModel(mApplication));
        }else if (modelClass.isAssignableFrom(DownloadViewModel.class)) {
            return (T) new DownloadViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(SubscribeViewModel.class)) {
            return (T) new SubscribeViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(FavoriteViewModel.class)) {
            return (T) new FavoriteViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(DownloadSortViewModel.class)) {
            return (T) new DownloadSortViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }


        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}