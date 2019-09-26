package com.gykj.zhumulangma.home.mvvm;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.home.mvvm.model.RadioModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AlbumDetailViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AlbumListViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerDetailViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerListViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.BatchDownloadViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.FineViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HomeViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HotViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayRadioViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayTrackViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioListViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RankViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchResultViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.TrackListViewModel;

/**
 * Author: Thomas.
 * Date: 2019/7/30 9:30
 * Email: 1071931588@qq.com
 * Description:
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;
    private final App mApplication;

    public static ViewModelFactory getInstance(App application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application);
                }
            }
        }
        return INSTANCE;
    }
    private ViewModelFactory(App application) {
        this.mApplication = application;
    }
    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HotViewModel.class)) {
            return (T) new HotViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(FineViewModel.class)) {
            return (T) new FineViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(RadioViewModel.class)) {
            return (T) new RadioViewModel(mApplication, new RadioModel(mApplication));
        }else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(SearchResultViewModel.class)) {
            return (T) new SearchResultViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(RankViewModel.class)) {
            return (T) new RankViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(AlbumListViewModel.class)) {
            return (T) new AlbumListViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(AlbumDetailViewModel.class)) {
            return (T) new AlbumDetailViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(AnnouncerViewModel.class)) {
            return (T) new AnnouncerViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(PlayTrackViewModel.class)) {
            return (T) new PlayTrackViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(PlayRadioViewModel.class)) {
            return (T) new PlayRadioViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }else if (modelClass.isAssignableFrom(RadioListViewModel.class)) {
            return (T) new RadioListViewModel(mApplication, new RadioModel(mApplication));
        }else if (modelClass.isAssignableFrom(AnnouncerDetailViewModel.class)) {
            return (T) new AnnouncerDetailViewModel(mApplication, new RadioModel(mApplication));
        }else if (modelClass.isAssignableFrom(TrackListViewModel.class)) {
            return (T) new TrackListViewModel(mApplication, new RadioModel(mApplication));
        }else if (modelClass.isAssignableFrom(BatchDownloadViewModel.class)) {
            return (T) new BatchDownloadViewModel(mApplication, new RadioModel(mApplication));
        }else if (modelClass.isAssignableFrom(AnnouncerListViewModel.class)) {
            return (T) new AnnouncerListViewModel(mApplication, new RadioModel(mApplication));
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}