package com.gykj.zhumulangma.pollution.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.mvvm.model.BaseModel;
import com.gykj.zhumulangma.common.net.PollutionService;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.dto.PollutionHappenDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.net.http.RxAdapter;

import io.reactivex.Observable;

/**
 * Author: Thomas.
 * Date: 2019/7/30 9:29
 * Email: 1071931588@qq.com
 * Description:
 */
public class HappenModel extends BaseModel {
    private final PollutionService mPollutionService;
    public HappenModel(Application application) {
        super(application);
        mPollutionService= RetrofitManager.getInstance().getPollutionService();
    }
    public Observable<ResponseDTO> happen(PollutionHappenDTO happenDTO){
        return mPollutionService.happen(happenDTO)
                .compose(RxAdapter.schedulersTransformer())
                .compose(RxAdapter.exceptionTransformer());
    }
}
