package com.gykj.zhumulangma.common.net.dto;

/**
 * Author: Thomas.
 * <br/>Date: 2019/7/31 9:21
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class LoginDTO {

    /**
     * code : 123456
     * descer_name : 决策中心
     * descer_phone : 决策中心电话
     * graer_name : 基层员工
     * graer_phone : 基层员工电话
     */

    private String code;
    private String descer_name;
    private String descer_phone;
    private String graer_name;
    private String graer_phone;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescer_name() {
        return descer_name;
    }

    public void setDescer_name(String descer_name) {
        this.descer_name = descer_name;
    }

    public String getDescer_phone() {
        return descer_phone;
    }

    public void setDescer_phone(String descer_phone) {
        this.descer_phone = descer_phone;
    }

    public String getGraer_name() {
        return graer_name;
    }

    public void setGraer_name(String graer_name) {
        this.graer_name = graer_name;
    }

    public String getGraer_phone() {
        return graer_phone;
    }

    public void setGraer_phone(String graer_phone) {
        this.graer_phone = graer_phone;
    }
}
