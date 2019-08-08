package com.gykj.zhumulangma.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gykj.zhumulangma.common.R;


public class ItemHeader extends FrameLayout {
    private String mTitle;
    private String mRightText;
    TextView tvTitle;
    TextView tvRightText;
    ImageView ivArrow;

    public ItemHeader(@NonNull Context context) {
        this(context,null);
    }

    public ItemHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ItemHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.ItemHeader,defStyleAttr,0);
        getAttrs(typedArray);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.common_widget_item_header,this);

        tvTitle=findViewById(R.id.tv_title);
        tvRightText=findViewById(R.id.tv_right_text);
        ivArrow=findViewById(R.id.iv_arrow);

        tvTitle.setText(mTitle);
        tvRightText.setText(mRightText);

    }

    private void getAttrs(TypedArray typedArray) {
        mTitle=typedArray.getString(R.styleable.ItemHeader_ih_title);
        mRightText=typedArray.getString(R.styleable.ItemHeader_ih_right_text);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        tvRightText.setOnClickListener(l);
        ivArrow.setOnClickListener(l);
    }
}
