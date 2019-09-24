package com.gykj.zhumulangma.home.dialog;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.gykj.zhumulangma.home.R;
import com.lxj.xpopup.core.BottomPopupView;

/**
 * Author: Thomas.
 * Date: 2019/9/24 10:22
 * Email: 1071931588@qq.com
 * Description:
 */
public class CommentPopup extends BottomPopupView implements View.OnClickListener {
    public CommentPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.home_dialog_comment;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.bt_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
