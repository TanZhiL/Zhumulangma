package com.gykj.zhumulangma.common.net.dto;

import com.gykj.zhumulangma.common.bean.CloumnInfoBean;

import java.util.List;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/22 12:34<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class ColumnInfoDTO {
    private List<CloumnInfoBean> columns;

    public List<CloumnInfoBean> getColumns() {
        return columns;
    }

    public void setColumns(List<CloumnInfoBean> columns) {
        this.columns = columns;
    }
}