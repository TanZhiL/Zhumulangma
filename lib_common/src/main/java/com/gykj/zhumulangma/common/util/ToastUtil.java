package com.gykj.zhumulangma.common.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gykj.zhumulangma.common.R;
import com.hjq.toast.ToastUtils;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 13:41
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:Toast
 */
public class ToastUtil {

    public static final int LEVEL_W = 0;
    public static final int LEVEL_E = 1;
    public static final int LEVEL_S = 2;

    public static void showToast(String message) {
        showToast(LEVEL_W, message);
    }

    public static void showToast(int resid) {
        showToast(LEVEL_W, resid);
    }

    public static void showToast(int level, String message) {
        ImageView ivIcon = ToastUtils.getView().findViewById(R.id.iv_icon);
        ivIcon.setVisibility(View.VISIBLE);
        switch (level) {
            case LEVEL_W:
                ivIcon.setImageResource(R.drawable.ic_common_warnning);
                break;
            case LEVEL_E:
                ivIcon.setImageResource(R.drawable.ic_third_update_close);
                break;
            case LEVEL_S:
                ivIcon.setImageResource(R.drawable.ic_common_succ);
                break;
        }
        TextView tvTip = ToastUtils.getView().findViewById(R.id.tv_tip);
        tvTip.setText(message);
        ToastUtils.show(message);
    }

    public static void showToast(int level, int resid) {
        ImageView ivIcon = ToastUtils.getView().findViewById(R.id.iv_icon);
        ivIcon.setVisibility(View.VISIBLE);
        switch (level) {
            case LEVEL_W:
                ivIcon.setImageResource(R.drawable.ic_common_warnning);
                break;
            case LEVEL_E:
                ivIcon.setImageResource(R.drawable.ic_third_update_close);
                break;
            case LEVEL_S:
                ivIcon.setImageResource(R.drawable.ic_common_succ);
                break;
        }
        TextView tvTip = ToastUtils.getView().findViewById(R.id.tv_tip);
        tvTip.setText(resid);
        ToastUtils.show(resid);
    }
}