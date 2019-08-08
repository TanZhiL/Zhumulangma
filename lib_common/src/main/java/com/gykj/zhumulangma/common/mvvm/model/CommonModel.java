package com.gykj.zhumulangma.common.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.bean.UploadFileBean;
import com.gykj.zhumulangma.common.net.CommonService;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.dto.ResponseDTO;
import com.gykj.zhumulangma.common.net.http.RxAdapter;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

/**
 * Author: Thomas.
 * Date: 2019/7/31 17:27
 * Email: 1071931588@qq.com
 * Description:
 */
public class CommonModel extends BaseModel {
    private CommonService mCommonService;

    public CommonModel(Application application) {
        super(application);
        mCommonService = RetrofitManager.getInstance().getCommonService();
    }

    public Observable<ResponseDTO<UploadFileBean>> uploadFile(MultipartBody.Part part) {
        return mCommonService.uploadFile(part)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }
    public Observable<ResponseDTO<List<UploadFileBean>>> uploadFiles(MultipartBody.Part[] parts) {
        return mCommonService.uploadFiles(parts)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }
}
