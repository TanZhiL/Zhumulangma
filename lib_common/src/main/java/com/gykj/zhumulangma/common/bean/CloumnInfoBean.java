package com.gykj.zhumulangma.common.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/22 12:35<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class CloumnInfoBean {

    /**
     * id : 10031
     * updated_at : 1605609000000
     * created_at : 1605609000000
     * title : 11月上新仙品♥第2波♥  | 《燕云台》抢先听
     * sub_title : 最逗逼有趣的大大，最炙手可热的新书，上新了，小说！
     * intro : 最逗逼有趣的大大，最炙手可热的新书，上新了，小说！
     * foot_note : 34张专辑
     * cover_url_small : http://fdfs.xmcdn.com/storages/1720-audiofreehighqps/20/D5/CMCoOSIDiWbHAAUiVwBnzto0.jpg
     * cover_url_middle :
     * cover_url_large : http://imagev2.xmcdn.com/storages/35d7-audiofreehighqps/1C/E7/CMCoOSUDiWa_AAGQFABnztQK.jpg!op_type=4&upload_type=cover&device_type=ios&name=large_pop
     * content_type : 1
     * released_at : 1605609000000
     * is_hot : true
     * operation_category : {"id":3,"name":"有声书","source":1,"kind":"operation_category"}
     * dimension_tags : []
     * content_num : 19
     * kind : column
     */

    @SerializedName("id")
    private int id;
    @SerializedName("updated_at")
    private long updatedAt;
    @SerializedName("created_at")
    private long createdAt;
    @SerializedName("title")
    private String title;
    @SerializedName("sub_title")
    private String subTitle;
    @SerializedName("intro")
    private String intro;
    @SerializedName("foot_note")
    private String footNote;
    @SerializedName("cover_url_small")
    private String coverUrlSmall;
    @SerializedName("cover_url_middle")
    private String coverUrlMiddle;
    @SerializedName("cover_url_large")
    private String coverUrlLarge;
    @SerializedName("content_type")
    private int contentType;
    @SerializedName("released_at")
    private long releasedAt;
    @SerializedName("is_hot")
    private boolean isHot;
    /**
     * id : 3
     * name : 有声书
     * source : 1
     * kind : operation_category
     */

    @SerializedName("operation_category")
    private OperationCategoryBean operationCategory;
    @SerializedName("content_num")
    private int contentNum;
    @SerializedName("kind")
    private String kind;
    @SerializedName("dimension_tags")
    private List<?> dimensionTags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getFootNote() {
        return footNote;
    }

    public void setFootNote(String footNote) {
        this.footNote = footNote;
    }

    public String getCoverUrlSmall() {
        return coverUrlSmall;
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        this.coverUrlSmall = coverUrlSmall;
    }

    public String getCoverUrlMiddle() {
        return coverUrlMiddle;
    }

    public void setCoverUrlMiddle(String coverUrlMiddle) {
        this.coverUrlMiddle = coverUrlMiddle;
    }

    public String getCoverUrlLarge() {
        return coverUrlLarge;
    }

    public void setCoverUrlLarge(String coverUrlLarge) {
        this.coverUrlLarge = coverUrlLarge;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public long getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(long releasedAt) {
        this.releasedAt = releasedAt;
    }

    public boolean isIsHot() {
        return isHot;
    }

    public void setIsHot(boolean isHot) {
        this.isHot = isHot;
    }

    public OperationCategoryBean getOperationCategory() {
        return operationCategory;
    }

    public void setOperationCategory(OperationCategoryBean operationCategory) {
        this.operationCategory = operationCategory;
    }

    public int getContentNum() {
        return contentNum;
    }

    public void setContentNum(int contentNum) {
        this.contentNum = contentNum;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<?> getDimensionTags() {
        return dimensionTags;
    }

    public void setDimensionTags(List<?> dimensionTags) {
        this.dimensionTags = dimensionTags;
    }

}