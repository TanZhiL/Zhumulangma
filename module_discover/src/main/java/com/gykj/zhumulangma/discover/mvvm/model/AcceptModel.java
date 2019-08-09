package com.gykj.zhumulangma.discover.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.bean.TaskBean;
import com.gykj.zhumulangma.common.mvvm.model.BaseModel;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.TaskService;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.net.http.RxAdapter;

import io.reactivex.Observable;

/**
 * Author: Thomas.
 * Date: 2019/7/30 9:29
 * Email: 1071931588@qq.com
 * Description:
 */
public class AcceptModel extends BaseModel {
    private final TaskService mTaskService;
    public AcceptModel(Application application) {
        super(application);
        mTaskService= RetrofitManager.getInstance().getTaskService();
    }
    public Observable<ResponseDTO<TaskBean>> accept(String type, String status){
        return mTaskService.accept(type,status)
                .compose(RxAdapter.schedulersTransformer())
                .compose(RxAdapter.exceptionTransformer());
    }
}
