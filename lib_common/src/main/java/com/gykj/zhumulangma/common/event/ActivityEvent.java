package com.gykj.zhumulangma.common.event;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:Activity事件
 */
public class ActivityEvent extends BaseEvent {
    public ActivityEvent(int code) {
        super(code);
    }
    public ActivityEvent(int code, Object data) {
        super(code,data);
    }
}
