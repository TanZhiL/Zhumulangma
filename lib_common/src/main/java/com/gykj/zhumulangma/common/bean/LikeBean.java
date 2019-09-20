package com.gykj.zhumulangma.common.bean;

import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.converter.PropertyConverter;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: Thomas.
 * Date: 2019/9/20 9:37
 * Email: 1071931588@qq.com
 * Description:
 */
@Entity
public class LikeBean {
    @Id
    long trackId;
    @Convert(converter = TrackConverter.class,columnType = String.class)
    Track track;
    long datetiem;

    @Generated(hash = 2028030783)
    public LikeBean(long trackId, Track track, long datetiem) {
        this.trackId = trackId;
        this.track = track;
        this.datetiem = datetiem;
    }

    @Generated(hash = 1258777425)
    public LikeBean() {
    }

    public long getTrackId() {
        return this.trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public Track getTrack() {
        return this.track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public long getDatetiem() {
        return this.datetiem;
    }

    public void setDatetiem(long datetiem) {
        this.datetiem = datetiem;
    }

    public static class TrackConverter implements PropertyConverter<Track, String> {
        @Override
        public Track convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            return new Gson().fromJson(databaseValue, Track.class);
        }

        @Override
        public String convertToDatabaseValue(Track entityProperty) {
            if (entityProperty == null) {
                return null;
            }
            return new Gson().toJson(entityProperty);
        }
    }
}
