package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadioViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<List<Radio>> mLocalSingleLiveEvent;
    private SingleLiveEvent<List<Radio>> mTopSingleLiveEvent;

    private int totalLocalPage =1;
    private int curLocalPage=1;
    private String cityCode="4301";

    public RadioViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getLocalList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CITY_CODE, cityCode);
        map.put(DTransferConstants.PAGE_SIZE, "3");
        curLocalPage=curLocalPage>=totalLocalPage?1:curLocalPage;
        map.put(DTransferConstants.PAGE,String.valueOf(curLocalPage++));
        mModel.getRadiosByCity(map)
                .subscribe(radioList -> {
                    totalLocalPage=radioList.getTotalPage();
                    getLocalSingleLiveEvent().postValue(radioList.getRadios());
                }, e->e.printStackTrace());
    }


    public void getTopList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_COUNT, "3");

        mModel.getRankRadios(map)
                .subscribe(radioList -> getTopSingleLiveEvent().postValue(radioList.getRadios())
                        , e->e.printStackTrace());
    }

    public SingleLiveEvent<List<Radio>> getLocalSingleLiveEvent() {
        return mLocalSingleLiveEvent=createLiveData(mLocalSingleLiveEvent);
    }

    public SingleLiveEvent<List<Radio>> getTopSingleLiveEvent() {
        return mTopSingleLiveEvent=createLiveData(mTopSingleLiveEvent);
    }
}
