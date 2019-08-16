package com.gykj.zhumulangma.home.adapter;

import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.bean.AlbumTrackBean;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class AlbumTrackAdapter extends BaseQuickAdapter<AlbumTrackBean, BaseViewHolder> {
    public AlbumTrackAdapter(int layoutResId) {
            super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, AlbumTrackBean detailTrackBean) {
        Track item=detailTrackBean.getTrack();
        Glide.with(mContext).load(item.getCoverUrlSmall()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getTrackTitle());
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getPlayCount()));
        helper.setText(R.id.tv_duration,ZhumulangmaUtil.secondToTime(item.getDuration()));
        helper.setText(R.id.tv_create_time, TimeUtils.millis2String(item.getCreatedAt(),new SimpleDateFormat("yyyy-MM")));

        helper.setGone(R.id.iv_playing,detailTrackBean.isPlaying());
        switch (detailTrackBean.getDownloadState()){
            case FINISHED:
                helper.setGone(R.id.iv_downloadsucc,true);
                helper.setGone(R.id.progressBar,false);
                helper.setGone(R.id.iv_download,false);
                break;
            case STARTED:
            case WAITING:
                helper.setGone(R.id.iv_downloadsucc,false);
                helper.setGone(R.id.progressBar,true);
                helper.setGone(R.id.iv_download,false);
                break;
            case STOPPED:
            case NOADD:
            case ERROR:
                helper.setGone(R.id.iv_downloadsucc,false);
                helper.setGone(R.id.progressBar,false);
                helper.setGone(R.id.iv_download,true);
                break;
        }
        helper.addOnClickListener(R.id.iv_download);
    }
}
