package com.gykj.zhumulangma.home.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.widget.PlayingIconView;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import java.text.SimpleDateFormat;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class AlbumTrackAdapter extends BaseQuickAdapter<Track, BaseViewHolder> {
    public AlbumTrackAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Track item) {

        Glide.with(mContext).load(item.getCoverUrlSmall()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title, item.getTrackTitle());
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getPlayCount()));
        helper.setText(R.id.tv_duration, ZhumulangmaUtil.secondToTime(item.getDuration()));
        helper.setText(R.id.tv_index, item.getOrderPositionInAlbum() + 1 + "");
        helper.setText(R.id.tv_create_time, TimeUtils.millis2String(item.getCreatedAt(), new SimpleDateFormat("yyyy-MM-dd")));
        //历史播放进度
        if (item.getSource() == 0) {
            helper.setText(R.id.tv_hasplay, "");
        } else {
            helper.setText(R.id.tv_hasplay, mContext.getString(R.string.hasplay, item.getSource()));
        }
        //播放动画
        if (null != XmPlayerManager.getInstance(mContext).getCurrSound()) {
            PlayingIconView lavPlaying = helper.getView(R.id.lav_playing);
            PlayableModel currSound = XmPlayerManager.getInstance(mContext).getCurrSound();
            if (currSound.equals(item)) {
                lavPlaying.setVisibility(View.VISIBLE);
                if (XmPlayerManager.getInstance(mContext).isPlaying()) {
                    lavPlaying.playAnimation();
                } else {
                    lavPlaying.pauseAnimation();
                }
            } else {
                lavPlaying.cancelAnimation();
                lavPlaying.setVisibility(View.GONE);
            }


        }
        //下载状态
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

    private static final String TAG = "AlbumTrackAdapter";
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        return super.onCreateViewHolder(parent, viewType);
    }
}
