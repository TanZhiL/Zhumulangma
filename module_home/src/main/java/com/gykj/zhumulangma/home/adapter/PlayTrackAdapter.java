package com.gykj.zhumulangma.home.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.widget.PlayingIconView;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class PlayTrackAdapter extends BaseQuickAdapter<Track, BaseViewHolder> {
    public PlayTrackAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Track item) {

        helper.setText(R.id.tv_title, item.getTrackTitle());
        if (null != XmPlayerManager.getInstance(mContext).getCurrSound()) {
            PlayingIconView lavPlaying = helper.getView(R.id.lav_playing);
            PlayableModel currSound = XmPlayerManager.getInstance(mContext).getCurrSound();
            if (currSound.equals(item)) {
                lavPlaying.setVisibility(View.VISIBLE);
                helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.colorPrimary));
                if (XmPlayerManager.getInstance(mContext).isPlaying()) {
                    lavPlaying.playAnimation();
                } else {
                    lavPlaying.pauseAnimation();
                }
            } else {
                helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.colorPrimaryDark));
                lavPlaying.cancelAnimation();
                lavPlaying.setVisibility(View.GONE);
            }


        }
        switch (XmDownloadManager.getInstance().getSingleTrackDownloadStatus(item.getDataId())) {
            case FINISHED:
                helper.setGone(R.id.iv_downloadsucc, true);
                helper.setGone(R.id.progressBar, false);
                helper.setGone(R.id.iv_download, false);
                helper.setGone(R.id.iv_paid, false);
                break;
            case STARTED:
            case WAITING:
                helper.setGone(R.id.iv_downloadsucc, false);
                helper.setGone(R.id.progressBar, true);
                helper.setGone(R.id.iv_download, false);
                helper.setGone(R.id.iv_paid, false);
                break;
            case STOPPED:
            case NOADD:
            case ERROR:
                helper.setGone(R.id.iv_downloadsucc, false);
                helper.setGone(R.id.progressBar, false);
                helper.setGone(R.id.iv_download, true);
                helper.setGone(R.id.iv_paid, false);
                break;
        }
        if (item.isPaid()) {
            helper.setGone(R.id.iv_downloadsucc, false);
            helper.setGone(R.id.progressBar, false);
            helper.setGone(R.id.iv_download, false);
            helper.setGone(R.id.iv_paid, true);
        }
        helper.addOnClickListener(R.id.iv_download);
    }
}
