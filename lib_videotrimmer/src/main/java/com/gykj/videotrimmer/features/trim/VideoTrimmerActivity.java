package com.gykj.videotrimmer.features.trim;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.gykj.videotrimmer.R;
import com.gykj.videotrimmer.features.common.ui.BaseActivity;
import com.gykj.videotrimmer.features.compress.VideoCompressor;
import com.gykj.videotrimmer.interfaces.VideoTrimListener;
import com.gykj.videotrimmer.widget.VideoTrimmerView;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerActivity extends BaseActivity implements VideoTrimListener {

  private static final String TAG = "jason";
  private static final String VIDEO_PATH_KEY = "video-file-path";
  private static final String COMPRESSED_VIDEO_FILE_NAME = "compress.mp4";
  public static final int VIDEO_TRIM_REQUEST_CODE = 0x001;
  private VideoTrimmerView mTrimmerView;

  private ProgressDialog mProgressDialog;
  VideoCompressor.VideoCompressTask videoCompressTask;
  public static void call(Fragment from, String videoPath, int code) {
    if (!TextUtils.isEmpty(videoPath)) {
      Bundle bundle = new Bundle();
      bundle.putString(VIDEO_PATH_KEY, videoPath);
      Intent intent = new Intent(from.getActivity(), VideoTrimmerActivity.class);
      intent.putExtras(bundle);
      from.startActivityForResult(intent, code);
    }
  }

  public static void call(Activity from, String videoPath, int code) {
    if (!TextUtils.isEmpty(videoPath)) {
      Bundle bundle = new Bundle();
      bundle.putString(VIDEO_PATH_KEY, videoPath);
      Intent intent = new Intent(from, VideoTrimmerActivity.class);
      intent.putExtras(bundle);
      from.startActivityForResult(intent, code);
    }
  }
  @Override
  public void initUI() {
    setContentView(R.layout.activity_video_trim);
    mTrimmerView=findViewById(R.id.trimmer_view);
    Bundle bd = getIntent().getExtras();
    String path = "";
    if (bd != null) path = bd.getString(VIDEO_PATH_KEY);
    if (mTrimmerView != null) {
      mTrimmerView.setOnTrimVideoListener(this);
      mTrimmerView.initVideoByURI(Uri.parse(path));
    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mTrimmerView.onVideoPause();
    mTrimmerView.setRestoreState(true);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(videoCompressTask!=null){
      videoCompressTask.cancel(true);
    }
    mTrimmerView.onDestroy();
  }

  @Override
  public void onStartTrim() {
//    buildDialog(getResources().getString(R.string.trimming)).show();
  }

  @Override
  public void onFinishTrim(String in) {
    Intent intent = getIntent();
    intent.setData(Uri.parse(in));
    setResult(RESULT_OK, intent);
    finish();
    /*
    String out = StorageUtil.getCacheDir() + File.separator + System.currentTimeMillis()+".mp4";
    new File(out).delete();
    videoCompressTask = VideoCompressor.compressVideoLow(in, out, new VideoCompressor.CompressListener() {
              @Override
              public void onStart() {
                buildDialog(getResources().getString(R.string.compressing)).show();
              }

              @Override
              public void onSuccess() {
                new File(in).delete();
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                Intent intent = getIntent();
                intent.setData(Uri.parse(out));
                setResult(RESULT_OK, intent);
                finish();
              }

              @Override
              public void onFail() {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
              }

              @Override
              public void onProgress(float percent) {

              }
            }
    );*/
  }

  @Override
  public void onCancel() {
    mTrimmerView.onDestroy();
    finish();
  }

  private ProgressDialog buildDialog(String msg) {
    if (mProgressDialog == null) {
      mProgressDialog = ProgressDialog.show(this, "", msg);
      mProgressDialog.setCancelable(true);
    }
    mProgressDialog.setMessage(msg);
    return mProgressDialog;
  }
}
