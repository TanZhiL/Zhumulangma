package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.bean.UserBean;
import com.gykj.zhumulangma.common.net.dto.LoginDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface CommonService {

    @Headers(Api.HEADER_HOST1)
    @POST("app/tokenlogin")
    Observable<ResponseDTO<UserBean>> login(@Body LoginDTO loginDTO);


    @GET(Api.BING_URL)
    Observable<BingBean> getBing(@Query("format") String type, @Query("n") String status);

    /**
     * 从网络中获取ResponseBody
     *
     * @param url
     * @return
     */
    @GET
    Observable<ResponseBody> getCommonBody(@Url String url);
}
