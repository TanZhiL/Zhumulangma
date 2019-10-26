package com.gykj.zhumulangma.home.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.home.R;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;


/**
 * Created by 10719
 * on 2019/6/11
 */
public class AlbumCategoryItem extends FrameLayout {

    private int icon;
    private String title="分类一";
    private  int categoryId;

    public AlbumCategoryItem(@NonNull Context context) {
        this(context,null);
    }

    public AlbumCategoryItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AlbumCategoryItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.AlbumCategoryItem,defStyleAttr,0);
        getAttrs(typedArray);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.home_widget_category_item,this);

        TextView tvTitle=findViewById(R.id.tv_title);
        ImageView ivIcon=findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setImageResource(icon);

        this.setOnClickListener(view -> {

            Object o = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                    .withInt(KeyCode.Home.TYPE, categoryId)
                    .withString(KeyCode.Home.TITLE,title)
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE,new NavigateBean(Constants.Router.Home.F_ALBUM_LIST, (ISupportFragment) o)));
        });
    }

    private void getAttrs(TypedArray typedArray) {
        icon=typedArray.getResourceId(R.styleable.AlbumCategoryItem_aci_icon,icon);
        categoryId=typedArray.getInt(R.styleable.AlbumCategoryItem_aci_id,3);
        title=typedArray.getString(R.styleable.AlbumCategoryItem_aci_title);
    }


}
