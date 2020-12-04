package com.gykj.zhumulangma.home.mvvm.viewmodel;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.gykj.zhumulangma.common.bean.ColumnBean;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.extra.RxField;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseRefreshViewModel;
import com.gykj.zhumulangma.common.net.dto.BannerDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnDTO;
import com.gykj.zhumulangma.common.net.dto.ColumnDetailDTO;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.bean.HomeBean;
import com.gykj.zhumulangma.home.bean.HomeItem;
import com.gykj.zhumulangma.home.bean.NavigationItem;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_CATEGORY_CHILD;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_SIZE_INDEX;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.COLUMN_TITLE_SEPARATOR;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.IS_PAID;
import static com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel.OPERATION_CATEGORY_ID;

public class ChildViewModel extends BaseRefreshViewModel<ZhumulangmaModel, HomeItem> {
    private SingleLiveEvent<List<HomeItem>> mNovelItemsEvent;
    private int mCurPage = 1;
    private final String mBannerCount;

    public ChildViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
        mBannerCount = String.valueOf(3 + new Random().nextInt(5));
    }

    @Override
    public void onViewRefresh() {
        mCurPage = 1;
        init();
    }

    public void init() {
        RxField<List<HomeItem>> rxField = new RxField<>(new ArrayList<>());
        //获取banner
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE_SIZE, mBannerCount);
        map.put(OPERATION_CATEGORY_ID, "6");
        map.put(IS_PAID, "0");
        mModel.getBanners1(map)
                .doOnNext((BannerDTO bannerV2List) -> {
                    HomeBean homeBean = new HomeBean();
                    homeBean.setBannerBeans(bannerV2List.getBanners());
                    rxField.get().add(new HomeItem(HomeItem.BANNER, homeBean));
                    rxField.get().add(new HomeItem(HomeItem.NAVIGATION, getNavigation()));
                    rxField.get().add(new HomeItem(HomeItem.LINE, null));
                })
                .flatMap((Function<BannerDTO, ObservableSource<List<Pair<ColumnBean, ColumnDetailDTO<Album>>>>>)
                        bannerDTO -> getColumnObservable())
                .doFinally(super::onViewRefresh)
                .map(getMapper())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairs -> {
                    if (!CollectionUtils.isEmpty(pairs)) {
                        mCurPage++;
                    }
                    rxField.get().addAll(pairs);
                    getNovelItemsEvent().setValue(rxField.get());
                    getClearStatusEvent().call();
                }, e ->
                {
                    getShowErrorViewEvent().call();
                    e.printStackTrace();
                });
    }

    private HomeBean getNavigation() {
        HomeBean homeBean = new HomeBean();
        List<NavigationItem> navigationItems = new ArrayList<>();
        navigationItems.add(new NavigationItem("排行榜", "", 0xffd5a6bd, R.drawable.ic_home_fine_cxb));
        navigationItems.add(new NavigationItem("故事", "故事", 0xffa2c4c9, R.drawable.ic_home_fine_xpb));
        navigationItems.add(new NavigationItem("哄睡", "哄睡", 0xfff9cb9c, R.drawable.ic_home_fine_dfhy));
        navigationItems.add(new NavigationItem("儿歌", "儿歌", 0xffb6d7a8, R.drawable.ic_home_fine_yss));
        navigationItems.add(new NavigationItem("动画", "动画", 0xffa4c2f4, R.drawable.ic_home_fine_sx));
        navigationItems.add(new NavigationItem("学科", "趣学科学", 0xffffe599, R.drawable.ic_home_fine_yg));
        homeBean.setNavigationItems(navigationItems);
        return homeBean;
    }

    private Observable<List<Pair<ColumnBean, ColumnDetailDTO<Album>>>> getColumnObservable(){

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(2));
        map.put(OPERATION_CATEGORY_ID, COLUMN_CATEGORY_CHILD);
        map.put(DTransferConstants.CONTENT_TYPE, "1");
        map.put(DTransferConstants.PAGE, String.valueOf(mCurPage));

        return  mModel.getColumns1(map)
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
                String pageSize = getPageSize(pair.first);
                int type = HomeItem.ALBUM_3;
                switch (pageSize) {
                    case "3":
                        type = HomeItem.ALBUM_3;
                        break;
                    case "5":
                        type = HomeItem.ALBUM_5;
                        break;
                    case "6":
                        type = HomeItem.ALBUM_6;
                        break;
                }
                ovelItenms.add(new HomeItem(type, homeBean));
                ovelItenms.add(new HomeItem(HomeItem.LINE, homeBean));
            }
            return ovelItenms;
        };
    }

    private Observable<Pair<ColumnBean,ColumnDetailDTO<Album>>> getColumnDetail(ColumnBean columnBean) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ID, String.valueOf(columnBean.getId()));
        String pageSize = getPageSize(columnBean);
        map.put(DTransferConstants.PAGE_SIZE,pageSize);
        map.put(DTransferConstants.PAGE, String.valueOf(1));
        return mModel.getBrowseAlbumColumn1(map).map(albumColumnDetailDTO ->
                new Pair<>(columnBean,albumColumnDetailDTO));

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
        getMoreColumns();
    }

    public SingleLiveEvent<List<HomeItem>> getNovelItemsEvent() {
        return mNovelItemsEvent = createLiveData(mNovelItemsEvent);
    }
}
