package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.net.dto.PollutionHappenDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PollutionService {
    String HOST1=API.BaseUrl.KEY+":"+API.BaseUrl.HOST1;

    @Headers(HOST1)
    @POST("waterqul/waterqul_insert")
    Observable<ResponseDTO> happen(@Body PollutionHappenDTO happenDTO);


}
