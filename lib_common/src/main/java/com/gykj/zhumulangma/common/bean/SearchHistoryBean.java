package com.gykj.zhumulangma.common.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;

/**
 * Created by 10719
 * on 2019/6/25
 */
@Entity
public class SearchHistoryBean {
    @Id(autoincrement = true)
    private
    Long id;
    private String keyword;
    private long datatime;
    @Generated(hash = 1604123910)
    public SearchHistoryBean(Long id, String keyword, long datatime) {
        this.id = id;
        this.keyword = keyword;
        this.datatime = datatime;
    }
    @Generated(hash = 1570282321)
    public SearchHistoryBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getKeyword() {
        return this.keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchHistoryBean that = (SearchHistoryBean) o;
        return Objects.equals(keyword, that.keyword);
    }
    public long getDatatime() {
        return this.datatime;
    }
    public void setDatatime(long datatime) {
        this.datatime = datatime;
    }

}
