package com.gykj.zhumulangma.common.bean;

import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Author: Thomas.
 * Date: 2019/8/20 8:30
 * Email: 1071931588@qq.com
 * Description:
 */
@Entity
public class PlayHistoryBean {
    @Id
    long soundId;
    long albumId;
    String kind;
    int percent;
    long datatime;
    @Convert(converter =TrackConverter.class,columnType = String.class)
    Track track;
    @Generated(hash = 661660988)
    public PlayHistoryBean(long soundId, long albumId, String kind, int percent,
            long datatime, Track track) {
        this.soundId = soundId;
        this.albumId = albumId;
        this.kind = kind;
        this.percent = percent;
        this.datatime = datatime;
        this.track = track;
    }
    @Generated(hash = 1831795327)
    public PlayHistoryBean() {
    }
    public long getSoundId() {
        return this.soundId;
    }
    public void setSoundId(long soundId) {
        this.soundId = soundId;
    }
    public String getKind() {
        return this.kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public int getPercent() {
        return this.percent;
    }
    public void setPercent(int percent) {
        this.percent = percent;
    }
    public long getDatatime() {
        return this.datatime;
    }
    public void setDatatime(long datatime) {
        this.datatime = datatime;
    }
    public Track getTrack() {
        return this.track;
    }
    public void setTrack(Track track) {
        this.track = track;
    }
    public long getAlbumId() {
        return this.albumId;
    }
    public void setAlbumId(long albumId) {
        this.albumId = albumId;
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
