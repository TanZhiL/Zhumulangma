package com.gykj.zhumulangma.home.adapter;

import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.bean.TrackDownloadBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.dao.TrackDownloadBeanDao;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.text.SimpleDateFormat;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class AlbumTrackAdapter extends BaseQuickAdapter<Track, BaseViewHolder> {
    public AlbumTrackAdapter(int layoutResId) {
            super(layoutResId);
    }
    private ZhumulangmaModel model =new ZhumulangmaModel(App.getInstance());

    @Override
    protected void convert(BaseViewHolder helper, Track item) {

        Glide.with(mContext).load(item.getCoverUrlSmall()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getTrackTitle());
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getPlayCount()));
        helper.setText(R.id.tv_duration,ZhumulangmaUtil.secondToTime(item.getDuration()));
        helper.setText(R.id.tv_create_time, TimeUtils.millis2String(item.getCreatedAt(),new SimpleDateFormat("yyyy-MM-dd")));
        if(null!=XmPlayerManager.getInstance(mContext).getCurrSound()){
            LottieAnimationView lavPlaying=helper.getView(R.id.lav_playing);
            PlayableModel currSound = XmPlayerManager.getInstance(mContext).getCurrSound();
            if(currSound.equals(item)){
                lavPlaying.setVisibility(View.VISIBLE);
                if(XmPlayerManager.getInstance(mContext).isPlaying()){
                    lavPlaying.playAnimation();
                }else {
                    lavPlaying.pauseAnimation();
                }
            }else {
                lavPlaying.cancelAnimation();
                lavPlaying.setVisibility(View.GONE);
            }


        }
        model.list(TrackDownloadBean.class,TrackDownloadBeanDao.Properties.TrackId.eq(item.getDataId()))
                .subscribe(trackDownloadBeans -> {
                    if(trackDownloadBeans.size()==0){
                        helper.setGone(R.id.iv_downloadsucc,false);
                        helper.setGone(R.id.progressBar,false);
                        helper.setGone(R.id.iv_download,true);
                        return;
                    }
                    switch (trackDownloadBeans.get(0).getStatus()){
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
                },e->e.printStackTrace());
        model.list(PlayHistoryBean.class, PlayHistoryBeanDao.Properties.SoundId.eq(item.getDataId()),
                PlayHistoryBeanDao.Properties.Kind.eq(item.getKind()))
                .subscribe(playHistoryBeans -> {
                    if(playHistoryBeans.size()==0){
                        helper.setText(R.id.tv_hasplay,"");
                    return;
                }
                  helper.setText(R.id.tv_hasplay,mContext.getString(R.string.hasplay,playHistoryBeans.get(0).getPercent()));
                },e->e.printStackTrace());
        helper.addOnClickListener(R.id.iv_download);
    }
}
