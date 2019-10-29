package com.gykj.zhumulangma.main.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.net.RxAdapter;

import io.reactivex.Observable;
import io.rx_cache2.EvictProvider;

public class MainModel extends ZhumulangmaModel {


    public MainModel(Application application) {
        super(application);
    }

    public Observable<BingBean> getBing(String format, String n) {

        return mNetManager.getCacheProvider().getBing(mNetManager.getCommonService().getBing(format, n),new EvictProvider(true))
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

}
