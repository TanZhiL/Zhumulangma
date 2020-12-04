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
    private List<BannerBean> bannerBeans;
    private List<NavigationItem> navigationItems;
    private String header;
    private Pair<ColumnBean, ColumnDetailDTO<Album>> mColumnDetailDTOPair;


    public List<BannerBean> getBannerBeans() {
        return bannerBeans;
    }

    public void setBannerBeans(List<BannerBean> bannerBeans) {
        this.bannerBeans = bannerBeans;
    }

    public List<NavigationItem> getNavigationItems() {
        return navigationItems;
    }

    public void setNavigationItems(List<NavigationItem> navigationItems) {
        this.navigationItems = navigationItems;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Pair<ColumnBean, ColumnDetailDTO<Album>> getColumnDetailDTOPair() {
        return mColumnDetailDTOPair;
    }

    public void setColumnDetailDTOPair(Pair<ColumnBean, ColumnDetailDTO<Album>> columnDetailDTOPair) {
        this.mColumnDetailDTOPair = columnDetailDTOPair;
    }
}