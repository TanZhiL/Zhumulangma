package com.gykj.zhumulangma.home.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ResourceUtils;
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
    private final static int[] COLORS = new int[]{
            0xffd5a6bd
            ,0xffa2c4c9
            ,0xffb6d7a8
            ,0xfff9cb9c
            ,0xffa4c2f4
            ,0xffffe599
            ,0xffa4c2f4

    };
    public NavigationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, NavigationItem item) {
        Drawable drawable = new DrawableCreator.Builder()
                .setCornersRadius(SizeUtils.dp2px(20))
                .setSolidColor(COLORS[helper.getAdapterPosition()%COLORS.length])
                .build();
        String icon = item.getIcon();
        if(!TextUtils.isEmpty(icon)){
            Glide.with(mContext).load(ResourceUtils.getDrawableIdByName("ic_home_nav_"+icon))
                    .into((ImageView) helper.getView(R.id.iv_icon));
        }
        helper.setText(R.id.tv_label, item.getLabel());
        helper.getView(R.id.fl_container).setBackground(drawable);

    }

    private static final String TAG = "NavigationAdapter";
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        return super.onCreateViewHolder(parent, viewType);
    }
}
