package com.gykj.zhumulangma.home.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;

/**
 * Created by 10719
 * on 2019/6/25
 */
public class AlbumTagAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public AlbumTagAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView view = helper.getView(R.id.tv_keyword);

        view.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        view.setBackgroundResource(R.drawable.shap_common_tag_first);

        helper.setText(R.id.tv_keyword, item);
    }
}
