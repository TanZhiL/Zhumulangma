package com.gykj.zhumulangma.common.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.R;
import com.luck.picture.lib.entity.LocalMedia;

/**
 * Author: Thomas.
 * Date: 2019/7/30 8:39
 * Email: 1071931588@qq.com
 * Description:
 */
public class ImageAdapter extends BaseQuickAdapter<LocalMedia, BaseViewHolder> {
    private boolean mReadOnly;
    public ImageAdapter(int layoutResId) {
        super(layoutResId);
    }

    public ImageAdapter(int layoutResId,boolean readOnly){
        super(layoutResId);
        mReadOnly=readOnly;
    }
    @Override
    protected void convert(BaseViewHolder helper, LocalMedia media) {
        String path =media.getCompressPath();
        Glide.with(mContext).load(path).into((ImageView) helper.getView(R.id.iv_item));
        if(mReadOnly){
            helper.setGone(R.id.iv_delete,true);
        }else {
            helper.addOnClickListener(R.id.iv_delete);
        }
    }
}
