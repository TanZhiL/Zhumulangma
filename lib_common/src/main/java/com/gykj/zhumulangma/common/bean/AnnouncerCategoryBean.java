package com.gykj.zhumulangma.common.bean;

import androidx.annotation.Nullable;

import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategory;

import java.util.Objects;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/11 10:43
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class AnnouncerCategoryBean extends AnnouncerCategory {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnouncerCategoryBean that = (AnnouncerCategoryBean) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
