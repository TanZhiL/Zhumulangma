package com.gykj.zhumulangma.home.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.db.DBManager;
import com.gykj.zhumulangma.common.extra.RxField;
import com.gykj.zhumulangma.common.net.RxAdapter;
import com.gykj.zhumulangma.home.R;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.Arrays;

import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/2 11:48
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class PlaySchedulePopup extends BottomPopupView implements View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener, OnOptionsSelectListener {
    private static final String TAG = "PlaySchedulePopup";
    private PlayScheduleAdapter mScheduleAdapter;
    private Context mContext;
    private DBManager mDBManager = DBManager.getInstance();
    private onSelectedListener mListener;
    private String[] mSchedules = {"不开启", "播完当前声音", "10分钟", "20分钟", "30分钟", "60分钟", "90分钟", "自定义"};
    private String[] mHours = {"0小时", "1小时", "2小时", "3小时", "4小时", "5小时", "6小时", "7小时",
            "8小时", "9小时", "10小时", "11小时", "12小时", "13小时", "14小时", "15小时",
            "16小时", "17小时", "18小时", "19小时", "20小时", "21小时", "22小时", "23小时"
    };
    private String[] mMinutes = {"0分钟", "5分钟", "10分钟", "15分钟", "20分钟", "25分钟", "30分钟", "35分钟", "40分钟", "45分钟", "55分钟"};

    private int[] mTimes = {0, 0, 10, 20, 30, 60, 90, 0};
    OptionsPickerView<String> pickerView;

    public PlaySchedulePopup(@NonNull Context context, @NonNull onSelectedListener listener) {
        super(context);
        mContext = context;
        mListener = listener;

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
        mScheduleAdapter = new PlayScheduleAdapter(R.layout.home_item_play_schedule);
        mScheduleAdapter.bindToRecyclerView(recyclerView);
        mScheduleAdapter.setNewData(Arrays.asList(mSchedules));
        findViewById(R.id.tv_close).setOnClickListener(this);
        mScheduleAdapter.setOnItemClickListener(this);

    }

    // 最大高度为Window的0.85
    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext()) * .85f);
    }

    public PlayScheduleAdapter getScheduleAdapter() {
        return mScheduleAdapter;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        RadioButton radioButton = (RadioButton) adapter.getViewByPosition(position, R.id.rb_indicator);
        radioButton.setChecked(true);
        if (position != 7) {
            mDBManager.putSP(Constants.SP.PLAY_SCHEDULE_TYPE, position)
                    .compose(RxAdapter.exceptionTransformer())
                    .compose(RxAdapter.schedulersTransformer())
                    .doOnSubscribe((Consumer<Disposable>) mContext)
                    .flatMap((Function<Integer, ObservableSource<Long>>) aBoolean ->
                            mDBManager.putSP(Constants.SP.PLAY_SCHEDULE_TIME,
                                    System.currentTimeMillis() + mTimes[position] * 60 * 1000))
                    .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace);
        }
        if (position != 0 && position != 1 && position != 7) {
            mDBManager.getSPLong(Constants.SP.PLAY_SCHEDULE_TIME, 0)
                    .compose(RxAdapter.exceptionTransformer())
                    .compose(RxAdapter.schedulersTransformer())
                    .doOnSubscribe((Consumer<Disposable>) mContext)
                    .subscribe(aLong -> {
                        mListener.onSelected(position, aLong);
                        XmPlayerManager.getInstance(mContext).pausePlayInMillis(aLong);
                    }, Throwable::printStackTrace);
        } else if (position == 0) {
            mListener.onSelected(position, -1);
            XmPlayerManager.getInstance(mContext).pausePlayInMillis(0);
        } else if (position == 1) {
            mListener.onSelected(position, 0);
            XmPlayerManager.getInstance(mContext).pausePlayInMillis(-1);
        } else {
            pickerView = new OptionsPickerBuilder(mContext, this)
                    .setCancelColor(0x8A000000)
                    .setSubmitColor(mContext.getResources().getColor(R.color.colorPrimary))
                    .setCyclic(true, true, true)
                    .setLineSpacingMultiplier(2.6f)
                    .setSelectOptions(0, 1)
                    .setDecorView(((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content))
                    .build();
            pickerView.setNPicker(Arrays.asList(mHours), Arrays.asList(mMinutes), null);
            pickerView.show();
        }
        dismiss();

    }


    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        mDBManager.putSP(Constants.SP.PLAY_SCHEDULE_TYPE, 7)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer())
                .doOnSubscribe((Consumer<Disposable>) mContext)
                .flatMap((Function<Integer, ObservableSource<Long>>) integer ->
                        mDBManager.putSP(Constants.SP.PLAY_SCHEDULE_TIME, System.currentTimeMillis()
                                + options1 * 1000 * 60 * 60 + options2 * 1000 * 5 * 60))
                .subscribe(aLong -> {
                    mListener.onSelected(7, aLong);
                    XmPlayerManager.getInstance(mContext).pausePlayInMillis(aLong);
                }, Throwable::printStackTrace);
    }

    public OptionsPickerView<String> getPickerView() {
        return pickerView;
    }

    public class PlayScheduleAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public PlayScheduleAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_label, item);
            RxField<Integer> type = new RxField<>();
            RxField<Long> time = new RxField<>();

            mDBManager.getSPInt(Constants.SP.PLAY_SCHEDULE_TYPE, 0)
                    .compose(RxAdapter.exceptionTransformer())
                    .compose(RxAdapter.schedulersTransformer())
                    .doOnSubscribe((Consumer<Disposable>) mContext)
                    .flatMap((Function<Integer, ObservableSource<Long>>) integer -> {
                        type.set(integer);
                        return mDBManager.getSPLong(Constants.SP.PLAY_SCHEDULE_TIME, 0);
                    })
                    .subscribe(aLong -> {
                        time.set(aLong);

                        if (helper.getLayoutPosition() == 0) {
                            if (type.get() == 0) {
                                helper.setChecked(R.id.rb_indicator, true);
                            } else if (type.get() == 1) {
                                helper.setChecked(R.id.rb_indicator, false);
                            } else if (System.currentTimeMillis() >= time.get()) {
                                helper.setChecked(R.id.rb_indicator, true);
                            } else {
                                helper.setChecked(R.id.rb_indicator, false);
                            }
                        } else {
                            if (helper.getLayoutPosition() == type.get()) {
                                if (type.get() == 1) {
                                    helper.setChecked(R.id.rb_indicator, true);
                                } else {
                                    if (System.currentTimeMillis() < time.get()) {
                                        helper.setChecked(R.id.rb_indicator, true);
                                    } else {
                                        helper.setChecked(R.id.rb_indicator, false);
                                    }
                                }
                            } else {
                                helper.setChecked(R.id.rb_indicator, false);
                            }
                        }
                    }, Throwable::printStackTrace);
        }
    }

    public interface onSelectedListener {
        void onSelected(int type, long time);
    }
}
