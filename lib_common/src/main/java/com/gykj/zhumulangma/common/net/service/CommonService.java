package com.gykj.zhumulangma.common.net.service;

import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.net.Constans;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
/**
 * Author: Thomas.<br/>
 * Date: 2019/10/12 11:16<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:公用Api
 */
public interface CommonService {

    /**
     * 获取必应数据
     * @param type
     * @param status
     * @return
     */
    @GET(Constans.BING_URL)
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
