package com.gykj.zhumulangma.common.bean;

import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/6 14:10
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class TaskBean {

    /**
     * task_status : 已处理
     * flag : 0
     * task_solve_result : 处理结果
     * task_solve_time : 2019-08-05 15:09:56
     * task_from : 投诉事件
     * task_overview : 描述
     * task_solve_file : [{"file_id":"201907301037314268","file_name":"abc","file_type":"txt","file_path":"/upload/7815696ecbf1c96e6894b779456d330e.txt","file_size":3,"file_md5":"7815696ecbf1c96e6894b779456d330e"}]
     * task_solve_video : {"file_id":"201907301037314268","file_name":"abc","file_type":"mp4","file_path":"/video/video_1.mp4","file_size":3,"file_md5":"7815696ecbf1c96e6894b779456d330e"}
     * task_init_time : 2019-08-05 15:00:52
     * gy_gxsj : 2019-08-05 15:09:57
     * task_person_phone : 15602800418
     * task_person_name : 基层员工
     * task_content : 第一次指派
     * geometry : {"coordinates":[1.1835849457030049E7,4207289.96588017],"type":"Point"}
     * _id : 5d47d6450e587621c03ecb12
     * task_type : 事件类型
     * task_num : 2019080515005252
     */

    private String task_status;
    private int flag;
    private String task_solve_result;
    private String task_solve_time;
    private String task_from;
    private String task_overview;
    private UploadFileBean task_solve_video;
    private String task_init_time;
    private String gy_gxsj;
    private String task_person_phone;
    private String task_person_name;
    private String task_content;
//    private GeometryBean geometry;
    private String _id;
    private String task_type;
    private String task_num;
    private List<UploadFileBean> task_solve_file;

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTask_solve_result() {
        return task_solve_result;
    }

    public void setTask_solve_result(String task_solve_result) {
        this.task_solve_result = task_solve_result;
    }

    public String getTask_solve_time() {
        return task_solve_time;
    }

    public void setTask_solve_time(String task_solve_time) {
        this.task_solve_time = task_solve_time;
    }

    public String getTask_from() {
        return task_from;
    }

    public void setTask_from(String task_from) {
        this.task_from = task_from;
    }

    public String getTask_overview() {
        return task_overview;
    }

    public void setTask_overview(String task_overview) {
        this.task_overview = task_overview;
    }

    public UploadFileBean getTask_solve_video() {
        return task_solve_video;
    }

    public void setTask_solve_video(UploadFileBean task_solve_video) {
        this.task_solve_video = task_solve_video;
    }

    public String getTask_init_time() {
        return task_init_time;
    }

    public void setTask_init_time(String task_init_time) {
        this.task_init_time = task_init_time;
    }

    public String getGy_gxsj() {
        return gy_gxsj;
    }

    public void setGy_gxsj(String gy_gxsj) {
        this.gy_gxsj = gy_gxsj;
    }

    public String getTask_person_phone() {
        return task_person_phone;
    }

    public void setTask_person_phone(String task_person_phone) {
        this.task_person_phone = task_person_phone;
    }

    public String getTask_person_name() {
        return task_person_name;
    }

    public void setTask_person_name(String task_person_name) {
        this.task_person_name = task_person_name;
    }

    public String getTask_content() {
        return task_content;
    }

    public void setTask_content(String task_content) {
        this.task_content = task_content;
    }

  /*  public GeometryBean getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryBean geometry) {
        this.geometry = geometry;
    }*/

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_num() {
        return task_num;
    }

    public void setTask_num(String task_num) {
        this.task_num = task_num;
    }

    public List<UploadFileBean> getTask_solve_file() {
        return task_solve_file;
    }

    public void setTask_solve_file(List<UploadFileBean> task_solve_file) {
        this.task_solve_file = task_solve_file;
    }

    public static class GeometryBean {

        /**
         * coordinates : [{"high":3471712362749231104,"low":11840856286293413,"naN":false,"infinite":false,"finite":true,"negative":false},{"high":3472275312702652416,"low":421014005618883,"naN":false,"infinite":false,"finite":true,"negative":false}]
         * type : Point
         */

        private String type;
        private List<CoordinatesBean> coordinates;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<CoordinatesBean> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<CoordinatesBean> coordinates) {
            this.coordinates = coordinates;
        }

        public static class CoordinatesBean {
            /**
             * high : 3471712362749231104
             * low : 11840856286293413
             * naN : false
             * infinite : false
             * finite : true
             * negative : false
             */

            private long high;
            private long low;
            private boolean naN;
            private boolean infinite;
            private boolean finite;
            private boolean negative;

            public long getHigh() {
                return high;
            }

            public void setHigh(long high) {
                this.high = high;
            }

            public long getLow() {
                return low;
            }

            public void setLow(long low) {
                this.low = low;
            }

            public boolean isNaN() {
                return naN;
            }

            public void setNaN(boolean naN) {
                this.naN = naN;
            }

            public boolean isInfinite() {
                return infinite;
            }

            public void setInfinite(boolean infinite) {
                this.infinite = infinite;
            }

            public boolean isFinite() {
                return finite;
            }

            public void setFinite(boolean finite) {
                this.finite = finite;
            }

            public boolean isNegative() {
                return negative;
            }

            public void setNegative(boolean negative) {
                this.negative = negative;
            }
        }
    }
}
