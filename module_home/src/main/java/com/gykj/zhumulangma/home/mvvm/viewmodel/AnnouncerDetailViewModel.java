package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Thomas.
 * Date: 2019/9/10 17:37
 * Email: 1071931588@qq.com
 * Description:
 */
public class AnnouncerDetailViewModel extends BaseViewModel<ZhumulangmaModel> {
    public AnnouncerDetailViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getAlbum(String announcerId) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.AID , announcerId);
        map.put(DTransferConstants.PAGE, String.valueOf(1));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(5));

    }
}
