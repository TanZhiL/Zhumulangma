package com.gykj.zhumulangma.home.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Author: Thomas.<br/>
 * Date: 2020/12/5 17:18<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class TabBean implements Parcelable {


    /**
     * cat_id : 1000522
     * cat_name : 热门
     * nav_ids : 35537,35538
     * nav_type : nav_cat
     * show_like : true
     */

    @SerializedName("cat_id")
    private String catId;
    @SerializedName("cat_name")
    private String catName;
    @SerializedName("nav_ids")
    private String navIds;
    @SerializedName("nav_type")
    private String navType;
    @SerializedName("show_like")
    private boolean showLike;

    @SerializedName("is_cat")
    private boolean isCat;
    @SerializedName("nav_cat")
    private String navCatId;


    protected TabBean(Parcel in) {
        catId = in.readString();
        catName = in.readString();
        navIds = in.readString();
        navType = in.readString();
        showLike = in.readByte() != 0;
        isCat = in.readByte() != 0;
        navCatId = in.readString();
    }

    public static final Creator<TabBean> CREATOR = new Creator<TabBean>() {
        @Override
        public TabBean createFromParcel(Parcel in) {
            return new TabBean(in);
        }

        @Override
        public TabBean[] newArray(int size) {
            return new TabBean[size];
        }
    };

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getNavIds() {
        return navIds;
    }

    public void setNavIds(String navIds) {
        this.navIds = navIds;
    }

    public String getNavType() {
        return navType;
    }

    public void setNavType(String navType) {
        this.navType = navType;
    }

    public boolean isShowLike() {
        return showLike;
    }

    public void setShowLike(boolean showLike) {
        this.showLike = showLike;
    }

    public boolean isCat() {
        return isCat;
    }

    public void setCat(boolean cat) {
        isCat = cat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getNavCatId() {
        return navCatId;
    }

    public void setNavCatId(String navCatId) {
        this.navCatId = navCatId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(catId);
        dest.writeString(catName);
        dest.writeString(navIds);
        dest.writeString(navType);
        dest.writeByte((byte) (showLike ? 1 : 0));
        dest.writeByte((byte) (isCat ? 1 : 0));
        dest.writeString(navCatId);
    }
}