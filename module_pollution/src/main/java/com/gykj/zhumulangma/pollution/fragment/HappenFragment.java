package com.gykj.zhumulangma.pollution.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.KeyboardUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.Application;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.polltion.R;
import com.gykj.zhumulangma.pollution.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.pollution.mvvm.viewmodel.HappenViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Route(path = AppConstants.Router.Pollution.F_HAPPEN)
public class HappenFragment extends BaseMvvmFragment<HappenViewModel> implements View.OnClickListener, View.OnFocusChangeListener, View.OnTouchListener {

    private TextView tvRiverName;
    OptionsPickerView opvRiverName;

    private IndicatorSeekBar isbPiData,isbBod5Data,isbTpData,isbCodData,isbAnData,isDoData,isbPolluteIndex;

    private HashMap<String, String> mRiverNames;

    public HappenFragment() {
    }

    @Override
    protected int onBindLayout() {
        return R.layout.pollution_fragment_report;
    }

    @Override
    protected void initView(View view) {
        tvRiverName = fd(R.id.tv_river_name);
        isbPiData=fd(R.id.isb_pi_data);
        isbBod5Data=fd(R.id.isb_bod5_data);
        isbTpData=fd(R.id.isb_tp_data);
        isbCodData=fd(R.id.isb_cod_data);
        isbAnData=fd(R.id.isb_an_data);
        isDoData=fd(R.id.isb_do_data);
        isbPolluteIndex=fd(R.id.isb_pollute_index);

        opvRiverName = new OptionsPickerBuilder(mContext, (options1, option2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            tvRiverName.setText((String) mRiverNames.keySet().toArray()[options1]);
        }).build();
    }

    @Override
    public void initListener() {
        tvRiverName.setOnTouchListener(this);
        RxView.clicks(fd(R.id.btn_commit))
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(unit -> mViewModel.happen(
                        tvRiverName.getText().toString(),
                        mRiverNames.get(tvRiverName.getText().toString()),
                        isbPiData.getProgressFloat(),
                        isbBod5Data.getProgressFloat(),
                        isbTpData.getProgressFloat(),
                        isbCodData.getProgressFloat(),
                        isbAnData.getProgressFloat(),
                        isDoData.getProgressFloat(),
                        isbPolluteIndex.getProgressFloat()
                ));
    }

    @Override
    public void initData() {
        mRiverNames = new HashMap<>();
        String[] stringArray = getResources().getStringArray(R.array.river_names);
        for (int i = 0; i <stringArray.length; i++) {
            mRiverNames.put(stringArray[i].split(":")[0],stringArray[i].split(":")[1]);
        }
        opvRiverName.setPicker(new ArrayList(mRiverNames.keySet()));
    }

    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"污染发生"};
    }

    @Override
    public void onClick(View v) {
        if (v == tvRiverName) {
            //选取河流名称
            opvRiverName.show();
            KeyboardUtils.hideSoftInput(_mActivity);
        }
    }

    @Override
    public Class<HappenViewModel> onBindViewModel() {
        return HappenViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(Application.getInstance());
    }

    @Override
    public void initViewObservable() {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            if (v == tvRiverName) {
                //选取河流名称
                opvRiverName.show();
                KeyboardUtils.hideSoftInput(_mActivity);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == tvRiverName) {
            //选取河流名称
            opvRiverName.show();
            KeyboardUtils.hideSoftInput(_mActivity);
        }
        return false;
    }
}
