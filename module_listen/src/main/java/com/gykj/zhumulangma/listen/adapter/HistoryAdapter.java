package com.gykj.zhumulangma.listen.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class HistoryAdapter extends BaseQuickAdapter<PlayHistoryBean, BaseViewHolder> {
    public HistoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayHistoryBean item) {
        Track track = item.getTrack();
        Glide.with(mContext).load(track.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title, track.getTrackTitle());
        helper.setText(R.id.tv_album, track.getAlbum().getAlbumTitle());
        helper.setText(R.id.tv_duration, ZhumulangmaUtil.secondToTime(track.getDuration()));
        helper.setText(R.id.tv_hasplay, mContext.getString(R.string.hasplay, item.getPercent()));

    }
}
