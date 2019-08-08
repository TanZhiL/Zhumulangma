package com.gykj.zhumulangma.module_task.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.mvvm.model.BaseModel;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.TaskService;
import com.gykj.zhumulangma.common.net.dto.TaskFeedbackDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.net.http.RxAdapter;

import io.reactivex.Observable;

/**
 * Author: Thomas.
 * Date: 2019/7/30 9:29
 * Email: 1071931588@qq.com
 * Description:
 */
public class FeedbackModel extends BaseModel {
    private final TaskService mTaskService;
    public FeedbackModel(Application application) {
        super(application);
        mTaskService= RetrofitManager.getInstance().getTaskService();
    }
    public Observable<ResponseDTO> feedback(TaskFeedbackDTO feedbackDTO){
        return mTaskService.feedback(feedbackDTO)
                .compose(RxAdapter.schedulersTransformer())
                .compose(RxAdapter.exceptionTransformer());
    }
}
