package com.gykj.zhumulangma.common.net.service;

import com.gykj.zhumulangma.common.net.Constans;
import com.gykj.zhumulangma.common.net.dto.BannerDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnDetailDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnInfoDTO;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

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
    Observable<BannerDTO> getBanners(@QueryMap Map<String,String> params);

    /**
     * 获取所有听单列表
     * @param params
     * @return
     */
    @Headers(Constans.HEADER_XMLY)
    @GET("/operation/columns")
    Observable<ColumnDTO> getColumns(@QueryMap Map<String,String> params);

    /**
     * 获取听单基本信息
     * @param params
     * @return
     */
    @Headers(Constans.HEADER_XMLY)
    @GET("/operation/batch_get_columns")
    Observable<ColumnInfoDTO> getColumnInfo(@QueryMap Map<String,String> params);

    /**
     * 分页获取听单专辑列表
     * @param params
     * @return
     */
    @Headers(Constans.HEADER_XMLY)
    @GET("/operation/browse_column_content")
    Observable<ColumnDetailDTO<Album>> getBrowseAlbumColumn(@QueryMap Map<String,String> params);

    /**
     * 分页获取听单声音列表
     * @param params
     * @return
     */
    @Headers(Constans.HEADER_XMLY)
    @GET("/operation/browse_column_content")
    Observable<ColumnDetailDTO<Track>> getBrowseTrackColumn(@QueryMap Map<String,String> params);
}
