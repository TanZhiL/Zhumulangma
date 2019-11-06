package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.aop.NeedLogin;
import com.gykj.zhumulangma.common.bean.AnnouncerCategoryBean;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.adapter.AnnouncerTrackAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentAnnouncerDetailBinding;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerDetailViewModel;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:主播详情页
 */
@Route(path = Constants.Router.Home.F_ANNOUNCER_DETAIL)
public class AnnouncerDetailFragment extends BaseRefreshMvvmFragment<HomeFragmentAnnouncerDetailBinding, AnnouncerDetailViewModel, Object>
        implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long mAnnouncerId;
    @Autowired(name = KeyCode.Home.ANNOUNCER_NAME)
    public String mAnnouncerName;
    private Announcer mAnnouncer;
    private AlbumAdapter mAlbumAdapter;
    private AnnouncerTrackAdapter mTrackAdapter;

    private ImageView ivWhiteLeft;
    private ImageView ivWhiteRight;
    private ImageView ivTransLeft;
    private ImageView ivTransRight;


    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_announcer_detail;
    }

    @Override
    protected void initView() {
        initBar();
        mBinding.rvAlbum.setHasFixedSize(true);
        mBinding.rvTrack.setHasFixedSize(true);
        mBinding.rvAlbum.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvTrack.setLayoutManager(new LinearLayoutManager(mActivity));
        mAlbumAdapter = new AlbumAdapter(R.layout.home_item_album_line);
        mTrackAdapter = new AnnouncerTrackAdapter(R.layout.home_item_announcer_track);
        mAlbumAdapter.bindToRecyclerView(mBinding.rvAlbum);
        mTrackAdapter.bindToRecyclerView(mBinding.rvTrack);
    }

    /**
     * 初始化标题栏
     */
    private void initBar() {
        ivTransLeft = mBinding.ctbTrans.getLeftCustomView().findViewById(R.id.iv_left);
        ivTransRight = mBinding.ctbTrans.getRightCustomView().findViewById(R.id.iv1_right);

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

        ivWhiteLeft = mBinding.ctbWhite.getLeftCustomView().findViewById(R.id.iv_left);
        ivWhiteRight = mBinding.ctbWhite.getRightCustomView().findViewById(R.id.iv1_right);
        TextView tvTitle = mBinding.ctbWhite.getCenterCustomView().findViewById(R.id.tv_title);
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
        mBinding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (nestedScrollView, i, scrollY, i2, i3) -> {
                    mBinding.flParallax.setTranslationY(-scrollY);
                    mBinding.ctbWhite.setAlpha(ZhumulangmaUtil.visibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
                    mBinding.ctbTrans.setAlpha(ZhumulangmaUtil.unvisibleByScroll(SizeUtils.px2dp(scrollY), 0, 100));
                });
        mBinding.refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mViewModel.onViewRefresh();
            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent,
                                       int offset, int headerHeight, int maxDragHeight) {
                mBinding.ctbTrans.setAlpha(1 - (float) offset / mBinding.ctbTrans.getHeight());
                mBinding.ivParallax.setScaleX((float) (1 + percent * 0.2));
                mBinding.ivParallax.setScaleY((float) (1 + percent * 0.2));
                mBinding.flParallax.setTranslationY(offset);
            }
        });
        ivWhiteLeft.setOnClickListener(this);
        ivWhiteRight.setOnClickListener(this);
        ivTransLeft.setOnClickListener(this);
        ivTransRight.setOnClickListener(this);
        mAlbumAdapter.setOnItemClickListener(this);
        mTrackAdapter.setOnItemClickListener(this);
        mBinding.ihAlbum.setOnClickListener(v ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.TYPE, AlbumListFragment.ANNOUNCER)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, mAnnouncerId)
                        .withString(KeyCode.Home.TITLE, mAnnouncerName)));
        mBinding.ihTrack.setOnClickListener(v ->
                RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_TRACK_LIST)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, mAnnouncerId)
                        .withString(KeyCode.Home.TITLE, mAnnouncerName)));

        mBinding.tvMore.setOnClickListener(this);
        mBinding.ivAvatar.setOnClickListener(this);
        mBinding.tvFollwer.setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, null);
    }

    @Override
    public void initData() {
        mViewModel.setAnnouncerId(mAnnouncerId);
        mViewModel.init();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getAnnouncerEvent().observe(this, announcer -> {
            mAnnouncer = announcer;
            Glide.with(mActivity).load(announcer.getAvatarUrl()).into(mBinding.ivAvatar);
            mBinding.tvNick.setText(announcer.getNickname());
            mBinding.tvFans.setText("关注  " + ZhumulangmaUtil.toWanYi(announcer.getFollowingCount())
                    + "  |  粉丝  " + ZhumulangmaUtil.toWanYi(announcer.getFollowerCount()));
            mBinding.tvVip.setVisibility(announcer.isVerified() ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(announcer.getVsignature())) {
                mBinding.tvVsignature.setText(announcer.getVsignature());
            }
            mBinding.tvCategory.setText(announcer.getAnnouncerPosition());
            if (!TextUtils.isEmpty(announcer.getVdesc())) {
                mBinding.tvIntro.setText(announcer.getVdesc());
            }

            mBinding.ihAlbum.setTitle("专辑(" + announcer.getReleasedAlbumCount() + ")");
            mBinding.ihTrack.setTitle("声音(" + announcer.getReleasedTrackCount() + ")");

            List<AnnouncerCategoryBean> categoryBeans = new Gson().fromJson(ResourceUtils.readAssets2String("announcer_category.json"),
                    new TypeToken<ArrayList<AnnouncerCategoryBean>>() {
                    }.getType());
            AnnouncerCategoryBean categoryBean = new AnnouncerCategoryBean();
            categoryBean.setId(announcer.getvCategoryId());
            int i = categoryBeans.indexOf(categoryBean);
            if (i != -1) {
                String vcategoryName = categoryBeans.get(i).getVcategoryName();
                mBinding.tvCategory.setText(vcategoryName);
            }
        });

        mViewModel.getAlbumListEvent().observe(this, albumList -> {
            if (!CollectionUtils.isEmpty(albumList.getAlbums())) {
                mBinding.gpAlbum.setVisibility(View.VISIBLE);
                mAlbumAdapter.setNewData(albumList.getAlbums());
            }
        });
        mViewModel.getTrackListEvent().observe(this, trackList -> {
            if (!CollectionUtils.isEmpty(trackList.getTracks())) {
                mBinding.gpTrack.setVisibility(View.VISIBLE);
                mTrackAdapter.setNewData(trackList.getTracks());
            }
        });
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == ivWhiteLeft || v == ivTransLeft) {
            pop();
        } else if (id == R.id.tv_more) {
            RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_LIST)
                    .withLong(KeyCode.Home.CATEGORY_ID, mAnnouncer.getvCategoryId())
                    .withString(KeyCode.Home.TITLE, mBinding.tvCategory.getText().toString()));
        } else if (id == R.id.tv_follwer) {
            follwer();
        }
    }

    @NeedLogin
    private void follwer() {
        Log.d(TAG, "follwer() called");
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
            RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getItem(position).getId()));
        } else {
            Track track = mTrackAdapter.getItem(position);
            mViewModel.playTrack(track.getAlbum().getAlbumId(), track.getDataId());
        }
    }
}
