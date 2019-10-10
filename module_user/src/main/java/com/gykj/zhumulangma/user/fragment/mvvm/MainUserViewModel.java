package com.gykj.zhumulangma.user.fragment.mvvm;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.net.API;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Author: Thomas.
 * Date: 2019/10/10 8:52
 * Email: 1071931588@qq.com
 * Description:
 */
public class MainUserViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<String> mStarEvent;
    private SingleLiveEvent<String> mForkEvent;
    public MainUserViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void init(){
        mModel.getCommonBody(API.GITHUB_STAR)
                .doOnNext(body -> {
                 List list =  new Gson().fromJson(body.string(),new TypeToken<List>(){}.getType());
                 getStarEvent().setValue(convertNum(list.size()));
                })
                .flatMap((Function<ResponseBody, ObservableSource<ResponseBody>>) body -> mModel.getCommonBody(API.GITHUB_FORK))
                .subscribe(body -> {
                    List list =  new Gson().fromJson(body.string(),new TypeToken<List>(){}.getType());
                    getForkEvent().setValue(convertNum(list.size()));
                }, e->e.printStackTrace());
    }

    private String convertNum(int num){
        if(num<1000){
            return String.valueOf(num);
        }
        String dy1000= String.valueOf(num/1000);
        String xy1000= String.valueOf(num%1000/100);

        return dy1000+"."+xy1000+"k";
    }
    public SingleLiveEvent<String> getStarEvent() {
        return mStarEvent=createLiveData(mStarEvent);
    }

    public SingleLiveEvent<String> getForkEvent() {
        return mForkEvent=createLiveData(mForkEvent);
    }

}
