package com.gykj.zhumulangma.listen.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.gykj.zhumulangma.common.bean.PlayHistoryBean;

/**
 * Author: Thomas.
 * Date: 2019/9/18 14:09
 * Email: 1071931588@qq.com
 * Description:
 */

public class PlayHistoryItem implements MultiItemEntity {
    public static final int HEADER = 1;
    public static final int TRACK = 2;
    public static final int SCHEDULE = 3;
    public int itemType;
    public PlayHistoryBean data;
    public String header;

    public PlayHistoryItem(int itemType,PlayHistoryBean data) {
        this.itemType = itemType;
        this.data=data;
    }
    public PlayHistoryItem(int itemType,String header) {
        this.itemType = itemType;
        this.header=header;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

}