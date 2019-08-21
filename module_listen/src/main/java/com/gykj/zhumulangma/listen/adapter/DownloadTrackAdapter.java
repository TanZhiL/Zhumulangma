package com.gykj.zhumulangma.listen.adapter;

import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.model.XmDownloadAlbum;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class DownloadTrackAdapter extends BaseQuickAdapter<Track, BaseViewHolder> {
    public DownloadTrackAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Track item) {
        Glide.with(mContext).load(item.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getTrackTitle());
        helper.setText(R.id.tv_size, ZhumulangmaUtil.byte2FitMemorySize(item.getDownloadedSize()));

        helper.setText(R.id.tv_album,item.getAlbum().getAlbumTitle());
        helper.setText(R.id.tv_duration, ZhumulangmaUtil.secondToTime(item.getDuration()));
        helper.addOnClickListener(R.id.ll_delete);
    }
}
