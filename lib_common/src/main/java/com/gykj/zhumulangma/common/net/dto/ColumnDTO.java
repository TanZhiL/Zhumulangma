package com.gykj.zhumulangma.common.net.dto;

import com.google.gson.annotations.SerializedName;
import com.gykj.zhumulangma.common.bean.ColumnBean;

import java.util.List;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/20 19:51<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class ColumnDTO {

    /**
     * total_page : 1842
     * total_count : 9206
     * current_page : 1
     * banners : [{"id":34062,"banner_title":"","banner_cover_url":"http://imagev2.xmcdn.com/group86/M02/BD/59/wKg5IF7sZEOwlW8pAAJAZKmyIt0847.jpg","short_title":"","banner_content_type":2,"is_paid":0,"operation_category_id":-1,"banner_content_id":32810412,"banner_content_title":"日诵国学|每天1分钟 轻松读国学","redirect_url":"","created_at":1593198993000,"updated_at":1605812395000,"kind":"banner"},{"id":34121,"banner_title":"","banner_cover_url":"http://imagev2.xmcdn.com/group87/M05/28/B5/wKg5IV74BNyS5-39AAIfDwmj8qY624.jpg","short_title":"","banner_content_type":2,"is_paid":0,"operation_category_id":-1,"banner_content_id":24911060,"banner_content_title":"改变中国历史的二十场大战","redirect_url":"","created_at":1593447393000,"updated_at":1605812395000,"kind":"banner"},{"id":34191,"banner_title":"","banner_cover_url":"http://imagev2.xmcdn.com/group86/M08/2C/47/wKg5IF74QUuDcKmKAAI7PjqGczw189.jpg","short_title":"","banner_content_type":2,"is_paid":0,"operation_category_id":-1,"banner_content_id":249689,"banner_content_title":"听一首歌·念一个人·忆一段情","redirect_url":"","created_at":1593620193000,"updated_at":1605812395000,"kind":"banner"},{"id":34238,"banner_title":"","banner_cover_url":"http://imagev2.xmcdn.com/group84/M05/16/A0/wKg5JF711AGwcJUIAAGvxvxU2xE977.jpg","short_title":"","banner_content_type":2,"is_paid":0,"operation_category_id":-1,"banner_content_id":292190,"banner_content_title":"失眠小姐","redirect_url":"","created_at":1593756993000,"updated_at":1605812395000,"kind":"banner"},{"id":34212,"banner_title":"","banner_cover_url":"http://imagev2.xmcdn.com/group85/M00/2B/BC/wKg5JV74PhOAGdCbAAH49F0E5ME592.jpg","short_title":"","banner_content_type":2,"is_paid":0,"operation_category_id":-1,"banner_content_id":290996,"banner_content_title":"经典留声机","redirect_url":"","created_at":1593706593000,"updated_at":1605812395000,"kind":"banner"}]
     */
    @SerializedName("total_page")
    private int totalPage;
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("current_page")
    private int currentPage;
    /**
     * id : 34062
     * banner_title :
     * banner_cover_url : http://imagev2.xmcdn.com/group86/M02/BD/59/wKg5IF7sZEOwlW8pAAJAZKmyIt0847.jpg
     * short_title :
     * banner_content_type : 2
     * is_paid : 0
     * operation_category_id : -1
     * banner_content_id : 32810412
     * banner_content_title : 日诵国学|每天1分钟 轻松读国学
     * redirect_url :
     * created_at : 1593198993000
     * updated_at : 1605812395000
     * kind : banner
     */

    private List<ColumnBean> columns;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<ColumnBean> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnBean> columns) {
        this.columns = columns;
    }
}