package com.gykj.zhumulangma.listen.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/10 14:51
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class DownloadSortAdapter extends BaseItemDraggableAdapter<Track, BaseViewHolder> {
    public DownloadSortAdapter(int layoutResId) {
        super(layoutResId,null);
    }
    private ZhumulangmaModel model = new ZhumulangmaModel(App.getInstance());

    @Override
    protected void convert(BaseViewHolder helper, Track item) {
        Glide.with(mContext).load(item.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getTrackTitle());
        helper.setText(R.id.tv_size, ZhumulangmaUtil.byte2FitMemorySize(item.getDownloadedSize()));

        helper.setText(R.id.tv_album,item.getAlbum().getAlbumTitle());
        helper.setText(R.id.tv_duration, ZhumulangmaUtil.secondToTime(item.getDuration()));
        model.list(PlayHistoryBean.class, PlayHistoryBeanDao.Properties.SoundId.eq(item.getDataId()),
                PlayHistoryBeanDao.Properties.Kind.eq(item.getKind()))
                .subscribe(playHistoryBeans ->
                {
                    if (playHistoryBeans.size() == 0) {
                        helper.setText(R.id.tv_hasplay, "");
                        return;
                    }
                    helper.setText(R.id.tv_hasplay, mContext.getString(R.string.hasplay, playHistoryBeans.get(0).getPercent()));
                }, Throwable::printStackTrace);
    }
}
