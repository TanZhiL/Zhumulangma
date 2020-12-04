package com.gykj.zhumulangma.common.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/20 19:53<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class BannerBean {


    /**
     * id : 34212
     * banner_title :
     * banner_cover_url : http://imagev2.xmcdn.com/group85/M00/2B/BC/wKg5JV74PhOAGdCbAAH49F0E5ME592.jpg
     * short_title :
     * banner_content_type : 2
     * is_paid : 0
     * operation_category_id : -1
     * banner_content_id : 290996
     * banner_content_title : 经典留声机
     * redirect_url :
     * created_at : 1593706593000
     * updated_at : 1605812395000
     * kind : banner
     */

    private int id;
    @SerializedName("banner_title")
    private String bannerTitle;
    @SerializedName("banner_cover_url")
    private String bannerCoverUrl;
    @SerializedName("short_title")
    private String shortTitle;
    @SerializedName("banner_content_type")
    private int bannerContentType;
    @SerializedName("is_paid")
    private int isPaid;
    @SerializedName("operation_category_id")
    private int operationCategoryId;
    @SerializedName("banner_content_id")
    private int bannerContentId;
    @SerializedName("banner_content_title")
    private String bannerContentTitle;
    @SerializedName("redirect_url")
    private String redirectUrl;
    @SerializedName("created_at")
    private long createdAt;
    @SerializedName("updated_at")
    private long updatedAt;

    private String kind;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.bannerTitle = bannerTitle;
    }

    public String getBannerCoverUrl() {
        return bannerCoverUrl;
    }

    public void setBannerCoverUrl(String bannerCoverUrl) {
        this.bannerCoverUrl = bannerCoverUrl;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public int getBannerContentType() {
        return bannerContentType;
    }

    public void setBannerContentType(int bannerContentType) {
        this.bannerContentType = bannerContentType;
    }

    public int getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(int isPaid) {
        this.isPaid = isPaid;
    }

    public int getOperationCategoryId() {
        return operationCategoryId;
    }

    public void setOperationCategoryId(int operationCategoryId) {
        this.operationCategoryId = operationCategoryId;
    }

    public int getBannerContentId() {
        return bannerContentId;
    }

    public void setBannerContentId(int bannerContentId) {
        this.bannerContentId = bannerContentId;
    }

    public String getBannerContentTitle() {
        return bannerContentTitle;
    }

    public void setBannerContentTitle(String bannerContentTitle) {
        this.bannerContentTitle = bannerContentTitle;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BannerBean that = (BannerBean) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BannerBean{" +
                "id=" + id +
                ", bannerTitle='" + bannerTitle + '\'' +
                '}';
    }
}