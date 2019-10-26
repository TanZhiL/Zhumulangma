package com.gykj.zhumulangma.common.mvvm.view.status;

import android.content.Context;
import android.view.View;

import com.gykj.zhumulangma.common.R;
import com.kingja.loadsir.callback.Callback;

/**
 * Author: Thomas.
 * <br/>Date: 2019/7/22 10:46
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class LoadingStatus extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.common_layout_loading;
    }
    @Override
    protected boolean onReloadEvent(Context context, View view) {
        //不响应reload事件
        return true;
    }

    @Override
    public boolean getSuccessVisible() {
        //背景可自定义
        return true;
    }
}
