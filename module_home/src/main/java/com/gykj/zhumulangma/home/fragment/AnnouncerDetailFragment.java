package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
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
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.widget.ItemHeader;
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
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;
/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:主播详情页
 */
@Route(path = AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
public class AnnouncerDetailFragment extends BaseRefreshMvvmFragment<AnnouncerDetailViewModel,Object> implements
       View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long mAnnouncerId;
    @Autowired(name = KeyCode.Home.ANNOUNCER_NAME)
    public String mAnnouncerName;
    private Announcer mAnnouncer;
    private AlbumAdapter mAlbumAdapter;
    private AnnouncerTrackAdapter mTrackAdapter;

    private CommonTitleBar ctbTrans;
    private CommonTitleBar ctbWhite;
    private NestedScrollView scrollView;
    private ImageView ivParallax;
    private View flParallax;
    private SmartRefreshLayout refreshLayout;
    private ImageView ivWhiteLeft;
    private ImageView ivWhiteRight;
    private ImageView ivTransLeft;
    private ImageView ivTransRight;


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
        scrollView = view.findViewById(R.id.msv);
        ivParallax = view.findViewById(R.id.parallax);
        flParallax = view.findViewById(R.id.fl_parallax);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        initBar();
        RecyclerView rvAlbum = fd(R.id.rv_album);
        RecyclerView rvTrack = fd(R.id.rv_track);
        rvAlbum.setHasFixedSize(true);
        rvTrack.setHasFixedSize(true);
        rvAlbum.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTrack.setLayoutManager(new LinearLayoutManager(mActivity));
        mAlbumAdapter = new AlbumAdapter(R.layout.home_item_album_line);
        mTrackAdapter = new AnnouncerTrackAdapter(R.layout.home_item_announcer_track);
        mAlbumAdapter.bindToRecyclerView(rvAlbum);
        mTrackAdapter.bindToRecyclerView(rvTrack);
    }

    /**
     * 初始化标题栏
     */
    private void initBar() {
        ivTransLeft = ctbTrans.getLeftCustomView().findViewById(R.id.iv_left);
        ivTransRight = ctbTrans.getRightCustomView().findViewById(R.id.iv1_right);

        ivTransLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivTransLeft.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        ivTransLeft.setVisibility(View.VISIBLE);


        ivTransRight.setImageResource(R.drawable.ic_common_more);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivTransRight.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        ivTransRight.setVisibility(View.VISIBLE);

        ivWhiteLeft = ctbWhite.getLeftCustomView().findViewById(R.id.iv_left);
        ivWhiteRight = ctbWhite.getRightCustomView().findViewById(R.id.iv1_right);
        TextView tvTitle = ctbWhite.getCenterCustomView().findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(mAnnouncerName);

        ivWhiteLeft.setImageResource(R.drawable.ic_common_titlebar_back);
        ivWhiteLeft.setVisibility(View.VISIBLE);

        ivWhiteRight.setImageResource(R.drawable.ic_common_more);
        ivWhiteRight.setVisibility(View.VISIBLE);
    }


    @Override
    public void initListener() {
        super.initListener();
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (nestedScrollView, i, scrollY, i2, i3) -> {
            flParallax.setTranslationY(-scrollY);
            ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
            ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
        });
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mViewModel.onViewRefresh();
            }
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent,
                                       int offset, int headerHeight, int maxDragHeight) {
                ctbTrans.setAlpha(1 - (float) offset / ctbTrans.getHeight());
                ivParallax.setScaleX((float) (1 + percent * 0.2));
                ivParallax.setScaleY((float) (1 + percent * 0.2));
                flParallax.setTranslationY(offset);
            }
        });
        ivWhiteLeft.setOnClickListener(this);
        ivWhiteRight.setOnClickListener(this);
        ivTransLeft.setOnClickListener(this);
        ivTransRight.setOnClickListener(this);
        mAlbumAdapter.setOnItemClickListener(this);
        mTrackAdapter.setOnItemClickListener(this);
        fd(R.id.ih_album).setOnClickListener(v -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, AlbumListFragment.ANNOUNCER)
                    .withLong(KeyCode.Home.ANNOUNCER_ID,mAnnouncerId)
                    .withString(KeyCode.Home.TITLE, mAnnouncerName)
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
        fd(R.id.ih_track).setOnClickListener(v -> {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_TRACK_LIST)
                    .withLong(KeyCode.Home.ANNOUNCER_ID,mAnnouncerId)
                    .withString(KeyCode.Home.TITLE, mAnnouncerName)
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_TRACK_LIST, (ISupportFragment) o)));
        });

        fd(R.id.tv_more).setOnClickListener(this);
        fd(R.id.iv_avatar).setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout,null);
    }

    @Override
    public void initData() {
        mViewModel.setAnnouncerId(mAnnouncerId);
        mViewModel.init();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getAnnouncerEvent().observe(this, announcer -> {
            mAnnouncer=announcer;
            Glide.with(mActivity).load(announcer.getAvatarUrl()).into((ImageView) fd(R.id.iv_avatar));
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

        mViewModel.getAlbumListEvent().observe(this, albumList -> {
            if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                fd(R.id.gp_album).setVisibility(View.VISIBLE);
                mAlbumAdapter.setNewData(albumList.getAlbums());
            }
        });
        mViewModel.getTrackListEvent().observe(this, trackList -> {
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
    public void onClick(View v) {
        int id = v.getId();
        if (v == ivWhiteLeft || v == ivTransLeft) {
            pop();
        } else if (id == R.id.tv_more) {
            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_LIST)
                    .withLong(KeyCode.Home.CATEGORY_ID,mAnnouncer.getvCategoryId())
                    .withString(KeyCode.Home.TITLE, ((TextView)fd(R.id.tv_category)).getText().toString())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_LIST, (ISupportFragment) o)));
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
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAlbumAdapter) {
            Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getItem(position).getId())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        } else {
            Track track = mTrackAdapter.getItem(position);
            mViewModel.play(track.getAlbum().getAlbumId(), track.getDataId());
        }
    }
}
