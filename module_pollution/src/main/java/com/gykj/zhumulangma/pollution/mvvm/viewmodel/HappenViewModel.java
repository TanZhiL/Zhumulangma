package com.gykj.zhumulangma.pollution.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.net.dto.PollutionHappenDTO;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.pollution.mvvm.model.HappenModel;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Author: Thomas.
 * Date: 2019/8/5 12:41
 * Email: 1071931588@qq.com
 * Description:
 */
public class HappenViewModel extends BaseViewModel<HappenModel> {

    public HappenViewModel(@NonNull Application application, HappenModel model) {
        super(application, model);
    }

    public void happen(String river_name,
                       String river_code,
                       float pi_data,
                       float bod5_data,
                       float tp_data,
                       float cod_data,
                       float an_data,
                       float do_data,
                       float pollute_index) {
        if(TextUtils.isEmpty(river_name)){
            ToastUtil.showToast("请选择河湖名称");
            return;
        }
        PollutionHappenDTO happenDTO=new PollutionHappenDTO(river_name,
                river_code,
                pi_data,
                bod5_data,
                tp_data,
                cod_data,
                an_data,
                do_data,
                pollute_index
                );
        Observable.defer(()->mModel.happen(happenDTO).doOnSubscribe(this))
                .doOnSubscribe(d->postShowTransLoadingViewEvent("上传中..."))
                .doFinally(()->postShowTransLoadingViewEvent(null))
                .subscribe(responseDTO -> {
                    postFinishSelfEvent();
                    ToastUtil.showToast("上传成功");
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });


    }
}
