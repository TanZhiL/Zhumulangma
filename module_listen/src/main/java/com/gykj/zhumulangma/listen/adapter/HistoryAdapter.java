package com.gykj.zhumulangma.listen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.SectionEntity;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.HistoryViewModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class HistoryAdapter extends BaseSectionQuickAdapter<HistoryViewModel.PlayHistorySection, BaseViewHolder> {


    public HistoryAdapter(int layoutResId, int sectionHeadResId) {
        super(layoutResId, sectionHeadResId,null);
    }

    @Override
    protected void convert(BaseViewHolder helper, HistoryViewModel.PlayHistorySection item) {
        Track track = item.t.getTrack();
        Glide.with(mContext).load(track.getCoverUrlMiddle()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title, track.getAlbum().getAlbumTitle());
        helper.setText(R.id.tv_album, track.getTrackTitle());
        helper.setText(R.id.tv_duration, ZhumulangmaUtil.secondToTime(track.getDuration()));
        helper.setText(R.id.tv_hasplay, mContext.getString(R.string.hasplay, item.t.getPercent()));
    }

    @Override
    protected void convertHead(BaseViewHolder helper, HistoryViewModel.PlayHistorySection item) {
        helper.setText(R.id.tv_date,item.header);
        helper.setGone(R.id.v_line,helper.getAdapterPosition()!=0);
    }
}
