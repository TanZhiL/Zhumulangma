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
 * <br/>Date: 2019/9/20 9:37
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
@Entity
public class FavoriteBean {
    @Id
    long trackId;
    @Convert(converter = TrackConverter.class,columnType = String.class)
    Track track;
    long datetime;



    @Generated(hash = 991451167)
    public FavoriteBean(long trackId, Track track, long datetime) {
        this.trackId = trackId;
        this.track = track;
        this.datetime = datetime;
    }

    @Generated(hash = 653294794)
    public FavoriteBean() {
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

    public long getDatetime() {
        return this.datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
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
