package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.live.program.ProgramList;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Author: Thomas.
 * Date: 2019/9/5 14:35
 * Email: 1071931588@qq.com
 * Description:
 */
public class PlayRadioViewModel extends BaseViewModel<ZhumulangmaModel> {

    private SingleLiveEvent<ProgramList> mProgramsSingleLiveEvent;
    public PlayRadioViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }
 public void _getPrograms(String radioId){
     Map<String, String> map = new HashMap<>();
     map.put(DTransferConstants.RADIOID, radioId);
        mModel.getProgram(map).subscribe(programList ->
                getProgramsSingleLiveEvent().postValue(programList), e->e.printStackTrace());
 }

    public SingleLiveEvent<ProgramList> getProgramsSingleLiveEvent() {
        return mProgramsSingleLiveEvent=createLiveData(mProgramsSingleLiveEvent);
    }
}
