package com.gykj.zhumulangma.home.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.home.R;
import com.lxj.xpopup.core.CenterPopupView;

/**
 * Author: Thomas.
 * Date: 2019/9/19 17:32
 * Email: 1071931588@qq.com
 * Description:
 */
public class SpeechPopup extends CenterPopupView {

    public SpeechPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.home_dialog_speech;
    }

}
