package com.gykj.zhumulangma.home.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ResourceUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gykj.zhumulangma.common.bean.ProvinceBean;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.ProvinceAdapter;
import com.lxj.xpopup.impl.PartShadowPopupView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/30 10:33
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:省市台省份选择弹窗
 */
public class RadioProvincePopup extends PartShadowPopupView implements BaseQuickAdapter.OnItemClickListener {
    private onSelectedListener mListener;

    private List<ProvinceBean> mProvinceBeans;
    public RadioProvincePopup(@NonNull Context context) {
        super(context);
    }
    public RadioProvincePopup(@NonNull Context context, onSelectedListener listener) {
        super(context);
        mListener=listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.home_dialog_recyclerview;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        String s = ResourceUtils.readAssets2String("province.json");
        mProvinceBeans = new Gson().fromJson(s, new TypeToken<ArrayList<ProvinceBean>>() {
        }.getType());
        RecyclerView rvProvince = findViewById(R.id.recyclerview);
        rvProvince.setLayoutManager(new GridLayoutManager(getContext(), 5));
        ProvinceAdapter provinceAdapter = new ProvinceAdapter(R.layout.home_item_rank_category, mProvinceBeans);
        rvProvince.setHasFixedSize(true);
        provinceAdapter.bindToRecyclerView(rvProvince);
        provinceAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        dismissWith(()-> {
            if(mListener!=null){
                mListener.onSelected(mProvinceBeans.get(position).getProvince_code(),
                        mProvinceBeans.get(position).getProvince_name());
            }
        });

    }
    public interface onSelectedListener{
       void onSelected(int province_code, String province_name);
    }
    public interface onPopupDismissingListener {

        void onDismissing();
    }
    private onPopupDismissingListener mDismissingListener;
    public void setDismissingListener(onPopupDismissingListener dismissingListener) {
        mDismissingListener = dismissingListener;
    }

    @Override
    protected void doDismissAnimation() {
        super.doDismissAnimation();
        if(mDismissingListener!=null)
            mDismissingListener.onDismissing();
    }
}
