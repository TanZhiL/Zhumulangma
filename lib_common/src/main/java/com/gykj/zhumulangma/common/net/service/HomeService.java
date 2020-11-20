package com.gykj.zhumulangma.common.net.service;

import com.gykj.zhumulangma.common.net.Constans;
import com.gykj.zhumulangma.common.net.dto.BannerDTO;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/20 19:05<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public interface HomeService {

    @Headers(Constans.HEADER_XMLY)
    @GET("/operation/banners")
    Observable<BannerDTO> getCategoryBannersV2(@QueryMap Map<String,String> params);


}
