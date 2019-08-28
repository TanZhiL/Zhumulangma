package com.gykj.zhumulangma.home.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;

/**
 * Author: Thomas.
 * Date: 2019/8/28 15:55
 * Email: 1071931588@qq.com
 * Description:
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
