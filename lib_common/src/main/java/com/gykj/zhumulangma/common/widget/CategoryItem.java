package com.gykj.zhumulangma.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gykj.zhumulangma.common.R;


/**
 * Created by 10719
 * on 2019/6/11
 */
public class CategoryItem extends FrameLayout {

    private int icon;
    private String title="分类一";
    private  int categoryId;

    public CategoryItem(@NonNull Context context) {
        this(context,null);
    }

    public CategoryItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CategoryItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.CategoryItem,defStyleAttr,0);
        getAttrs(typedArray);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.common_widget_category_item,this);

        TextView tvTitle=findViewById(R.id.tv_title);
        ImageView ivIcon=findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setImageResource(icon);

        this.setOnClickListener(view -> {

        });
    }

    private void getAttrs(TypedArray typedArray) {
        icon=typedArray.getResourceId(R.styleable.CategoryItem_ci_icon,icon);
        categoryId=typedArray.getInt(R.styleable.CategoryItem_ci_id,3);
        title=typedArray.getString(R.styleable.CategoryItem_ci_title);
    }


}
