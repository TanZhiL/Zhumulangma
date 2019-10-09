package com.gykj.zhumulangma.main.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.http.RxAdapter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class MainModel extends ZhumulangmaModel {
    RetrofitManager mRetrofitManager=RetrofitManager.getInstance();

    public MainModel(Application application) {
        super(application);
    }
    public Observable<BingBean> getBing(String format,String n){
        return mRetrofitManager.getCommonService().getBing(format,n)
                .compose(RxAdapter.exceptionTransformer());
    }

    public Observable<ResponseBody> getBingImage(String ur){
        return mRetrofitManager.getCommonService().getBingImage(ur)
                .compose(RxAdapter.exceptionTransformer());
    }
}
