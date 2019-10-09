package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.bean.TaskBean;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.net.dto.TaskFeedbackDTO;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TaskService {
    String HOST1=API.BaseUrl.KEY+":"+API.BaseUrl.HOST1;

    @Headers(HOST1)
    @POST("task/task_randomover")
    Observable<ResponseDTO> feedback(@Body TaskFeedbackDTO feedbackDTO);

    @Headers(HOST1)
    @GET("task/mytask")
    Observable<ResponseDTO<TaskBean>> accept(@Query("type") String type, @Query("status") String status);
}
