package com.gykj.zhumulangma.home.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;

import com.bigkoo.pickerview.view.OptionsPickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.Arrays;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/2 11:48
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class PlayTempoPopup extends BottomPopupView implements View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener {
    private static final String TAG = "PlaySchedulePopup";
    private PlayScheduleAdapter mTempoAdapter;
    private Context mContext;
    private onTempoSelectedListener mListener;
    public static String[] TEMPO_LABLES = {"0.5倍", "0.75倍", "1倍", "1.25倍", "1.75倍", "2倍", "2.5倍", "3倍"};
    public static float[] TEMPO_VALUES = {0.5f, 0.75f, 1f, 1.25f, 1.75f, 2f, 2.5f, 3f};
    OptionsPickerView<String> pickerView;
    public PlayTempoPopup(@NonNull Context context, @NonNull onTempoSelectedListener listener) {
        super(context);
        mContext = context;
        mListener=listener;

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.home_dialog_play_schedule;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        RecyclerView recyclerView = findViewById(R.id.rv_schedule);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mTempoAdapter = new PlayScheduleAdapter(R.layout.home_item_play_schedule);
        mTempoAdapter.bindToRecyclerView(recyclerView);
        mTempoAdapter.setNewData(Arrays.asList(TEMPO_LABLES));
        findViewById(R.id.tv_close).setOnClickListener(this);
        mTempoAdapter.setOnItemClickListener(this);

    }

    // 最大高度为Window的0.85
    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext()) * .85f);
    }

    @Override
    protected void onShow() {
        mTempoAdapter.notifyDataSetChanged();
        super.onShow();
    }

    public PlayScheduleAdapter getTempoAdapter() {
        return mTempoAdapter;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        RadioButton radioButton = (RadioButton) adapter.getViewByPosition(position, R.id.rb_indicator);
        radioButton.setChecked(true);
        XmPlayerManager.getInstance(mContext).setTempo(TEMPO_VALUES[position]);
        mListener.onTempoSelected(TEMPO_LABLES[position]);
        dismiss();

    }



    public  class PlayScheduleAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public PlayScheduleAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_label, item);
            helper.setChecked(R.id.rb_indicator,helper.getLayoutPosition()==
                    Arrays.binarySearch(TEMPO_VALUES,XmPlayerManager.getInstance(mContext).getTempo()));
        }
    }
    public interface onTempoSelectedListener{
        void onTempoSelected(String tempo);
    }
}
