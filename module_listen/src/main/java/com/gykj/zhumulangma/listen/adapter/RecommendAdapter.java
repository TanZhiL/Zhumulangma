package com.gykj.zhumulangma.listen.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.dao.SubscribeBeanDao;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class RecommendAdapter extends BaseQuickAdapter<Album, BaseViewHolder> {

    private ZhumulangmaModel model = new ZhumulangmaModel(App.getInstance());

    public RecommendAdapter(int layoutResId) {
        super(layoutResId);
    }
    @Override
    protected void convert(BaseViewHolder helper, Album item) {
        Glide.with(mContext).load(item.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_playcount, ZhumulangmaUtil.toWanYi(item.getPlayCount()));
        helper.setText(R.id.tv_title,item.getAlbumTitle());
        helper.setText(R.id.tv_track_num, String.format(mContext.getResources().getString(R.string.ji),
                item.getIncludeTrackCount()));
        helper.setText(R.id.tv_desc,item.getLastUptrack().getTrackTitle());

        model.list(SubscribeBean.class, SubscribeBeanDao.Properties.AlbumId.eq(item.getId()))
                .subscribe(subscribeBeans -> {
                        helper.setGone(R.id.ll_subscribe,subscribeBeans.size()==0);
                        helper.setGone(R.id.ll_unsubscribe,subscribeBeans.size()>0);
                }, e->e.printStackTrace());

        helper.addOnClickListener(R.id.ll_subscribe);
        helper.addOnClickListener(R.id.ll_unsubscribe);
    }
}
