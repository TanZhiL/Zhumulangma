package com.gykj.zhumulangma.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.bean.ColumnBean;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.extra.GlideImageLoader;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.common.widget.ItemHeader;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.activity.AlbumListActivity;
import com.gykj.zhumulangma.home.bean.HomeItem;
import com.gykj.zhumulangma.home.bean.NavigationItem;
import com.gykj.zhumulangma.home.bean.TabBean;
import com.gykj.zhumulangma.home.widget.RadioCategoryItem;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_ALBUM_TYPE_INDEX;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_TITLE_INDEX;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_TITLE_SEPARATOR;

/**
 * Created by 10719
 * on 2019/6/17
 */
public class ColumnAdapter extends BaseMultiItemQuickAdapter<HomeItem, BaseViewHolder> {
    private static final String TAG = "ColumnAdapter";
    public static final RecyclerView.RecycledViewPool RECYCLEDVIEWPOOL = new RecyclerView.RecycledViewPool();
    private TabBean mTabBean;

    public ColumnAdapter(List<HomeItem> data) {
        super(data);
    }

    public ColumnAdapter(TabBean tabBean, List<HomeItem> data, Context context) {
        super(data);
        mContext = context;
        mTabBean = tabBean;
        addItemType(HomeItem.BANNER, R.layout.home_item_banner);
        addItemType(HomeItem.NAVIGATION_LIST, R.layout.common_layout_nest_list);
        addItemType(HomeItem.NAVIGATION_GRID, R.layout.home_item_navigation_grid);
        addItemType(HomeItem.ALBUM_GRID, R.layout.home_item_album_6);
        addItemType(HomeItem.ALBUM_LIST, R.layout.home_item_album_5);
        addItemType(HomeItem.REFRESH, R.layout.home_layout_item_refresh);
        addItemType(HomeItem.CATEGOTY, R.layout.home_fragment_category);
        addItemType(HomeItem.LINE, R.layout.home_item_line);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeItem item) {

        switch (helper.getItemViewType()) {
            case HomeItem.BANNER:
                onBindBanner(helper, item);
                break;
            case HomeItem.NAVIGATION_LIST:
                onBindNavigationList(helper.getView(R.id.recyclerview), item);
                break;
            case HomeItem.NAVIGATION_GRID:
                onBindNavigationGrid(helper, item);
                break;
            case HomeItem.ALBUM_GRID:
                onBindAlbum3_6(helper, item);
                break;
            case HomeItem.ALBUM_LIST:
                onBindAlbum5(helper, item);
                break;
        }
    }


    private void onBindBanner(BaseViewHolder helper, HomeItem item) {
        Banner banner = helper.getView(R.id.banner);
        if (!banner.getImageUrls().equals(item.getData().getBannerBeans())) {
            banner.setOffscreenPageLimit(item.getData().getBannerBeans().size());
            banner.update(item.getData().getBannerBeans());
        }
    }

