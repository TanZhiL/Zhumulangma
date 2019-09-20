package com.gykj.zhumulangma.listen.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.bean.FavoriteBean;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class FavoriteAdapter extends BaseQuickAdapter<FavoriteBean, BaseViewHolder> {
    public FavoriteAdapter(int layoutResId) {
        super(layoutResId);
    }
    private ZhumulangmaModel model = new ZhumulangmaModel(App.getInstance());

    @Override
    protected void convert(BaseViewHolder helper, FavoriteBean item) {
        Glide.with(mContext).load(item.getTrack().getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getTrack().getTrackTitle());

        helper.setText(R.id.tv_album,item.getTrack().getAlbum().getAlbumTitle());
        helper.setText(R.id.tv_duration, ZhumulangmaUtil.secondToTime(item.getTrack().getDuration()));

        helper.addOnClickListener(R.id.ll_delete);
    }
}
