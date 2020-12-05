package com.gykj.zhumulangma.home.bean;

import android.util.Pair;

import com.gykj.zhumulangma.common.bean.BannerBean;
import com.gykj.zhumulangma.common.bean.ColumnBean;
import com.gykj.zhumulangma.common.net.dto.ColumnDetailDTO;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/29 14:56<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class HomeBean {
    private List<BannerBean> mBannerBeans;
    private List<NavigationItem> mNavigationItems;
    private int mNavCategory;
    private String mHeader;
    private Pair<ColumnBean, ColumnDetailDTO<Album>> mColumnDetailDTOPair;
    private List<Album> mGussLikeAlbumList;


    public List<BannerBean> getBannerBeans() {
        return mBannerBeans;
    }

    public void setBannerBeans(List<BannerBean> bannerBeans) {
        this.mBannerBeans = bannerBeans;
    }

    public List<NavigationItem> getNavigationItems() {
        return mNavigationItems;
    }

    public void setNavigationItems(List<NavigationItem> navigationItems) {
        this.mNavigationItems = navigationItems;
    }

    public String getHeader() {
        return mHeader;
    }

    public void setHeader(String header) {
        this.mHeader = header;
    }

    public Pair<ColumnBean, ColumnDetailDTO<Album>> getColumnDetailDTOPair() {
        return mColumnDetailDTOPair;
    }

    public int getNavCategory() {
        return mNavCategory;
    }

    public void setNavCategory(int navCategory) {
        this.mNavCategory = navCategory;
    }

    public void setColumnDetailDTOPair(Pair<ColumnBean, ColumnDetailDTO<Album>> columnDetailDTOPair) {
        this.mColumnDetailDTOPair = columnDetailDTOPair;
    }

    public List<Album>  getGussLikeAlbumList() {
        return mGussLikeAlbumList;
    }

    public void setGussLikeAlbumList(List<Album>  gussLikeAlbumList) {
        mGussLikeAlbumList = gussLikeAlbumList;
    }
}