    private void onBindNavigationList(RecyclerView rvNavitioin, HomeItem item) {
        if (rvNavitioin.getAdapter() == null) {
            List<NavigationItem> navigationItems = item.getData().getNavigationItems();
            NavigationAdapter navigationAdapter = new NavigationAdapter(R.layout.home_item_navigation);
            navigationAdapter.setNewData(item.getData().getNavigationItems());
            rvNavitioin.setAdapter(navigationAdapter);
            rvNavitioin.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
            rvNavitioin.setHasFixedSize(true);
            OverScrollDecoratorHelper.setUpOverScroll(rvNavitioin, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
            navigationAdapter.bindToRecyclerView(rvNavitioin);
            navigationAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (!TextUtils.isEmpty(mTabBean.getNavType()) && mTabBean.getNavType().equals("nav_cat")) {
                    if (TextUtils.isEmpty(navigationItems.get(position).getValue())) {
                        RouteHelper.navigateTo(Constants.Router.Home.F_RANK);
                    } else {
                        RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                                .withInt(KeyCode.Home.CATEGORY, Integer.parseInt(navigationItems.get(position).getValue()))
                                .withString(KeyCode.Home.TITLE, navigationItems.get(position).getLabel()));
                    }
                } else {
                    RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                            .withInt(KeyCode.Home.CATEGORY, item.getData().getNavCategory())
                            .withString(KeyCode.Home.TAG, navigationItems.get(position).getValue())
                            .withString(KeyCode.Home.TITLE, navigationItems.get(position).getLabel()));
                }
            });
        }
    }


    private void onBindNavigationGrid(BaseViewHolder helper, HomeItem item) {
        for (int i = 1; i <= 15; i++) {
            RadioCategoryItem categoryItem = helper.getView(ResourceUtils.getIdByName("nav_" + i));
            if (i <= item.getData().getNavigationItems().size()) {
                NavigationItem navigationItem = item.getData().getNavigationItems().get(i - 1);
                categoryItem.setCategory(item.getData().getNavCategory());
                categoryItem.setText(navigationItem.getLabel());
                categoryItem.setTag(navigationItem.getValue());
                categoryItem.setVisibility(View.VISIBLE);
            } else {
                categoryItem.setVisibility(View.GONE);
            }
        }
    }

    private void onBindAlbum3_6(BaseViewHolder helper, HomeItem item) {
        ItemHeader itemHeader = (ItemHeader) helper.getView(R.id.ih_title);
        List<Album> albums;
        itemHeader.setTag(item);
        boolean isGuessLike = !CollectionUtils.isEmpty(item.getData().getGussLikeAlbumList());
        if (isGuessLike) {
            albums = item.getData().getGussLikeAlbumList();
            itemHeader.setTitle("猜你喜欢");
        } else {
            albums = item.getData().getColumnDetailDTOPair().second.getColumns();
            ColumnBean columnBean = item.getData().getColumnDetailDTOPair().first;
            itemHeader.setTitle(getTiltle(columnBean));
        }
        for (int i = 1; i <= 6; i++) {
            View view = helper.getView(ResourceUtils.getIdByName("layout_album_" + i));
            if (i <= albums.size()) {
                view.setTag(item);
                Album album = albums.get(i - 1);
                Glide.with(mContext).load(album.getCoverUrlMiddle())
                        .into((ImageView) view.findViewById(R.id.iv_cover));
                ((TextView) view.findViewById(R.id.tv_playcount)).setText(ZhumulangmaUtil.toWanYi(album.getPlayCount()));
                ((TextView) view.findViewById(R.id.tv_title)).setText(album.getAlbumTitle());
                view.setVisibility(View.VISIBLE);
                TextView tvDesc = view.findViewById(R.id.tv_desc);
                TextView tvTitle = view.findViewById(R.id.tv_title);
                ViewGroup.LayoutParams layoutParams = tvTitle.getLayoutParams();
                String showDesc = "";
                try {
                    showDesc = item.getData().getColumnDetailDTOPair().first.getTitle().split(COLUMN_TITLE_SEPARATOR)[COLUMN_ALBUM_TYPE_INDEX];
                } catch (Exception ignored) {
                }
                if (showDesc.equalsIgnoreCase("d")) {
                    tvDesc.setVisibility(View.VISIBLE);
                    tvDesc.setText(album.getAlbumIntro());
                    layoutParams.height = SizeUtils.dp2px(23);
                    tvTitle.setMaxLines(1);
                } else {
                    tvDesc.setVisibility(View.GONE);
                    layoutParams.height = SizeUtils.dp2px(43);
                    tvTitle.setMaxLines(2);
                }
            } else {
                view.setTag(null);
                view.setVisibility(View.GONE);
            }
        }
    }

    private void onBindAlbum5(BaseViewHolder helper, HomeItem item) {
        ItemHeader itemHeader = (ItemHeader) helper.getView(R.id.ih_title);
        ColumnBean columnBean = item.getData().getColumnDetailDTOPair().first;
        itemHeader.setTitle(getTiltle(columnBean));
        itemHeader.setTag(item);

        List<Album> albums = item.getData().getColumnDetailDTOPair().second.getColumns();
        for (int i = 1; i <= 5; i++) {
            View view = helper.getView(ResourceUtils.getIdByName("layout_album_" + i));
            if (i <= albums.size()) {
                view.setTag(item);
                Album album = albums.get(i - 1);
                Glide.with(mContext).load(album.getCoverUrlMiddle()).into((ImageView) view.findViewById(R.id.iv_cover));
                ((TextView) view.findViewById(R.id.tv_playcount)).setText(ZhumulangmaUtil.toWanYi(album.getPlayCount()));
                ((TextView) view.findViewById(R.id.tv_title)).setText(album.getAlbumTitle());
                ((TextView) view.findViewById(R.id.tv_track_num)).setText(String.format(mContext.getResources().getString(R.string.ji),
                        album.getIncludeTrackCount()));
                ((TextView) view.findViewById(R.id.tv_desc)).setText(album.getLastUptrack().getTrackTitle());
                view.setVisibility(View.VISIBLE);
            } else {
                view.setTag(null);
                view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: " + viewType);
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = super.onCreateDefViewHolder(parent, viewType);
        switch (viewType) {
            case HomeItem.BANNER:
                Banner banner = baseViewHolder.getView(R.id.banner);
                banner.setIndicatorGravity(BannerConfig.RIGHT);
                banner.setDelayTime(3000);
                banner.setImages(new ArrayList<>()).setImageLoader(new GlideImageLoader());
                banner.setOnBannerListener(getOnBannerListener());
                break;
            case HomeItem.NAVIGATION_GRID:
                baseViewHolder.getView(R.id.iv_less).setOnClickListener(v -> {
                    baseViewHolder.getView(R.id.cl_more).setVisibility(View.GONE);
                    baseViewHolder.getView(R.id.iv_more).setVisibility(View.VISIBLE);
                });
                baseViewHolder.getView(R.id.iv_more).setOnClickListener(v -> {
                    baseViewHolder.getView(R.id.cl_more).setVisibility(View.VISIBLE);
                    baseViewHolder.getView(R.id.iv_more).setVisibility(View.GONE);
                });
                break;
            case HomeItem.ALBUM_LIST:
            case HomeItem.ALBUM_GRID:
                ItemHeader itemHeader = baseViewHolder.getView(R.id.ih_title);
                itemHeader.setOnClickListener(v -> {
                    HomeItem homeItem = (HomeItem) v.getTag();
                    boolean isGuessLike = !CollectionUtils.isEmpty(homeItem.getData().getGussLikeAlbumList());
                    if (isGuessLike) {
                        RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                                .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.LIKE)
                                .withString(KeyCode.Home.TITLE, itemHeader.getTitle()));
                    } else {
                        RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                                .withInt(KeyCode.Home.CATEGORY, AlbumListActivity.COLUMN)
                                .withInt(KeyCode.Home.COLUMN, homeItem.getData().getColumnDetailDTOPair().first.getId())
                                .withString(KeyCode.Home.TITLE, itemHeader.getTitle()));
                    }
                });
                for (int i = 1; i <= 6; i++) {
                    View view = baseViewHolder.getView(ResourceUtils.getIdByName("layout_album_" + i));
                    if (view != null) {
                        int finalI = i - 1;
                        view.setOnClickListener(v -> {
                            HomeItem homeItem = (HomeItem) v.getTag();
                            boolean isGuessLike = !CollectionUtils.isEmpty(homeItem.getData().getGussLikeAlbumList());
                            if (homeItem != null) {
                                Album album = isGuessLike ?
                                        homeItem.getData().getGussLikeAlbumList().get(finalI)
                                        : homeItem.getData().getColumnDetailDTOPair().second.getColumns().get(finalI);
                                RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                                        .withLong(KeyCode.Home.ALBUMID, album.getId()));
                            }
                        });
                    }
                }
                break;
        }
        return baseViewHolder;
    }

    private OnBannerListener getOnBannerListener() {
        return (o, position) -> {
            BannerBean bannerV2 = (BannerBean) o;
            switch (bannerV2.getBannerContentType()) {
                case 2:
                    RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                            .withLong(KeyCode.Home.ALBUMID, bannerV2.getBannerContentId()));
                    break;
                case 1:
                    RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                            .withLong(KeyCode.Home.ANNOUNCER_ID, bannerV2.getBannerContentId()));
                case 4:
                    RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Discover.F_WEB)
                            .withLong(KeyCode.Discover.PATH, bannerV2.getBannerContentId()));
                    break;
            }
        };
    }

    private String getTiltle(ColumnBean columnBean) {
        String title = columnBean.getOperationCategory().getName();
        try {
            title = columnBean.getTitle()
                    .split(COLUMN_TITLE_SEPARATOR)[COLUMN_TITLE_INDEX];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }

}
