package com.gykj.zhumulangma.main.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.net.NetManager;
import com.gykj.zhumulangma.common.net.RxAdapter;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class MainModel extends ZhumulangmaModel {


    public MainModel(Application application) {
        super(application);
    }

    public Observable<BingBean> getBing(String format, String n) {

        return mNetManager.getCommonService().getBing(format, n)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

}
