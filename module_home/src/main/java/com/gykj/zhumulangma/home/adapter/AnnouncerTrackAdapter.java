package com.gykj.zhumulangma.home.adapter;

import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class AnnouncerTrackAdapter extends BaseQuickAdapter<Track, BaseViewHolder> {
    public AnnouncerTrackAdapter(int layoutResId) {
            super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Track item) {

        Glide.with(mContext).load(item.getCoverUrlSmall()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getTrackTitle());
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getPlayCount()));
        helper.setText(R.id.tv_duration,ZhumulangmaUtil.secondToTime(item.getDuration()));
        helper.setText(R.id.tv_create_time, TimeUtils.millis2String(item.getCreatedAt(),new SimpleDateFormat("yyyy-MM-dd")));

    }
}
