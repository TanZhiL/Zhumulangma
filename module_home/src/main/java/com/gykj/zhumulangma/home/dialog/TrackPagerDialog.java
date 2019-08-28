package com.gykj.zhumulangma.home.dialog;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.TrackPagerAdapter;

import razerdp.basepopup.BasePopupWindow;

public class TrackPagerDialog extends BasePopupWindow {

    private RecyclerView rvPager;
    private TrackPagerAdapter mPagerAdapter;
    private Context mContext;

    public TrackPagerDialog(Context context) {
        super(context);
        mContext=context;
    }

    @Override
    public View onCreateContentView() {
        View view = createPopupById(R.layout.home_dialog_track_pager);
        rvPager=view.findViewById(R.id.rv_pager);
        rvPager.setHasFixedSize(true);
        rvPager.setLayoutManager(new GridLayoutManager(mContext,4));
        mPagerAdapter=new TrackPagerAdapter(R.layout.home_item_pager);
        mPagerAdapter.bindToRecyclerView(rvPager);
        setBackground(Color.TRANSPARENT);
        return view;
    }
/*

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }
*/

    public TrackPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }
}
