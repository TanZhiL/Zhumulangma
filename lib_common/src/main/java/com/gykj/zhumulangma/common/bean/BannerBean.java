package com.gykj.zhumulangma.common.bean;

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
    private String banner_title;
    private String banner_cover_url;
    private String short_title;
    private int banner_content_type;
    private int is_paid;
    private int operation_category_id;
    private int banner_content_id;
    private String banner_content_title;
    private String redirect_url;
    private long created_at;
    private long updated_at;
    private String kind;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBanner_title() {
        return banner_title;
    }

    public void setBanner_title(String banner_title) {
        this.banner_title = banner_title;
    }

    public String getBanner_cover_url() {
        return banner_cover_url;
    }

    public void setBanner_cover_url(String banner_cover_url) {
        this.banner_cover_url = banner_cover_url;
    }

    public String getShort_title() {
        return short_title;
    }

    public void setShort_title(String short_title) {
        this.short_title = short_title;
    }

    public int getBanner_content_type() {
        return banner_content_type;
    }

    public void setBanner_content_type(int banner_content_type) {
        this.banner_content_type = banner_content_type;
    }

    public int getIs_paid() {
        return is_paid;
    }

    public void setIs_paid(int is_paid) {
        this.is_paid = is_paid;
    }

    public int getOperation_category_id() {
        return operation_category_id;
    }

    public void setOperation_category_id(int operation_category_id) {
        this.operation_category_id = operation_category_id;
    }

    public int getBanner_content_id() {
        return banner_content_id;
    }

    public void setBanner_content_id(int banner_content_id) {
        this.banner_content_id = banner_content_id;
    }

    public String getBanner_content_title() {
        return banner_content_title;
    }

    public void setBanner_content_title(String banner_content_title) {
        this.banner_content_title = banner_content_title;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}