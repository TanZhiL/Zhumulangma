package com.gykj.zhumulangma.common.bean;

import androidx.annotation.Nullable;

import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategory;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/11 10:43
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class AnnouncerCategoryBean extends AnnouncerCategory {


    @Override
    public boolean equals(@Nullable Object obj) {
        return getId()==((AnnouncerCategoryBean)obj).getId();

    }
}
