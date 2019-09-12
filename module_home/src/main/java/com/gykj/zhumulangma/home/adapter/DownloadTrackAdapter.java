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
public class DownloadTrackAdapter extends BaseQuickAdapter<Track, BaseViewHolder> {
    public DownloadTrackAdapter(int layoutResId) {
            super(layoutResId);
    }
    private ZhumulangmaModel model =new ZhumulangmaModel(App.getInstance());

    @Override
    protected void convert(BaseViewHolder helper, Track item) {
        helper.setText(R.id.tv_title,item.getTrackTitle());
        helper.setText(R.id.tv_duration,ZhumulangmaUtil.secondToTime(item.getDuration()));
        helper.setText(R.id.tv_size, ZhumulangmaUtil.byte2FitMemorySize(item.getDownloadedSize()));
        helper.setText(R.id.tv_index,item.getOrderPositionInAlbum()+"");

        model.list(TrackDownloadBean.class,TrackDownloadBeanDao.Properties.TrackId.eq(item.getDataId()))
                .subscribe(trackDownloadBeans -> {
                    if(trackDownloadBeans.size()==0){

                        return;
                    }
                    switch (trackDownloadBeans.get(0).getStatus()){
                        case FINISHED:

                            break;
                        case STARTED:
                        case WAITING:

                            break;
                        case STOPPED:
                        case NOADD:
                        case ERROR:

                            break;
                    }
                },e->e.printStackTrace());

    }
}
