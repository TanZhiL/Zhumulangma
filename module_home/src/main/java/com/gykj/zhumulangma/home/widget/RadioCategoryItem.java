package com.gykj.zhumulangma.home.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.home.R;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * Date: 2019/9/9 9:07
 * Email: 1071931588@qq.com
 * Description:
 */
public class RadioCategoryItem extends android.support.v7.widget.AppCompatTextView {
    private int categoryId;
    public RadioCategoryItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.RadioCategoryItem);
        getAttrs(typedArray);
        typedArray.recycle();

        this.setOnClickListener(view -> {

            Object o = ARouter.getInstance().build(AppConstants.Router.Home.F_RADIO_LIST)
                    .withInt(KeyCode.Home.TYPE, categoryId)
                    .withString(KeyCode.Home.TITLE,getText().toString())
                    .navigation();
            EventBus.getDefault().post(new BaseActivityEvent<>(
                    EventCode.MainCode.NAVIGATE,new NavigateBean(AppConstants.Router.Home.F_RADIO_LIST, (ISupportFragment) o)));
        });
    }

    private void getAttrs(TypedArray typedArray) {
        categoryId=typedArray.getInt(R.styleable.RadioCategoryItem_rci_id,5);
    }

}
