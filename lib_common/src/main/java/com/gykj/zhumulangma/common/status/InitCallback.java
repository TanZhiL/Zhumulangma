package com.gykj.zhumulangma.common.status;

import android.content.Context;
import android.view.View;

import com.gykj.zhumulangma.common.R;
import com.kingja.loadsir.callback.Callback;

/**
 * Author: Thomas.
 * Date: 2019/7/22 10:46
 * Email: 1071931588@qq.com
 * Description:
 */
public class InitCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.common_layout_init;
    }

    @Override
    protected boolean onReloadEvent(Context context, View view) {
        //不响应reload事件
        return true;
    }

}
