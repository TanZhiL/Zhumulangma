package com.gykj.zhumulangma.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: Thomas.
 * Date: 2019/7/30 17:47
 * Email: 1071931588@qq.com
 * Description:
 */
public class UserBean implements Parcelable {

    /**
     * code : 123456
     * descer_name : 决策中心
     * descer_phone : 决策中心电话
     * graer_name : 基层员工
     * graer_phone : 基层员工电话
     * flag : 0
     * gy_gxsj : 2019-07-30 17:10:25
     * _id : 5d4009813638b6ad83992fb5
     * token : 585090
     */

    private String code;
    private String descer_name;
    private String descer_phone;
    private String graer_name;
    private String graer_phone;
    private int flag;
    private String gy_gxsj;
    private String _id;
    private String token;

    @Override
    public String toString() {
        return "UserBean{" +
                "code='" + code + '\'' +
                ", descer_name='" + descer_name + '\'' +
                ", descer_phone='" + descer_phone + '\'' +
                ", graer_name='" + graer_name + '\'' +
                ", graer_phone='" + graer_phone + '\'' +
                ", flag=" + flag +
                ", gy_gxsj='" + gy_gxsj + '\'' +
                ", _id='" + _id + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getGy_gxsj() {
        return gy_gxsj;
    }

    public void setGy_gxsj(String gy_gxsj) {
        this.gy_gxsj = gy_gxsj;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.descer_name);
        dest.writeString(this.descer_phone);
        dest.writeString(this.graer_name);
        dest.writeString(this.graer_phone);
        dest.writeInt(this.flag);
        dest.writeString(this.gy_gxsj);
        dest.writeString(this._id);
        dest.writeString(this.token);
    }

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        this.code = in.readString();
        this.descer_name = in.readString();
        this.descer_phone = in.readString();
        this.graer_name = in.readString();
        this.graer_phone = in.readString();
        this.flag = in.readInt();
        this.gy_gxsj = in.readString();
        this._id = in.readString();
        this.token = in.readString();
    }

    public static final Parcelable.Creator<UserBean> CREATOR = new Parcelable.Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
