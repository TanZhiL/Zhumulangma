package com.gykj.zhumulangma.home.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.List;

/**
 * Created by 10719
 * on 2019/6/12
 */
public class AnnouncerAdapter extends BaseQuickAdapter<Announcer, BaseViewHolder> {


    public AnnouncerAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Announcer item) {
        Glide.with(mContext).load(item.getAvatarUrl()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_name,item.getNickname());
        helper.setText(R.id.tv_vsignature,item.getVsignature());
        helper.setText(R.id.tv_fans, ZhumulangmaUtil.toWanYi(item.getFollowerCount()));
        helper.setText(R.id.tv_following,ZhumulangmaUtil.toWanYi(item.getFollowingCount()));
        helper.setGone(R.id.tv_vip,item.isVerified());
    }
}
