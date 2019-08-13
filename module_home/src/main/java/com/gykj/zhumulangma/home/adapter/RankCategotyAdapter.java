package com.gykj.zhumulangma.home.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;

import java.util.List;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class RankCategotyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public RankCategotyAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_label,item);
    }
}
