package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.bean.UploadFileBean;
import com.gykj.zhumulangma.common.bean.UserBean;
import com.gykj.zhumulangma.common.net.config.API;
import com.gykj.zhumulangma.common.net.dto.LoginDTO;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CommonService {
    String HOST1=API.BaseUrl.KEY+":"+API.BaseUrl.HOST1;

    @Headers(HOST1)
    @POST("app/tokenlogin")
    Observable<ResponseDTO<UserBean>> login(@Body LoginDTO loginDTO);

    @Headers(HOST1)
    @POST("file/fileupload")
    @Multipart
    Observable<ResponseDTO<UploadFileBean>> uploadFile(@Part MultipartBody.Part part);

    @Headers(HOST1)
    @POST("file/batchfileupload")
    @Multipart
    Observable<ResponseDTO<List<UploadFileBean>>> uploadFiles(@Part MultipartBody.Part[] parts);
}
