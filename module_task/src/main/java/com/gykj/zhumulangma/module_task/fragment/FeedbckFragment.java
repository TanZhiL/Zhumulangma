package com.gykj.zhumulangma.module_task.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.KeyboardUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.Application;
import com.gykj.zhumulangma.common.adapter.ImageAdapter;
import com.gykj.zhumulangma.common.event.RequestCode;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.videotrimmer.features.trim.VideoTrimmerActivity;
import com.gykj.zhumulangma.module_task.R;
import com.gykj.zhumulangma.module_task.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.module_task.mvvm.viewmodel.FeedbackViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Route(path = AppConstants.Router.Task.F_FEEDBACK)
public class FeedbckFragment extends BaseMvvmFragment<FeedbackViewModel> implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {
    private final static int MAX_IMAGE = 9;
    private RecyclerView rvImage;
    private ImageAdapter mAdapter;
    private View ivFooter;
    private TextView tvReportType;
    private EditText etSolveResult;
    private Button btnCommit;

    private FrameLayout flVideo;
    private ImageView ivPlay;
    private ImageView ivThumb;
    private ImageView ivDelete;
    private String mVideoPath;

    OptionsPickerView opvReportType;

    private String[] mReportTypes;

    public FeedbckFragment() {

    }


    @Override
    protected int onBindLayout() {
        return R.layout.task_fragment_feedbck;
    }

    @Override
    protected void initView(View view) {
        rvImage = fd(R.id.rv_image);
        flVideo = fd(R.id.fl_video);
        ivPlay = fd(R.id.iv_play);
        ivDelete = fd(R.id.iv_delete);
        ivThumb = fd(R.id.iv_thumb);

        tvReportType = fd(R.id.tv_report_type);
        etSolveResult = fd(R.id.et_solve_result);
        btnCommit = fd(R.id.btn_commit);

        rvImage.setHasFixedSize(true);
        rvImage.setLayoutManager(new GridLayoutManager(mContext, 4));
        mAdapter = new ImageAdapter(com.gykj.zhumulangma.common.R.layout.common_item_image);

        FrameLayout container = new FrameLayout(mContext);
        ivFooter = LayoutInflater.from(mContext).inflate(com.gykj.zhumulangma.common.R.layout.common_item_image, container);
        ((ImageView) ivFooter.findViewById(R.id.iv_item)).setImageResource(com.gykj.zhumulangma.common.R.drawable.common_image_add);
        ivFooter.findViewById(R.id.iv_delete).setVisibility(View.GONE);

        mAdapter.addFooterView(container);
        mAdapter.setFooterViewAsFlow(true);
        mAdapter.bindToRecyclerView(rvImage);

        opvReportType = new OptionsPickerBuilder(mContext, (options1, option2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            tvReportType.setText(mReportTypes[options1]);
            tvReportType.setTextColor(Color.BLACK);
        }).build();
    }

    @Override
    public void initListener() {
        super.initListener();
        ivFooter.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
        flVideo.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        tvReportType.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mReportTypes = getResources().getStringArray(R.array.report_types);
        addDisposable(RxView.clicks(btnCommit)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(unit -> mViewModel._feedback(
                        tvReportType.getText().toString(),
                        etSolveResult.getText().toString(),
                        mAdapter.getData(),
                        mVideoPath)));
        opvReportType.setPicker(Arrays.asList(mReportTypes));
    }

    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"处理反馈"};
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        PictureSelector.create(this).themeStyle(R.style.picture_default_style).openExternalPreview(position, mAdapter.getData());
    }

    @Override
    public void onClick(View v) {
        if (v == ivFooter) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .compress(true)
                    .maxSelectNum(MAX_IMAGE - mAdapter.getData().size())
                    .forResult(RequestCode.Common.PIKE_IMAGE);
        } else if (v == flVideo && TextUtils.isEmpty(mVideoPath)) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofVideo())
                    .maxSelectNum(1)
                    .forResult(RequestCode.Common.PIKE_VIDEO);
        } else if (v == ivPlay && !TextUtils.isEmpty(mVideoPath)) {
            PictureSelector.create(this).externalPictureVideo(mVideoPath);
        } else if (v == ivDelete) {
            mVideoPath = "";
            ivThumb.setImageDrawable(null);
            ivDelete.setVisibility(View.GONE);
        } else if (v == tvReportType) {
            //选取事件类型
            opvReportType.show();
            KeyboardUtils.hideSoftInput(_mActivity);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.Common.PIKE_IMAGE:
                    // 图片、视频、音频选择结果回调
                    mAdapter.addData(PictureSelector.obtainMultipleResult(data));
                    if (mAdapter.getData().size() == MAX_IMAGE) {
                        mAdapter.removeAllFooterView();
                    }
                    break;
                case RequestCode.Common.PIKE_VIDEO:
                    // 图片、视频、音频选择结果回调
                    LocalMedia media = PictureSelector.obtainMultipleResult(data).get(0);
                    String path = media.getPath();
                    if (media.getDuration() < 3000) {
                        ToastUtil.showToast("视频大小不能小于3秒");
                        return;
                    } else if (media.getDuration() > 15000) {
                        VideoTrimmerActivity.call(this, path, RequestCode.Common.VIDEO_TRIMMER);
                    } else {
                        mViewModel.compressVideo(path);

                    }
                    break;
                case RequestCode.Common.VIDEO_TRIMMER:
                    // 视频裁剪回调
                    String trimPath = data.getData().toString();
                    mViewModel.compressVideo(trimPath);
                    break;
            }
        }
    }

    @Override
    public Class<FeedbackViewModel> onBindViewModel() {
        return FeedbackViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(Application.getInstance());
    }

    @Override
    public void initViewObservable() {

        mViewModel.getCompressLiveEvent().observe(this, (desPath) -> {

                mVideoPath = desPath;
                Glide.with(FeedbckFragment.this).load(desPath).into(ivThumb);
                ivDelete.setVisibility(View.VISIBLE);

        });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        mAdapter.remove(position);
        if (mAdapter.getFooterLayoutCount() == 0) {

            mAdapter.addFooterView(ivFooter);
        }
    }
}

