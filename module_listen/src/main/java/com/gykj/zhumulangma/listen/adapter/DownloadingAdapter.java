package com.gykj.zhumulangma.listen.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.widget.CircleProgressBar;
import com.gykj.zhumulangma.listen.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class DownloadingAdapter extends BaseQuickAdapter<Track, BaseViewHolder> {
    public DownloadingAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Track item) {
        Glide.with(mContext).load(item.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getTrackTitle());
        helper.setText(R.id.tv_size, ZhumulangmaUtil.byte2FitMemorySize(item.getDownloadSize()));

        helper.setText(R.id.tv_album,item.getAlbum().getAlbumTitle());
        helper.setText(R.id.tv_duration, ZhumulangmaUtil.secondToTime(item.getDuration()));

        CircleProgressBar progressBar = helper.getView(R.id.cpb_progress);
        progressBar.setProgress((int) (item.getDownloadedSize()*100/item.getDownloadSize()));

        switch (XmDownloadManager.getInstance().getSingleTrackDownloadStatus(item.getDataId())) {
            case STARTED:
                helper.setImageResource(R.id.iv_status, R.drawable.ic_listen_pause)
                        .setTextColor(R.id.tv_status,mContext.getResources().getColor(R.color.colorPrimary))
                        .setText(R.id.tv_status,"下载中");
                progressBar.setSecondColor(mContext.getResources().getColor(R.color.colorPrimary));
            case WAITING:
                helper.setImageResource(R.id.iv_status, R.drawable.ic_listen_waiting)
                        .setTextColor(R.id.tv_status,mContext.getResources().getColor(R.color.colorGray))
                        .setText(R.id.tv_status,"待下载");
                progressBar.setSecondColor(mContext.getResources().getColor(R.color.colorGray));
                break;
            case STOPPED:
                helper.setImageResource(R.id.iv_status, R.drawable.ic_listen_download)
                        .setTextColor(R.id.tv_status,mContext.getResources().getColor(R.color.colorGray))
                        .setText(R.id.tv_status,"已暂停");
                progressBar.setSecondColor(mContext.getResources().getColor(R.color.colorGray));
                break;
        }

        helper.addOnClickListener(R.id.ll_delete);
    }
}
