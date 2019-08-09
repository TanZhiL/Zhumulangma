package com.gykj.zhumulangma.discover.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.CommonModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;
import com.gykj.zhumulangma.common.net.dto.TaskFeedbackDTO;
import com.gykj.zhumulangma.common.util.FileUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.videotrimmer.features.compress.VideoCompressor;
import com.gykj.zhumulangma.discover.mvvm.model.FeedbackModel;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Author: Thomas.
 * Date: 2019/7/30 9:25
 * Email: 1071931588@qq.com
 * Description:
 */
public class FeedbackViewModel extends BaseViewModel<FeedbackModel> {
    private SingleLiveEvent<String> mCompressLiveEvent;
    private CommonModel mCommonModel;

    public FeedbackViewModel(@NonNull Application application, FeedbackModel model, CommonModel commonModel) {
        super(application, model);
        mCommonModel = commonModel;
    }

    public void _feedback(String report_type, String solve_result, List<LocalMedia> mediaList, final String videoPath) {
        if (invalid(report_type, solve_result, mediaList))
            return;
        List<String> cacheFiles=new ArrayList<>();

        TaskFeedbackDTO repotDTO = new TaskFeedbackDTO();
        repotDTO.setTask_type(report_type);
        repotDTO.setTask_solve_result(solve_result);

        MultipartBody.Part[] parts = new MultipartBody.Part[mediaList.size()];
        for (int i = 0; i < mediaList.size(); i++) {
            File file = new File(mediaList.get(i).getCompressPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            parts[i] = filePart;
            cacheFiles.add(mediaList.get(i).getCompressPath());
        }
        //0.任务列表
        List<Observable> observables = new ArrayList<>();
        //1.上传图片
        observables.add(mCommonModel.uploadFiles(parts).doOnSubscribe(FeedbackViewModel.this).doOnNext(listResponseDTO ->
                repotDTO.setTask_solve_file(listResponseDTO.result)

        ));
        //判断是否有视频文件
        if (!TextUtils.isEmpty(videoPath)) {
            File file = new File(videoPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file",
                    file.getName(), requestFile);
            cacheFiles.add(videoPath);
            //2.上传视频
            observables.add(mCommonModel.uploadFile(filePart).doOnSubscribe(FeedbackViewModel.this).doOnNext(uploadFileBeanResponseDTO ->
                    repotDTO.setTask_solve_video(uploadFileBeanResponseDTO.result)));
        }

        Observable.zipArray((Function<Object[], Object>) objects -> 0,false,1,observables.toArray(new Observable[]{}))
                //3.上传数据
                .flatMap((Function<Object, ObservableSource<?>>) o -> mModel.feedback(repotDTO))
                //4.清空缓存文件
                .flatMap(o -> Observable.create(emitter -> {
                    for (int i = 0; i < mediaList.size(); i++) {
                        new File(mediaList.get(i).getCompressPath()).delete();
                    }
                    if(!TextUtils.isEmpty(videoPath)){
                        new File(videoPath).delete();
                    }
                    emitter.onNext(0);
                }))
                .compose(upstream -> Observable.defer(()->upstream))
                .doOnSubscribe(d -> postShowTransLoadingViewEvent("上传中..."))
                .doFinally(() -> postShowTransLoadingViewEvent(null))
                .subscribe(o -> {
                    postFinishSelfEvent();
                    FileUtil.deleteFiles(cacheFiles);
                    ToastUtil.showToast("上传成功");
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    VideoCompressor.VideoCompressTask videoCompressTask;
    public void compressVideo(String in) {
        String desPath = getApplication().getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".mp4";
        videoCompressTask = VideoCompressor.compressVideoLow(in, desPath, new VideoCompressor.CompressListener() {
            @Override
            public void onStart() {
                postShowTransLoadingViewEvent("视频压缩中...");
            }

            @Override
            public void onSuccess() {
                postShowTransLoadingViewEvent(null);
                getCompressLiveEvent().postValue(desPath);
            }

            @Override
            public void onFail() {
                postShowTransLoadingViewEvent(null);
                ToastUtil.showToast("视频解压失败");
            }

            @Override
            public void onProgress(float percent) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=videoCompressTask){
            videoCompressTask.cancel(true);
        }
    }


    private boolean invalid(String report_type, String solve_result, List<LocalMedia> images) {

        if (TextUtils.isEmpty(report_type.trim())) {
            ToastUtil.showToast("请选择事件类型");
            return true;
        }
        if (TextUtils.isEmpty(solve_result.trim())) {
            ToastUtil.showToast("请输入处理结果");
            return true;
        }
        if (images.size() == 0) {
            ToastUtil.showToast("请至少选取一张图片");
            return true;
        }
        return false;
    }


    public SingleLiveEvent<String> getCompressLiveEvent() {
        return mCompressLiveEvent = createLiveData(mCompressLiveEvent);
    }
}
