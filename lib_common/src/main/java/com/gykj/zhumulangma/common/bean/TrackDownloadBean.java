package com.gykj.zhumulangma.common.bean;

import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.converter.PropertyConverter;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: Thomas.
 * Date: 2019/8/20 8:53
 * Email: 1071931588@qq.com
 * Description:
 */
@Entity
public class TrackDownloadBean {
    @Id
    long trackId;
    @Convert(converter = DownloadStateConverter.class,columnType = Integer.class)
    DownloadState status;


    @Generated(hash = 81615015)
    public TrackDownloadBean(long trackId, DownloadState status) {
        this.trackId = trackId;
        this.status = status;
    }

    @Generated(hash = 411110430)
    public TrackDownloadBean() {
    }


    public long getTrackId() {
        return this.trackId;
    }


    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }


    public DownloadState getStatus() {
        return this.status;
    }


    public void setStatus(DownloadState status) {
        this.status = status;
    }


    public static class DownloadStateConverter implements PropertyConverter<DownloadState, Integer> {
        @Override
        public DownloadState convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (DownloadState state : DownloadState.values()) {
                if (state.value() == databaseValue) {
                    return state;
                }
            }
            return DownloadState.NOADD;
        }

        @Override
        public Integer convertToDatabaseValue(DownloadState entityProperty) {
            return entityProperty == null ? null : entityProperty.value();
        }
    }
}
