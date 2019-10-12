package com.gykj.zhumulangma.common.bean;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/9 11:17
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class ProvinceBean {

    /**
     * id : 1
     * kind : province
     * province_code : 110000
     * province_name : 北京
     * updated_at : 1567998881640
     * created_at : 1567998881640
     */

    private int id;
    private int province_code;
    private String province_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvince_code() {
        return province_code;
    }

    public void setProvince_code(int province_code) {
        this.province_code = province_code;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }
}
