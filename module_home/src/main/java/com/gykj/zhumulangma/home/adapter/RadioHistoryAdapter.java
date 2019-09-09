package com.gykj.zhumulangma.home.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class RadioHistoryAdapter extends BaseQuickAdapter<PlayHistoryBean, BaseViewHolder> {
    public RadioHistoryAdapter(int layoutResId ){
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayHistoryBean item) {
        Glide.with(mContext).load(item.getSchedule().getRelatedProgram().getBackPicUrl()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getSchedule().getRadioPlayCount()));
        helper.setText(R.id.tv_desc,"上次收听: "+item.getSchedule().getRelatedProgram().getProgramName());
        helper.setText(R.id.tv_radio_name,item.getSchedule().getRadioName());
    }
}
