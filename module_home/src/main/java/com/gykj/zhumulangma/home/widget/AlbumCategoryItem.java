package com.gykj.zhumulangma.home.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.home.R;


/**
 * Created by 10719
 * on 2019/6/11
 */
public class AlbumCategoryItem extends ConstraintLayout {

    private int icon;
    private String title="分类一";

    public AlbumCategoryItem(@NonNull Context context) {
        this(context,null);
    }

    public AlbumCategoryItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AlbumCategoryItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.AlbumCategoryItem);
        getAttrs(typedArray);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.home_widget_category_item,this);

        TextView tvTitle=findViewById(R.id.tv_title);
        ImageView ivIcon=findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setImageResource(icon);

        this.setOnClickListener(view ->
                RouterUtil.navigateTo(ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_LIST)
                .withInt(KeyCode.Home.TYPE, getTag()==null?3: Integer.parseInt(getTag().toString()))
                .withString(KeyCode.Home.TITLE,title)));
    }

    private void getAttrs(TypedArray typedArray) {
        icon=typedArray.getResourceId(R.styleable.AlbumCategoryItem_aci_icon,icon);
        title=typedArray.getString(R.styleable.AlbumCategoryItem_aci_title);
    }

}
