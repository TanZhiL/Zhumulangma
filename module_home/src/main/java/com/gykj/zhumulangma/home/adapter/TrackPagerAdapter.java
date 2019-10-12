package com.gykj.zhumulangma.home.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/28 15:55
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class TrackPagerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public TrackPagerAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_page,item);
    }
}
