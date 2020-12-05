package com.gykj.zhumulangma.home.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AlbumDetailViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AlbumListViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerDetailViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerListViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.BatchDownloadViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.CategoryViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.ChildViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.CrosstalkViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.MainHomeViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.HomeViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.NovelViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayRadioViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayTrackViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RadioListViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.RankViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchAlbumViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchAnnouncerViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchRadioViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchTrackViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchViewModel;
import com.gykj.zhumulangma.home.mvvm.viewmodel.TrackListViewModel;

/**
 * Author: Thomas.
 * <br/>Date: 2019/7/30 9:30
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
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

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(ChildViewModel.class)) {
            return (T) new ChildViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(NovelViewModel.class)) {
            return (T) new NovelViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(SearchRadioViewModel.class)) {
            return (T) new SearchRadioViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(RankViewModel.class)) {
            return (T) new RankViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(AlbumListViewModel.class)) {
            return (T) new AlbumListViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(AlbumDetailViewModel.class)) {
            return (T) new AlbumDetailViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(MainHomeViewModel.class)) {
            return (T) new MainHomeViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(AnnouncerViewModel.class)) {
            return (T) new AnnouncerViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(PlayTrackViewModel.class)) {
            return (T) new PlayTrackViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(PlayRadioViewModel.class)) {
            return (T) new PlayRadioViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(RadioListViewModel.class)) {
            return (T) new RadioListViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(AnnouncerDetailViewModel.class)) {
            return (T) new AnnouncerDetailViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(TrackListViewModel.class)) {
            return (T) new TrackListViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(BatchDownloadViewModel.class)) {
            return (T) new BatchDownloadViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(AnnouncerListViewModel.class)) {
            return (T) new AnnouncerListViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(SearchAlbumViewModel.class)) {
            return (T) new SearchAlbumViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(SearchTrackViewModel.class)) {
            return (T) new SearchTrackViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(SearchAnnouncerViewModel.class)) {
            return (T) new SearchAnnouncerViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(CrosstalkViewModel.class)) {
            return (T) new CrosstalkViewModel(mApplication, new ZhumulangmaModel(mApplication));
        } else if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            return (T) new CategoryViewModel(mApplication, new ZhumulangmaModel(mApplication));
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}