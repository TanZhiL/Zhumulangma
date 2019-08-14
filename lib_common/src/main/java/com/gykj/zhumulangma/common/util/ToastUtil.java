package com.gykj.zhumulangma.common.util;

import android.widget.Toast;

import com.gykj.zhumulangma.common.App;

/**
 * Description: <吐司工具类><br>
 * Author: mxdl<br>
 * Date: 2018/6/11<br>
 * Version: V1.0.0<br>
 * Update: <br>
 */
public class ToastUtil {

    public static void showToast(String message) {
        Toast.makeText(App.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int resid) {
        Toast.makeText(App.getInstance(), App.getInstance().getString(resid), Toast.LENGTH_SHORT)
                .show();
    }
}