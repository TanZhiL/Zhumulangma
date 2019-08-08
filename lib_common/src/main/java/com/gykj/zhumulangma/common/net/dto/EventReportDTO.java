package com.gykj.zhumulangma.common.net.dto;

import com.gykj.zhumulangma.common.bean.UploadFileBean;

import java.util.List;

/**
 * Author: Thomas.
 * Date: 2019/7/31 14:57
 * Email: 1071931588@qq.com
 * Description:
 */
public class EventReportDTO {

    /**
     * river_name : 河流名字
     * report_type : 上报类型
     * overview : 描述
     * address : 地址
     * contacts : 上报人
     * phone : 电话
     * contacts_id : 身份证
     * report_video : {"file_id":"201907301037314268","file_name":"abc","file_type":"txt","file_path":"/upload/7815696ecbf1c96e6894b779456d330e.txt","file_size":3,"file_md5":"7815696ecbf1c96e6894b779456d330e"}
     * photos : [{"file_id":"201907301037314268","file_name":"abc","file_type":"txt","file_path":"/upload/7815696ecbf1c96e6894b779456d330e.txt","file_size":3,"file_md5":"7815696ecbf1c96e6894b779456d330e"}]
     */

    private String river_name;
    private String river_code;
    private String report_type;
    private String overview;
    private String address;
    private String contacts;
    private String phone;
    private String contacts_id;
    private UploadFileBean report_video;
    private List<UploadFileBean> photos;

    public String getRiver_name() {
        return river_name;
    }

    public void setRiver_name(String river_name) {
        this.river_name = river_name;
    }

    public String getReport_type() {
        return report_type;
    }

    public void setReport_type(String report_type) {
        this.report_type = report_type;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContacts_id() {
        return contacts_id;
    }

    public void setContacts_id(String contacts_id) {
        this.contacts_id = contacts_id;
    }

    public UploadFileBean getReport_video() {
        return report_video;
    }

    public void setReport_video(UploadFileBean report_video) {
        this.report_video = report_video;
    }

    public List<UploadFileBean> getPhotos() {
        return photos;
    }

    public void setPhotos(List<UploadFileBean> photos) {
        this.photos = photos;
    }

    public String getRiver_code() {
        return river_code;
    }

    public void setRiver_code(String river_code) {
        this.river_code = river_code;
    }
}
