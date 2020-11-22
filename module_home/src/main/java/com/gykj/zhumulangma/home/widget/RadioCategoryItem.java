package com.gykj.zhumulangma.home.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.util.RouterUtil;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/9 9:07
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class RadioCategoryItem extends androidx.appcompat.widget.AppCompatTextView {
    public RadioCategoryItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOnClickListener(view ->
                RouterUtil.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, 3)
                        .withString(KeyCode.Home.TAG, getTag().toString())
                        .withString(KeyCode.Home.TITLE, getText().toString())));
    }

}
