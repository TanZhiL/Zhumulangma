package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.net.config.API;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface VideoService {
    String HOST1=API.BaseUrl.KEY+":"+API.BaseUrl.HOST1;

    @Headers(HOST1)
    @GET("alarm/add")
    Observable<ResponseDTO> alarm(@Query("num") int person_num);

}
