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
 * Date: 2019/9/30 10:33
 * Email: 1071931588@qq.com
 * Description:
 */
public class ProvincePopup extends PartShadowPopupView implements BaseQuickAdapter.OnItemClickListener {
    private onSelectedListener mListener;

    private ProvinceAdapter mProvinceAdapter;
    private RecyclerView rvProvince;
    private List<ProvinceBean> mProvinceBeans;
    public ProvincePopup(@NonNull Context context) {
        super(context);
    }
    public ProvincePopup(@NonNull Context context, onSelectedListener listener) {
        super(context);
        mListener=listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.home_dialog_category;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        String s = ResourceUtils.readAssets2String("province.json");
        mProvinceBeans = new Gson().fromJson(s, new TypeToken<ArrayList<ProvinceBean>>() {
        }.getType());
        rvProvince = findViewById(R.id.rv_category);
        rvProvince.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mProvinceAdapter = new ProvinceAdapter(R.layout.home_item_rank_category, mProvinceBeans);
        rvProvince.setHasFixedSize(true);
        mProvinceAdapter.bindToRecyclerView(rvProvince);
        mProvinceAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        dismissWith(()-> mListener.onSelected(mProvinceBeans.get(position).getProvince_code(),
                mProvinceBeans.get(position).getProvince_name()));

    }
    public interface onSelectedListener{
       void onSelected(int province_code, String province_name);
    }
}
