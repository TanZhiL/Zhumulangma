package com.gykj.zhumulangma.home.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class RankFreeAdapter extends BaseQuickAdapter<Album, BaseViewHolder> {
    public RankFreeAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Album item) {
        Glide.with(mContext).load(item.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getPlayCount()));
        helper.setText(R.id.tv_title,item.getAlbumTitle());
        helper.setText(R.id.tv_track_num, String.format(mContext.getResources().getString(R.string.ji),
                item.getIncludeTrackCount()));
        helper.setText(R.id.tv_desc,item.getLastUptrack().getTrackTitle());

        TextView tvIndex= helper.getView(R.id.tv_index);
        if(helper.getLayoutPosition()==0){
            tvIndex.setTextColor(Color.parseColor("#ff0000"));
        }else if(helper.getLayoutPosition()==1){
            tvIndex.setTextColor(Color.parseColor("#ff9900"));
        }else if(helper.getLayoutPosition()==2){
            tvIndex.setTextColor(Color.parseColor("#4a86e8"));
        }else {
            tvIndex.setTextColor(Color.parseColor("#999999"));
        }
        tvIndex.setText(String.valueOf(helper.getLayoutPosition()+1));
    }
}
