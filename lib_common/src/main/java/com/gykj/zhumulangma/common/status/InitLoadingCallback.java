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
public class InitLoadingCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.common_view_init_loading;
    }

    @Override
    protected boolean onReloadEvent(Context context, View view) {
        return true;
    }

    @Override
    public boolean getSuccessVisible() {
        return true;
    }

}
