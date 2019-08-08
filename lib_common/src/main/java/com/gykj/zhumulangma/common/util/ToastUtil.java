package com.gykj.zhumulangma.common.util;

import android.widget.Toast;

import com.gykj.zhumulangma.common.Application;

/**
 * Description: <吐司工具类><br>
 * Author: mxdl<br>
 * Date: 2018/6/11<br>
 * Version: V1.0.0<br>
 * Update: <br>
 */
public class ToastUtil {

    public static void showToast(String message) {
        Toast.makeText(Application.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int resid) {
        Toast.makeText(Application.getInstance(), Application.getInstance().getString(resid), Toast.LENGTH_SHORT)
                .show();
    }
}