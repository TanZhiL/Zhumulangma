package com.gykj.zhumulangma.common.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/21 19:52<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class ColumnBean {

    /**
     * id : 1653
     * updated_at : 1567764908000
     * created_at : 1567767850000
     * title : 单田芳经典评书top10，一代人听着长大的国民评书
     * cover_url_small : http://fdfs.xmcdn.com/group48/M08/B0/69/wKgKnFuWRHqSztqBAAAuu4gq1WU268.jpg
     * cover_url_middle :
     * cover_url_large : http://imagev2.xmcdn.com/group48/M08/37/4B/wKgKlVuWRG3BoAi5AAICXouzFF0875.jpg!op_type=4&upload_type=cover&device_type=ios&name=large_pop
     * content_type : 1
     * operation_category : {"id":12,"name":"相声评书","kind":"operation_category"}
     * dimension_tags : []
     * content_num : 10
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
    @SerializedName("cover_url_small")
    private String coverUrlSmall;
    @SerializedName("cover_url_middle")
    private String coverUrlMiddle;
    @SerializedName("cover_url_large")
    private String coverUrlLarge;
    @SerializedName("content_type")
    private int contentType;
    /**
     * id : 12
     * name : 相声评书
     * kind : operation_category
     */

    @SerializedName("operation_category")
    private OperationCategoryBean operationCategory;
    @SerializedName("content_num")
    private int contentNum;
    @SerializedName("kind")
    private String kind;
    @SerializedName("dimension_tags")
    private List<String> dimensionTags;

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

    public void setDimensionTags(List<String> dimensionTags) {
        this.dimensionTags = dimensionTags;
    }

    public static class OperationCategoryBean {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("kind")
        private String kind;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }
    }
}