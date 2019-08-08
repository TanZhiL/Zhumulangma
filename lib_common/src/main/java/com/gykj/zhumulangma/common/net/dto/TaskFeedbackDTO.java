package com.gykj.zhumulangma.common.net.dto;

import com.gykj.zhumulangma.common.bean.UploadFileBean;

import java.util.List;

/**
 * Author: Thomas.
 * Date: 2019/8/2 16:34
 * Email: 1071931588@qq.com
 * Description:
 */
public class TaskFeedbackDTO {


    private String task_type;
    private String task_solve_result;
    private UploadFileBean task_solve_video;
    private List<UploadFileBean> task_solve_file;

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_solve_result() {
        return task_solve_result;
    }

    public void setTask_solve_result(String task_solve_result) {
        this.task_solve_result = task_solve_result;
    }

    public UploadFileBean getTask_solve_video() {
        return task_solve_video;
    }

    public void setTask_solve_video(UploadFileBean task_solve_video) {
        this.task_solve_video = task_solve_video;
    }

    public List<UploadFileBean> getTask_solve_file() {
        return task_solve_file;
    }

    public void setTask_solve_file(List<UploadFileBean> task_solve_file) {
        this.task_solve_file = task_solve_file;
    }
}
