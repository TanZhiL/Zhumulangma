package com.gykj.zhumulangma.listen.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.sdkdownloader.model.XmDownloadAlbum;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class DownloadAlbumAdapter extends BaseQuickAdapter<XmDownloadAlbum, BaseViewHolder> {
    public DownloadAlbumAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, XmDownloadAlbum item) {
        Glide.with(mContext).load(item.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title,item.getAlbumTitle());
        helper.setText(R.id.tv_track_num, String.format(mContext.getResources().getString(R.string.ji),
                item.getTrackCount()));

    }
}
