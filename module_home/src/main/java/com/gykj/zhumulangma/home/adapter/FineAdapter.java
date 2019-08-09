package com.gykj.zhumulangma.home.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class FineAdapter extends BaseQuickAdapter<Album, BaseViewHolder> {
    public FineAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Album item) {
        Glide.with(mContext).load(item.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getAlbumTitle());
        helper.setText(R.id.tv_score,item.getAlbumScore());
        helper.setText(R.id.tv_unit,item.getPriceTypeInfos().get(0).getPriceUnit());
        String price= String.format("%.2f", item.getPriceTypeInfos().get(0).getPrice());
        String disPric= String.format("%.2f", item.getPriceTypeInfos().get(0).getDiscountedPrice());
        if(price.equals(disPric))
            ((TextView)helper.getView(R.id.tv_price)).setText(price);
        else {
            SpannableString span=new SpannableString(disPric+"/"+price);
            ForegroundColorSpan colorSpan=new ForegroundColorSpan(Color.parseColor("#505050"));
            span.setSpan(colorSpan,span.toString().lastIndexOf("/"),span.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ((TextView)helper.getView(R.id.tv_price)).setText(span);
        }
        helper.getView(R.id.tv_vip).setVisibility(price.equals(disPric)? View.GONE: View.VISIBLE);

    }
}
