package com.gykj.zhumulangma.home.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.R;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/9 9:07
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class RadioCategoryItem extends androidx.appcompat.widget.AppCompatTextView {
    private int category;
    public RadioCategoryItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadioCategoryItem);
        getAttrs(typedArray);
        typedArray.recycle();
        this.setOnClickListener(view ->
                RouteHelper.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                        .withInt(KeyCode.Home.CATEGORY, category)
                        .withString(KeyCode.Home.TAG, getTag().toString())
                        .withString(KeyCode.Home.TITLE, getText().toString())));
    }

    public void setCategory(int category) {
        this.category = category;
    }

    private void getAttrs(TypedArray typedArray) {
        category = typedArray.getInt(R.styleable.RadioCategoryItem_rci_category, 3);
    }
}
