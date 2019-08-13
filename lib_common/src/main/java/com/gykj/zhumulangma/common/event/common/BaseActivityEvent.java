package com.gykj.zhumulangma.common.event.common;

import com.gykj.zhumulangma.common.event.BaseEvent;

/**
 * Description: <BaseActivityEvent><br>
 * Author:      mxdl<br>
 * Date:        2018/4/4<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class BaseActivityEvent<T> extends BaseEvent<T> {
    public BaseActivityEvent(int code) {
        super(code);
    }
    public BaseActivityEvent(int code,T data) {
        super(code,data);
    }
}
