package com.gykj.zhumulangma.listen.adapter;

import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.bean.PlayHistoryItem;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class HistoryAdapter extends BaseMultiItemQuickAdapter<PlayHistoryItem, BaseViewHolder> {

    public HistoryAdapter(List<PlayHistoryItem> data) {
        super(data);
        addItemType(PlayHistoryItem.HEADER,R.layout.listen_item_history_header);
        addItemType(PlayHistoryItem.TRACK,R.layout.listen_item_history_track);
        addItemType(PlayHistoryItem.SCHEDULE,R.layout.listen_item_history_schedule);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayHistoryItem item) {
        switch (helper.getItemViewType()) {
            case PlayHistoryItem.HEADER:
                helper.setText(R.id.tv_date,item.header);
                helper.setGone(R.id.v_line,helper.getAdapterPosition()!=0);
                break;
            case PlayHistoryItem.TRACK:
                Track track = item.data.getTrack();
                Glide.with(mContext).load(track.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
                helper.setText(R.id.tv_title, track.getAlbum().getAlbumTitle());
                helper.setText(R.id.tv_album, track.getTrackTitle());
                helper.setText(R.id.tv_duration, ZhumulangmaUtil.secondToTime(track.getDuration()));
                helper.setText(R.id.tv_hasplay, mContext.getString(R.string.hasplay, item.data.getPercent()));
                break;
            case PlayHistoryItem.SCHEDULE:
                Schedule schedule = item.data.getSchedule();
                Glide.with(mContext).load(schedule.getRelatedProgram().getBackPicUrl()).into((ImageView) helper.getView(R.id.iv_cover));
                helper.setText(R.id.tv_title, schedule.getRadioName());
                helper.setText(R.id.tv_album,"上次收听:  "+schedule.getRelatedProgram().getProgramName());
                helper.setText(R.id.tv_duration,"上次收听时间:  "+ TimeUtils.millis2String(item.data.getDatatime(),"MM-dd hh:mm"));
                break;
        }
    }
}
