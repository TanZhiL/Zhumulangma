package com.gykj.zhumulangma.listen.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.adapter.TabNavigatorAdapter;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.SystemUtil;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.DownloadAlbumAdapter;
import com.gykj.zhumulangma.listen.adapter.DownloadTrackAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.DownloadViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.BaseRuntimeException;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;

@Route(path = AppConstants.Router.Listen.F_DOWNLOAD)
public class DownloadFragment extends BaseMvvmFragment<DownloadViewModel> implements
        BaseQuickAdapter.OnItemChildClickListener, IXmDownloadTrackCallBack {
    @Autowired(name = KeyCode.Listen.TAB_INDEX)
    public int tabIndex;
    private TextView tvMemory;
    private ViewPager viewpager;

    private MagicIndicator magicIndicator;
    private String[] tabs = {"专辑", "声音", "下载中"};
    private ViewGroup layoutDetail1, layoutDetail2, layoutDetail3;

    private RecyclerView rvAlbum;
    private RecyclerView rvTrack;
    private RecyclerView rvRecommend;
    private DownloadAlbumAdapter mAlbumAdapter;
    private DownloadTrackAdapter mTrackAdapter;
    private DownloadTrackAdapter mDownloadingAdapter;

    public DownloadFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.listen_fragment_download;
    }

    @Override
    protected void initView(View view) {

        layoutDetail1 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.common_layout_refresh_loadmore, null);
        layoutDetail2 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.common_layout_refresh_loadmore, null);
        layoutDetail3 = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.common_layout_refresh_loadmore, null);
        ((RefreshLayout) layoutDetail1.findViewById(R.id.refreshLayout)).setEnableRefresh(false);
        ((RefreshLayout) layoutDetail1.findViewById(R.id.refreshLayout)).setEnableLoadMore(false);

        ((RefreshLayout) layoutDetail2.findViewById(R.id.refreshLayout)).setEnableRefresh(false);
        ((RefreshLayout) layoutDetail2.findViewById(R.id.refreshLayout)).setEnableLoadMore(false);

        ((RefreshLayout) layoutDetail3.findViewById(R.id.refreshLayout)).setEnableRefresh(false);
        ((RefreshLayout) layoutDetail3.findViewById(R.id.refreshLayout)).setEnableLoadMore(false);


        rvAlbum = layoutDetail1.findViewById(R.id.rv);
        rvAlbum.setHasFixedSize(true);
        rvAlbum.setLayoutManager(new LinearLayoutManager(mContext));
        mAlbumAdapter = new DownloadAlbumAdapter(R.layout.listen_item_download_album);
        mAlbumAdapter.bindToRecyclerView(rvAlbum);

        rvTrack = layoutDetail2.findViewById(R.id.rv);
        rvTrack.setHasFixedSize(true);
        rvTrack.setLayoutManager(new LinearLayoutManager(mContext));
        mTrackAdapter = new DownloadTrackAdapter(R.layout.listen_item_download_track);
        mTrackAdapter.bindToRecyclerView(rvTrack);

        rvRecommend = layoutDetail3.findViewById(R.id.rv);
        rvRecommend.setHasFixedSize(true);
        rvRecommend.setLayoutManager(new LinearLayoutManager(mContext));
        mDownloadingAdapter = new DownloadTrackAdapter(R.layout.listen_item_download_track);
        mDownloadingAdapter.bindToRecyclerView(rvRecommend);

        tvMemory = fd(R.id.tv_memory);
        viewpager = fd(R.id.viewpager);
        viewpager.setAdapter(new DownloadPagerAdapter());
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new TabNavigatorAdapter(Arrays.asList(tabs), viewpager, 50));
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
        viewpager.setCurrentItem(tabIndex);
    }

    @Override
    public void initListener() {
        super.initListener();
        mAlbumAdapter.setOnItemChildClickListener(this);
        mTrackAdapter.setOnItemChildClickListener(this);
        XmDownloadManager.getInstance().addDownloadStatueListener(this);
    }

    @Override
    public void initData() {
        tvMemory.setText(getString(R.string.memory,
                XmDownloadManager.getInstance().getHumanReadableDownloadOccupation(IDownloadManager.Auto),
                SystemUtil.getRomTotalSize(mContext)));

        mAlbumAdapter.setNewData(XmDownloadManager.getInstance().getDownloadAlbums(true));
        mTrackAdapter.setNewData(XmDownloadManager.getInstance().getDownloadTracks(true));
        mDownloadingAdapter.setNewData(XmDownloadManager.getInstance().getDownloadTracks(false));
    }

    @Override
    public void initViewObservable() {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (adapter instanceof DownloadAlbumAdapter) {
            if (id == R.id.ll_delete) {

                XmDownloadManager.getInstance().clearDownloadedAlbum(mAlbumAdapter.getData().get(position).getAlbumId(), null);

            }
        } else {
            if (id == R.id.ll_delete) {
                XmDownloadManager.getInstance().clearDownloadedTrack(mTrackAdapter.getData().get(position).getDataId());
            }
        }
    }

    @Override
    public Class<DownloadViewModel> onBindViewModel() {
        return DownloadViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void onWaiting(Track track) {

    }

    @Override
    public void onStarted(Track track) {

    }

    @Override
    public void onSuccess(Track track) {

    }

    @Override
    public void onError(Track track, Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCancelled(Track track, Callback.CancelledException e) {

    }

    @Override
    public void onProgress(Track track, long l, long l1) {

    }

    @Override
    public void onRemoved() {
        initData();
    }


    class DownloadPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = null;
            switch (position) {
                case 0:
                    view = layoutDetail1;
                    break;
                case 1:
                    view = layoutDetail2;
                    break;
                case 2:
                    view = layoutDetail3;
                    break;
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    protected int onBindBarCenterStyle() {
        return BarStyle.CENTER_CUSTOME;
    }

    @Override
    protected View onBindBarCenterCustome() {
        magicIndicator = new MagicIndicator(mContext);
        magicIndicator.setBackgroundColor(Color.WHITE);
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.addView(magicIndicator);
        ViewGroup.LayoutParams layoutParams = magicIndicator.getLayoutParams();
        layoutParams.width = SizeUtils.dp2px(270);
        magicIndicator.setLayoutParams(layoutParams);

        return frameLayout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XmDownloadManager.getInstance().removeDownloadStatueListener(this);
    }
}
