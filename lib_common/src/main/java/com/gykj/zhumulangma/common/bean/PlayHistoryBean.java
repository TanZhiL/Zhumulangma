package com.gykj.zhumulangma.common.bean;

import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
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
    long groupId;
    String kind;
    int percent;
    long datatime;
    @Convert(converter =TrackConverter.class,columnType = String.class)
    Track track;
    @Convert(converter =ScheduleConverter.class,columnType = String.class)
    Schedule schedule;
    
    @Generated(hash = 926881078)
    public PlayHistoryBean(long soundId, long groupId, String kind, int percent, long datatime, Track track,
            Schedule schedule) {
        this.soundId = soundId;
        this.groupId = groupId;
        this.kind = kind;
        this.percent = percent;
        this.datatime = datatime;
        this.track = track;
        this.schedule = schedule;
    }

    public PlayHistoryBean(long soundId, long groupId, String kind, int percent, long datatime, Track track) {
        this.soundId = soundId;
        this.groupId = groupId;
        this.kind = kind;
        this.percent = percent;
        this.datatime = datatime;
        this.track = track;
    }

    public PlayHistoryBean(long soundId,long groupId,String kind, long datatime, Schedule schedule) {
        this.soundId = soundId;
        this.kind = kind;
        this.datatime = datatime;
        this.groupId = groupId;
        this.schedule = schedule;
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
    public static class ScheduleConverter implements PropertyConverter<Schedule, String> {
        @Override
        public Schedule convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            return new Gson().fromJson(databaseValue, Schedule.class);
        }

        @Override
        public String convertToDatabaseValue(Schedule entityProperty) {
            if (entityProperty == null) {
                return null;
            }
            return new Gson().toJson(entityProperty);
        }
    }

    @Override
    public String toString() {
        return "PlayHistoryBean{" +
                "soundId=" + soundId +
                ", groupId=" + groupId +
                ", kind='" + kind + '\'' +
                ", percent=" + percent +
                ", datatime=" + datatime +
                ", track=" + track +
                ", schedule=" + schedule +
                '}';
    }

    public Schedule getSchedule() {
        return this.schedule;
    }
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public long getGroupId() {
        return this.groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
