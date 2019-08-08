package com.gykj.zhumulangma.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.gykj.zhumulangma.common.R;
import com.gykj.util.DisplayUtil;
import com.gykj.util.MultiMediaUtil;

import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Description: <PhotoSelectDialog><br>
 * Author: mxdl<br>
 * Date: 2019/1/3<br>
 * Version: V1.0.0<br>
 * Update: <br>
 */
public class PhotoSelectDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final String TAG = PhotoSelectDialog.class.getSimpleName();
    private Context mContext;
    private OnPhotoClickLisener mOnClickLisener;
    private MultiMediaUtil.OnMediaError mOnMediaError;
    private String mPhotoPath;

    public void setOnClickLisener(OnPhotoClickLisener onPhotoClickLisener) {
        mOnClickLisener = onPhotoClickLisener;
    }

    public void setOnMediaError(MultiMediaUtil.OnMediaError onMediaError) {
        mOnMediaError = onMediaError;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext=context;
    }

    public static PhotoSelectDialog newInstance() {
        return new PhotoSelectDialog();
    }

    public interface OnPhotoClickLisener {
        void onTakePhototClick(String path);

        void onSelectPhotoClick(List<String> list);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(mContext,16) * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_widget_photo_select, container, false);
        Button btnSelectPhoto = (Button) view.findViewById(R.id.btn_select_photo);
        Button btnTakephoto = (Button) view.findViewById(R.id.btn_take_photo);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnSelectPhoto.setOnClickListener(this);
        btnTakephoto.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_take_photo) {
            mPhotoPath = MultiMediaUtil.getPhotoPath(mContext);
            MultiMediaUtil.takePhoto(this, mPhotoPath, MultiMediaUtil.TAKE_PHONE,mOnMediaError);

        } else if (i == R.id.btn_select_photo) {
            MultiMediaUtil.pohotoSelect(this, 1, MultiMediaUtil.SELECT_IMAGE,mOnMediaError);

        } else if (i == R.id.btn_cancel) {
            dismiss();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MultiMediaUtil.SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if(mOnClickLisener != null){
                        mOnClickLisener.onSelectPhotoClick(path);
                    }
                    dismiss();
                }
                break;
            case MultiMediaUtil.TAKE_PHONE:
                Log.v(TAG, "img path:" + mPhotoPath);
                if (mOnClickLisener != null) {
                    mOnClickLisener.onTakePhototClick(mPhotoPath);
                }
                dismiss();
                break;
        }
    }
}
