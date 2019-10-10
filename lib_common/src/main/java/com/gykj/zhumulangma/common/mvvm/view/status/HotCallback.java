package com.gykj.zhumulangma.common.mvvm.view.status;

import android.content.Context;
import android.view.View;

import com.gykj.zhumulangma.common.R;
import com.kingja.loadsir.callback.Callback;

/**
 * Author: Thomas.
 * Date: 2019/10/10 10:51
 * Email: 1071931588@qq.com
 * Description:
 */
public class HotCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.common_layout_skeleton_hot;
    }

    @Override
    protected boolean onReloadEvent(Context context, View view) {
        //不响应reload事件
        return true;
    }
}
