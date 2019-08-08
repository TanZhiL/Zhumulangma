package com.gykj.zhumulangma.common.net.dto;

/**
 * Author: Thomas.
 * Date: 2019/8/5 12:00
 * Email: 1071931588@qq.com
 * Description:
 */
public class PollutionHappenDTO {


    /**
     * river_name : 测试河流
     * pi_data : 0.5
     * bod5_data : 0.7
     * tp_data : 0.2
     * cod_data : 2.1
     * an_data : 1.2
     * do_data : 1.1
     * pollute_index : 0.8
     */

    private String river_name;
    private String river_code;
    private float pi_data;
    private float bod5_data;
    private float tp_data;
    private float cod_data;
    private float an_data;
    private float do_data;
    private float pollute_index;

    public PollutionHappenDTO() {
    }

    public PollutionHappenDTO(String river_name,
                              String river_code,
                              float pi_data,
                              float bod5_data,
                              float tp_data,
                              float cod_data,
                              float an_data,
                              float do_data,
                              float pollute_index) {
        this.river_name = river_name;
        this.river_code = river_code;
        this.pi_data = pi_data;
        this.bod5_data = bod5_data;
        this.tp_data = tp_data;
        this.cod_data = cod_data;
        this.an_data = an_data;
        this.do_data = do_data;
        this.pollute_index = pollute_index;
    }

    public String getRiver_name() {
        return river_name;
    }

    public void setRiver_name(String river_name) {
        this.river_name = river_name;
    }

    public float getPi_data() {
        return pi_data;
    }

    public void setPi_data(float pi_data) {
        this.pi_data = pi_data;
    }

    public float getBod5_data() {
        return bod5_data;
    }

    public void setBod5_data(float bod5_data) {
        this.bod5_data = bod5_data;
    }

    public float getTp_data() {
        return tp_data;
    }

    public void setTp_data(float tp_data) {
        this.tp_data = tp_data;
    }

    public float getCod_data() {
        return cod_data;
    }

    public void setCod_data(float cod_data) {
        this.cod_data = cod_data;
    }

    public float getAn_data() {
        return an_data;
    }

    public void setAn_data(float an_data) {
        this.an_data = an_data;
    }

    public float getDo_data() {
        return do_data;
    }

    public void setDo_data(float do_data) {
        this.do_data = do_data;
    }

    public float getPollute_index() {
        return pollute_index;
    }

    public void setPollute_index(float pollute_index) {
        this.pollute_index = pollute_index;
    }

    public String getRiver_code() {
        return river_code;
    }

    public void setRiver_code(String river_code) {
        this.river_code = river_code;
    }
}
