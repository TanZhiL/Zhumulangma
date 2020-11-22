package com.gykj.zhumulangma.home.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.bean.ColumnBean;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class RadioAdapter extends BaseQuickAdapter<ColumnBean, BaseViewHolder> {
    public RadioAdapter(int layoutResId ){
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ColumnBean item) {
        Glide.with(mContext).load(item.getCoverUrlLarge()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getContentNum()));
        helper.setText(R.id.tv_desc,item.getOperationCategory().getName());
        helper.setText(R.id.tv_radio_name,item.getTitle());
    }
}
