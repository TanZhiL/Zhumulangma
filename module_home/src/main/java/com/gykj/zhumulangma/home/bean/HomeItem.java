package com.gykj.zhumulangma.home.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/29 14:46<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:小说
 */
public class HomeItem implements MultiItemEntity {

    public static final int BANNER = 1;
    public static final int NAVIGATION = 2;
    public static final int ALBUM_6 = 4;
    public static final int ALBUM_3 = 7;
    public static final int ALBUM_5 = 8;
    public static final int REFRESH = 5;
    public static final int LINE = 6;

    public int itemType;
    public HomeBean data;

    public HomeItem(int itemType, HomeBean data) {
        this.itemType = itemType;
        this.data = data;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public HomeBean getData() {
        return data;
    }

    public void setData(HomeBean data) {
        this.data = data;
    }
}