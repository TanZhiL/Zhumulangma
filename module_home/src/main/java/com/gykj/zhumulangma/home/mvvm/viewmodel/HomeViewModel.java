package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Thomas.
 * Date: 2019/8/13 11:10
 * Email: 1071931588@qq.com
 * Description:
 */
public class HomeViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<HotWord>> mHotWordsEvent;
    public HomeViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);

    }
    public void getHotWords(){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, String.valueOf(20));
        mModel.getHotWords(map)
                .subscribe(hotWordList -> getHotWordsEvent().setValue(hotWordList.getHotWordList()), e -> {
                    e.printStackTrace();
                });
    }


    public SingleLiveEvent<List<HotWord>> getHotWordsEvent() {
        return mHotWordsEvent =createLiveData(mHotWordsEvent);
    }

}
