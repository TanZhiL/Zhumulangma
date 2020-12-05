package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.bean.ColumnBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.extra.RxField;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.net.dto.BannerDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnDetailDTO;
import com.gykj.zhumulangma.home.bean.HomeBean;
import com.gykj.zhumulangma.home.bean.HomeItem;
import com.gykj.zhumulangma.home.bean.NavigationItem;
import com.gykj.zhumulangma.home.bean.TabBean;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_PAGE_SIZE;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_SIZE_INDEX;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_TITLE_SEPARATOR;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IS_PAID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.OPERATION_CATEGORY_ID;

public class HomeViewModel extends BaseRefreshViewModel<ZhumulangmaModel, HomeItem> {
    private SingleLiveEvent<List<HomeItem>> mHomeItemsEvent;
    private int mCurPage = 1;
    private final String mBannerCount;
    private TabBean mTabBean;

    public HomeViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
        mBannerCount = String.valueOf(3 + new Random().nextInt(5));
    }

    public void initArguments(TabBean tabBean) {
        mTabBean = tabBean;
    }

    @Override
    public void onViewRefresh() {
        if (mTabBean.isCat()) {
            super.onViewRefresh();
            return;
        }
        mCurPage = 1;
        init();
    }

    public void init() {
        if (mTabBean.isCat()) {
            List<HomeItem> homeItems = new ArrayList<>();
            homeItems.add(new HomeItem(HomeItem.CATEGOTY, null));
            getHomeItemsEvent().setValue(homeItems);
            getClearStatusEvent().call();
            return;
        }
        RxField<List<HomeItem>> rxField = new RxField<>(new ArrayList<>());
        RxField<List<NavigationItem>> rxNavs = new RxField<>(new ArrayList<>());
        //获取banner
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE_SIZE, mBannerCount);
        map.put(OPERATION_CATEGORY_ID, mTabBean.getCatId());
        map.put(IS_PAID, "0");
        mModel.getBanners1(map)
                .doOnNext((BannerDTO bannerV2List) -> {
                    HomeBean homeBean = new HomeBean();
                    homeBean.setBannerBeans(bannerV2List.getBanners());
                    rxField.get().add(new HomeItem(HomeItem.BANNER, homeBean));
                })
                .flatMap((Function<BannerDTO, ObservableSource<String>>) bannerDTO -> {
                    String navIds = mTabBean.getNavIds();
                    return Observable.fromIterable(Arrays.asList(navIds.split(",")));
                })
                .flatMap((Function<String, ObservableSource<BannerDTO>>) s -> {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put(DTransferConstants.ID, s);
                    return mModel.getBanners1(map1);
                }).map(bannerDTO -> {
                    String bannerTitle = bannerDTO.getBanners().get(0).getBannerTitle();
                    rxNavs.get().addAll(new Gson().fromJson(bannerTitle, new TypeToken<List<NavigationItem>>() {
                    }.getType()));
                    return bannerDTO;
                }).toList()
                .flatMapObservable((Function<List<BannerDTO>, ObservableSource<List<Album>>>) bannerDTO -> {
                    int navType = getNavType();
                    HomeBean homeBean = new HomeBean();
                    homeBean.setNavigationItems(rxNavs.get());
                    if (!TextUtils.isEmpty(mTabBean.getNavCatId())) {
                        homeBean.setNavCategory(Integer.parseInt(mTabBean.getNavCatId()));
                    }
                    rxField.get().add(new HomeItem(navType, homeBean));
                    rxField.get().add(new HomeItem(HomeItem.LINE, null));
                    if (mTabBean.isShowLike()) {
                        return getGussLikeObservable();
                    } else {
                        return Observable.just(Collections.emptyList());
                    }
                }).flatMap((Function<List<Album>, ObservableSource<List<Pair<ColumnBean, ColumnDetailDTO<Album>>>>>)
                bannerDTO -> {
                    if (!CollectionUtils.isEmpty(bannerDTO)) {
                        HomeBean homeBean = new HomeBean();
                        homeBean.setGussLikeAlbumList(bannerDTO);
                        rxField.get().add(new HomeItem(HomeItem.ALBUM_GRID, homeBean));
                        rxField.get().add(new HomeItem(HomeItem.LINE, null));
                    }
                    return getColumnObservable();
                })
                .doFinally(super::onViewRefresh)
                .map(getMapper())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairs -> {
                    if (!CollectionUtils.isEmpty(pairs)) {
                        mCurPage++;
                    }
                    rxField.get().addAll(pairs);
                    getHomeItemsEvent().setValue(rxField.get());
                    getClearStatusEvent().call();
                }, e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private int getNavType() {
        int navType = HomeItem.NAVIGATION_LIST;
        switch (mTabBean.getNavType()) {
            case "nav_list":
                navType = HomeItem.NAVIGATION_LIST;
                break;
            case "nav_grid":
                navType = HomeItem.NAVIGATION_GRID;
                break;
            case "nav_cat":
                navType = HomeItem.NAVIGATION_CAT;
                break;
        }
        return navType;
    }

    private Observable<List<Album>> getGussLikeObservable() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, "6");
        map.put(DTransferConstants.DEVICE_TYPE, "2");
        return mModel.getGuessLike(map);
    }

    private HomeBean getNavigation() {
        HomeBean homeBean = new HomeBean();
        List<NavigationItem> navigationItems = new ArrayList<>();
        homeBean.setNavigationItems(navigationItems);
        return homeBean;
    }

    private Observable<List<Pair<ColumnBean, ColumnDetailDTO<Album>>>> getColumnObservable() {

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE_SIZE, COLUMN_PAGE_SIZE);
        map.put(OPERATION_CATEGORY_ID, mTabBean.getCatId());
        map.put(DTransferConstants.CONTENT_TYPE, "1");
        map.put(DTransferConstants.PAGE, String.valueOf(mCurPage));

        return mModel.getColumns1(map)
                .flatMap((Function<ColumnDTO, ObservableSource<ColumnBean>>) columnDTO ->
                        Observable.fromIterable(columnDTO.getColumns())).flatMap(
                        (Function<ColumnBean, ObservableSource<Pair<ColumnBean, ColumnDetailDTO<Album>>>>)
                                this::getColumnDetail).toList().toObservable();
    }

    private void getMoreColumns() {
        getColumnObservable()
                .map(getMapper())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairs -> {
                    if (!CollectionUtils.isEmpty(pairs)) {
                        mCurPage++;
                    }
                    getFinishLoadmoreEvent().setValue(pairs);
                }, e -> {
                    getFinishLoadmoreEvent().call();
                    e.printStackTrace();
                });
    }

    private Function<List<Pair<ColumnBean, ColumnDetailDTO<Album>>>, List<HomeItem>> getMapper() {
        return pairs -> {
            List<HomeItem> ovelItenms = new ArrayList<>();
            for (Pair<ColumnBean, ColumnDetailDTO<Album>> pair : pairs) {
                HomeBean homeBean = new HomeBean();
                homeBean.setColumnDetailDTOPair(pair);
                ovelItenms.add(new HomeItem(getType(pair.first), homeBean));
                ovelItenms.add(new HomeItem(HomeItem.LINE, null));
            }
            return ovelItenms;
        };
    }

    private Observable<Pair<ColumnBean, ColumnDetailDTO<Album>>> getColumnDetail(ColumnBean columnBean) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, String.valueOf(columnBean.getId()));
        String pageSize = getPageSize(columnBean);
        map.put(DTransferConstants.PAGE_SIZE, pageSize);
        map.put(DTransferConstants.PAGE, String.valueOf(1));
        return mModel.getBrowseAlbumColumn1(map).map(albumColumnDetailDTO ->
                new Pair<>(columnBean, albumColumnDetailDTO));

    }

    private int getType(ColumnBean columnBean) {
        String pageSize = "3";
        try {
            pageSize = columnBean.getTitle().split(COLUMN_TITLE_SEPARATOR)[COLUMN_SIZE_INDEX];
        } catch (Exception e) {
            e.printStackTrace();
        }
        int type = HomeItem.ALBUM_LIST;
        switch (pageSize) {
            case "3":
            case "6":
                type = HomeItem.ALBUM_GRID;
                break;
            case "5":
                type = HomeItem.ALBUM_LIST;
                break;
        }
        return type;
    }

    private String getPageSize(ColumnBean columnBean) {
        String pageSize = "3";
        try {
            pageSize = columnBean.getTitle().split(COLUMN_TITLE_SEPARATOR)[COLUMN_SIZE_INDEX];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageSize;
    }

    @Override
    public void onViewLoadmore() {
        if (mTabBean.isCat()) {
            super.onViewLoadmore();
            return;
        }
        getMoreColumns();
    }

    public SingleLiveEvent<List<HomeItem>> getHomeItemsEvent() {
        return mHomeItemsEvent = createLiveData(mHomeItemsEvent);
    }
}
