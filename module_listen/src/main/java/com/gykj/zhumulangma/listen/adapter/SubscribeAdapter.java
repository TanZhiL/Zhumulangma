package com.gykj.zhumulangma.listen.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class SubscribeAdapter extends BaseQuickAdapter<SubscribeBean, BaseViewHolder> {
    public SubscribeAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SubscribeBean item) {

        Glide.with(mContext).load(item.getAlbum().getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getAlbum().getPlayCount()));
        helper.setText(R.id.tv_title,item.getAlbum().getAlbumTitle());
        helper.setText(R.id.tv_desc,item.getAlbum().getAlbumIntro());
        helper.addOnClickListener(R.id.iv_more);
        helper.addOnClickListener(R.id.iv_play);

    }
}
