package com.gykj.zhumulangma.home.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.bean.NavigationItem;
import com.noober.background.drawable.DrawableCreator;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class NavigationAdapter extends BaseQuickAdapter<NavigationItem, BaseViewHolder> {
    public NavigationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, NavigationItem item) {
        Drawable drawable = new DrawableCreator.Builder()
                .setCornersRadius(SizeUtils.dp2px(20))
                .setSolidColor(item.getBgColor())
                .build();
        Glide.with(mContext).load(item.getIcon()).into((ImageView) helper.getView(R.id.iv_icon));
        helper.setText(R.id.tv_label, item.getLabel());
        helper.getView(R.id.fl_container).setBackground(drawable);

    }
}
