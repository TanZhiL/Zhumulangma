package com.gykj.zhumulangma.home.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.ResourceUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.bean.AnnouncerCategoryBean;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10719
 * on 2019/6/12
 */
public class AnnouncerAdapter extends BaseQuickAdapter<Announcer, BaseViewHolder> {


    public AnnouncerAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Announcer item) {
        Glide.with(mContext).load(item.getAvatarUrl()).into((ImageView) helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_name,item.getNickname());
        if(!TextUtils.isEmpty(item.getVsignature())){
            helper.setText(R.id.tv_vsignature,item.getVsignature());
        }else {
            helper.setText(R.id.tv_vsignature,"这个人很懒,什么也没留下...");
        }
        helper.setText(R.id.tv_fans, ZhumulangmaUtil.toWanYi(item.getFollowerCount()));

        List<AnnouncerCategoryBean> categoryBeans = new Gson().fromJson(ResourceUtils.readAssets2String("announcer_category.json"),
                new TypeToken<ArrayList<AnnouncerCategoryBean>>() {
                }.getType());
        AnnouncerCategoryBean categoryBean = new AnnouncerCategoryBean();
        categoryBean.setId(item.getvCategoryId());
        int i = categoryBeans.indexOf(categoryBean);
        if (i != -1) {
            String vcategoryName = categoryBeans.get(i).getVcategoryName();
            helper.setText(R.id.tv_album,vcategoryName);
        }else {
            helper.setText(R.id.tv_album,"未知");
        }
        helper.setGone(R.id.tv_vip,item.isVerified());
    }
}
