package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.net.dto.EventReportDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface EventService {
    String HOST1=API.BaseUrl.KEY+":"+API.BaseUrl.HOST1;

    @Headers(HOST1)
    @POST("report/report_insert")
    Observable<ResponseDTO> report(@Body EventReportDTO repotDTO);

}
