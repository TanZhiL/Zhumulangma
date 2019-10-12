package com.gykj.zhumulangma.common.event;
/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:Fragment事件
 */
public class FragmentEvent extends BaseEvent {
    public FragmentEvent(int code) {
        super(code);
    }
    public FragmentEvent(int code, Object o) {
        super(code,o);
    }
}
