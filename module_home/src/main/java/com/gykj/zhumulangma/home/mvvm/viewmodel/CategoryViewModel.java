package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.home.bean.HomeItem;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends BaseRefreshViewModel<ZhumulangmaModel, HomeItem> {
    private SingleLiveEvent<List<HomeItem>> mNovelItemsEvent;

    public CategoryViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init() {
        List<HomeItem> homeItems= new ArrayList<>();
        homeItems.add(new HomeItem(HomeItem.CATEGOTY,null));
        getNovelItemsEvent().setValue(homeItems);
        getClearStatusEvent().call();
    }



    public SingleLiveEvent<List<HomeItem>> getNovelItemsEvent() {
        return mNovelItemsEvent = createLiveData(mNovelItemsEvent);
    }
}

