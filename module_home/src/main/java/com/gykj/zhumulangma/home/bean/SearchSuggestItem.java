package com.gykj.zhumulangma.home.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ximalaya.ting.android.opensdk.model.word.AlbumResult;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 14:09
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */

public class SearchSuggestItem implements MultiItemEntity {
    public static final int ALBUM = 0;
    public static final int QUERY = 1;
    public int itemType;
    public AlbumResult mAlbumResult;
    public QueryResult mQueryResult;

    public SearchSuggestItem(AlbumResult albumResult) {
        this.itemType = ALBUM;
        mAlbumResult = albumResult;
    }
    public SearchSuggestItem(QueryResult queryResult) {
        this.itemType = QUERY;
        mQueryResult = queryResult;
    }
    @Override
    public int getItemType() {
        return itemType;
    }

}