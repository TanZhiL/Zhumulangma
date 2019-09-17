package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.AnnouncerCategoryBean;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.bean.ProvinceBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.widget.ItemHeader;
import com.gykj.zhumulangma.common.widget.TScrollView;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.adapter.AnnouncerTrackAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerDetailViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategoryList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
public class AnnouncerDetailFragment extends BaseMvvmFragment<AnnouncerDetailViewModel> implements
        TScrollView.OnScrollListener, View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long mAnnouncerId;
    @Autowired(name = KeyCode.Home.ANNOUNCER_NAME)
    public String mAnnouncerName;

    private Announcer mAnnouncer;
    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private TScrollView mScrollView;
    private ImageView parallax;
    private View flParallax;
    private SmartRefreshLayout refreshLayout;
    private ImageView whiteLeft;
    private ImageView whiteRight;
    private ImageView transLeft;
    private ImageView transRight;

    private RecyclerView rvAlbum, rvTrack;
    private AlbumAdapter mAlbumAdapter;
    private AnnouncerTrackAdapter mTrackAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_announcer_detail;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        ctbTrans = view.findViewById(R.id.ctb_trans);
        ctbWhite = view.findViewById(R.id.ctb_white);
        mScrollView = view.findViewById(R.id.msv);
        parallax = view.findViewById(R.id.parallax);
        flParallax = view.findViewById(R.id.fl_parallax);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        initBar();
        rvAlbum = fd(R.id.rv_album);
        rvTrack = fd(R.id.rv_track);
        rvAlbum.setHasFixedSize(true);
        rvTrack.setHasFixedSize(true);
        rvAlbum.setLayoutManager(new LinearLayoutManager(mContext));
        rvTrack.setLayoutManager(new LinearLayoutManager(mContext));
        mAlbumAdapter = new AlbumAdapter(R.layout.home_item_album);
        mTrackAdapter = new AnnouncerTrackAdapter(R.layout.home_item_announcer_track);

        mAlbumAdapter.bindToRecyclerView(rvAlbum);
        mTrackAdapter.bindToRecyclerView(rvTrack);
    }

    private void initBar() {

        transLeft = ctbTrans.getLeftCustomView().findViewById(R.id.iv_left);
        transRight = ctbTrans.getRightCustomView().findViewById(R.id.iv1_right);


        transLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transLeft.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transLeft.setVisibility(View.VISIBLE);


        transRight.setImageResource(R.drawable.ic_common_more);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transRight.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        transRight.setVisibility(View.VISIBLE);

        whiteLeft = ctbWhite.getLeftCustomView().findViewById(R.id.iv_left);
        whiteRight = ctbWhite.getRightCustomView().findViewById(R.id.iv1_right);
        TextView tvTitle = ctbWhite.getCenterCustomView().findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(mAnnouncerName);

        whiteLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        whiteLeft.setVisibility(View.VISIBLE);

        whiteRight.setImageResource(R.drawable.ic_common_more);
        whiteRight.setVisibility(View.VISIBLE);

    }


    @Override
    public void initListener() {
        super.initListener();
        mScrollView.setOnScrollListener(this);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                ctbTrans.setAlpha(1 - (float) offset / ctbTrans.getHeight());

                parallax.setScaleX((float) (1 + percent * 0.2));
                parallax.setScaleY((float) (1 + percent * 0.2));

                flParallax.setTranslationY(offset);
            }
        });
        whiteLeft.setOnClickListener(this);
        whiteRight.setOnClickListener(this);
        transLeft.setOnClickListener(this);
        transRight.setOnClickListener(this);

        mAlbumAdapter.setOnItemClickListener(this);
        mTrackAdapter.setOnItemClickListener(this);
        fd(R.id.ih_album).setOnClickListener(v -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, AlbumListFragment.ANNOUNCER)
                    .withLong(KeyCode.Home.ANNOUNCER_ID,mAnnouncerId)
                    .withString(KeyCode.Home.TITLE, mAnnouncerName)
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
        fd(R.id.ih_track).setOnClickListener(v -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_TRACK_LIST)
                    .withLong(KeyCode.Home.ANNOUNCER_ID,mAnnouncerId)
                    .withString(KeyCode.Home.TITLE, mAnnouncerName)
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_TRACK_LIST, (ISupportFragment) o)));
        });

        fd(R.id.tv_more).setOnClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel.getDetail(String.valueOf(mAnnouncerId));

    }

    @Override
    public void initViewObservable() {
        mViewModel.getAnnouncerSingleLiveEvent().observe(this, announcer -> {
            mAnnouncer=announcer;
            Glide.with(mContext).load(announcer.getAvatarUrl()).into((ImageView) fd(R.id.iv_avatar));
            ((TextView) fd(R.id.tv_nick)).setText(announcer.getNickname());
            ((TextView) fd(R.id.tv_fans)).setText("关注  " + ZhumulangmaUtil.toWanYi(announcer.getFollowingCount())
                    + "  |  粉丝  " + ZhumulangmaUtil.toWanYi(announcer.getFollowerCount()));
            fd(R.id.tv_vip).setVisibility(announcer.isVerified() ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(announcer.getVsignature())) {
                ((TextView) fd(R.id.tv_vsignature)).setText(announcer.getVsignature());
            }
            ((TextView) fd(R.id.tv_category)).setText(announcer.getAnnouncerPosition());
            if (!TextUtils.isEmpty(announcer.getVdesc())) {
                ((TextView) fd(R.id.tv_intro)).setText(announcer.getVdesc());
            }

            ((ItemHeader) fd(R.id.ih_album)).setTitle("专辑(" + announcer.getReleasedAlbumCount() + ")");
            ((ItemHeader) fd(R.id.ih_track)).setTitle("声音(" + announcer.getReleasedTrackCount() + ")");

            List<AnnouncerCategoryBean> categoryBeans = new Gson().fromJson(ResourceUtils.readAssets2String("announcer_category.json"),
                    new TypeToken<ArrayList<AnnouncerCategoryBean>>() {
                    }.getType());
            AnnouncerCategoryBean categoryBean = new AnnouncerCategoryBean();
            categoryBean.setId(announcer.getvCategoryId());
            int i = categoryBeans.indexOf(categoryBean);
            if (i != -1) {
                String vcategoryName = categoryBeans.get(i).getVcategoryName();
                ((TextView) fd(R.id.tv_category)).setText(vcategoryName);
            }
        });

        mViewModel.getAlbumListSingleLiveEvent().observe(this, albumList -> {
            if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                fd(R.id.gp_album).setVisibility(View.VISIBLE);
                mAlbumAdapter.setNewData(albumList.getAlbums());
            }
        });
        mViewModel.getTrackListSingleLiveEvent().observe(this, trackList -> {
            if (!CollectionUtils.isEmpty(trackList.getTracks())) {
                fd(R.id.gp_track).setVisibility(View.VISIBLE);
                mTrackAdapter.setNewData(trackList.getTracks());
            }
        });
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onScroll(int scrollY) {
        flParallax.setTranslationY(-scrollY);
        ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
        ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == whiteLeft || v == transLeft) {
            pop();
        } else if (id == R.id.tv_more) {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_LIST)
                    .withLong(KeyCode.Home.CATEGORY_ID,mAnnouncer.getvCategoryId())
                    .withString(KeyCode.Home.TITLE, ((TextView)fd(R.id.tv_category)).getText().toString())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_LIST, (ISupportFragment) o)));
        }

    }

    @Override
    public Class<AnnouncerDetailViewModel> onBindViewModel() {
        return AnnouncerDetailViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAlbumAdapter) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getItem(position).getId())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        } else {
            Track track = mTrackAdapter.getItem(position);
            mViewModel.play(track.getAlbum().getAlbumId(), track.getDataId());
        }
    }
}
