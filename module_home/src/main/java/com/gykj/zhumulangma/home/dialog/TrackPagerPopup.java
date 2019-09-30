package com.gykj.zhumulangma.home.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.TrackPagerAdapter;
import com.lxj.xpopup.impl.PartShadowPopupView;

/**
 * Author: Thomas.
 * Date: 2019/9/30 10:33
 * Email: 1071931588@qq.com
 * Description:专辑声音分页弹窗
 */
public class TrackPagerPopup extends PartShadowPopupView  {

    private TrackPagerAdapter mPagerAdapter;
    private RecyclerView rvPager;

    private BaseQuickAdapter.OnItemClickListener mListener;

    public TrackPagerPopup(@NonNull Context context) {
        super(context);
    }
    public TrackPagerPopup(@NonNull Context context, BaseQuickAdapter.OnItemClickListener listener) {
        super(context);
        mListener=listener;
    }

    @Override
    protected int getImplLayoutId() {
        mPagerAdapter = new TrackPagerAdapter(R.layout.home_item_pager);
        return R.layout.home_dialog_recyclerview;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        rvPager = findViewById(R.id.recyclerview);
        rvPager.setLayoutManager(new GridLayoutManager(getContext(), 4));

        mPagerAdapter.bindToRecyclerView(rvPager);
        mPagerAdapter.setOnItemClickListener(mListener);
    }


    public interface onPopupDismissingListener {

        void onDismissing();
    }
    private onPopupDismissingListener mDismissingListener;
    public void setDismissingListener(onPopupDismissingListener dismissingListener) {
        mDismissingListener = dismissingListener;
    }

    @Override
    protected void doDismissAnimation() {
        super.doDismissAnimation();
        if(mDismissingListener!=null)
            mDismissingListener.onDismissing();
    }

    public TrackPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    public RecyclerView getRvPager() {
        return rvPager;
    }
}
