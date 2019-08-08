package com.gykj.zhumulangma.main.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.main.R;
import com.gykj.zhumulangma.main.bean.MainFunc;

import java.util.List;

public class MainFuncAdapter extends BaseQuickAdapter<MainFunc, BaseViewHolder> {

    public MainFuncAdapter(int layoutResId, @Nullable List<MainFunc> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainFunc item) {
        helper.setText(R.id.tv_title,item.getTitle());
        helper.setImageResource(R.id.iv_cover,item.getCover());
    }
}
