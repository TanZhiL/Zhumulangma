package com.gykj.zhumulangma.listen.mvvm.model;

import android.app.Application;

import com.google.gson.Gson;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.db.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class HistoryModel extends ZhumulangmaModel {

    public HistoryModel(Application application) {
        super(application);
    }

    public Observable<List<PlayHistoryBean>> getHistory(int page, int pagesize) {
        /**
         * SELECT
         *     a.*
         *   FROM
         *     PLAY_HISTORY_BEAN a
         *   WHERE
         *     1 > (
         *       SELECT
         *         count(*)
         *       FROM
         *         PLAY_HISTORY_BEAN
         *       WHERE
         *         ALBUM_ID = a.ALBUM_ID
         *       AND DATATIME > a.DATATIME
         *     )
         *   ORDER BY
         *     a.DATATIME desc
         */
        String sql = "SELECT a.* FROM " + PlayHistoryBeanDao.TABLENAME +
                " a WHERE 1>( SELECT COUNT(*) FROM " + PlayHistoryBeanDao.TABLENAME
                + " WHERE " + PlayHistoryBeanDao.Properties.GroupId.columnName + " = a." + PlayHistoryBeanDao.Properties.GroupId.columnName
                + " AND " + PlayHistoryBeanDao.Properties.Datatime.columnName + " > a." + PlayHistoryBeanDao.Properties.Datatime.columnName
                + ")  ORDER BY a." + PlayHistoryBeanDao.Properties.Datatime.columnName +
                " DESC LIMIT " + pagesize + " OFFSET " + ((page - 1) * pagesize);
        return rawQuery(sql, null)
                .map(c -> {
                    List<PlayHistoryBean> list = new ArrayList<>();
                    if (c.moveToFirst()) {
                        do {
                            list.add(new PlayHistoryBean(
                                    c.getLong(c.getColumnIndex(PlayHistoryBeanDao.Properties.SoundId.columnName)),
                                    c.getLong(c.getColumnIndex(PlayHistoryBeanDao.Properties.GroupId.columnName)),
                                    c.getString(c.getColumnIndex(PlayHistoryBeanDao.Properties.Kind.columnName)),
                                    c.getInt(c.getColumnIndex(PlayHistoryBeanDao.Properties.Percent.columnName)),
                                    c.getLong(c.getColumnIndex(PlayHistoryBeanDao.Properties.Datatime.columnName)),
                                    new Gson().fromJson(c.getString(c.getColumnIndex(PlayHistoryBeanDao.Properties.Track.columnName)), Track.class),
                                    new Gson().fromJson(c.getString(c.getColumnIndex(PlayHistoryBeanDao.Properties.Schedule.columnName)), Schedule.class)
                            ));
                        } while (c.moveToNext());
                    }
                    c.close();
                    return list;
                });
    }
}
