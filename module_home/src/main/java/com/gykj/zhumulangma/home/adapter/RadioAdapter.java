package com.gykj.zhumulangma.home.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import java.util.List;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class RadioAdapter extends BaseQuickAdapter<Radio, BaseViewHolder> {
    public RadioAdapter(int layoutResId ){
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Radio item) {
        Glide.with(mContext).load(item.getCoverUrlLarge()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getRadioPlayCount()));
        helper.setText(R.id.tv_desc, String.format(mContext.getResources().getString(R.string.playing),item.getProgramName()));
        helper.setText(R.id.tv_title,item.getRadioName());
    }
}
