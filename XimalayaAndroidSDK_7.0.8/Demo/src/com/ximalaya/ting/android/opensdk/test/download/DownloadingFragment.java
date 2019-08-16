package com.ximalaya.ting.android.opensdk.test.download;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.test.fragment.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.util.Logger;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.BaseRuntimeException;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import org.xutils.x;

import java.util.List;

/**
 * Created by le.xin on 2016/9/14.
 */
public class DownloadingFragment extends BaseFragment implements IXmDownloadTrackCallBack {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_list ,null);
    }
    private ListView listView;
    private XmDownloadManager downloadManager;

    private Button button1;
    private Button button2;
    ProgressDialog progressDialog;

    private boolean isDoNotifi; // 是否处理回调

    private DownloadListAdapter downloadListAdapter;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.listView = (ListView) findViewById(R.id.list);
        downloadManager = XmDownloadManager.getInstance();
        downloadListAdapter = new DownloadListAdapter(downloadManager.getDownloadTracks(isDownloaded));
        listView.setAdapter(downloadListAdapter);
        progressDialog =  new ProgressDialog(getActivity());

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        button1.setText("全部继续");
        button2.setText("一键清空");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("全部继续".equals(button1.getText())) {
                    downloadManager.resumeAllDownloads(new IDoSomethingProgress() {
                        @Override
                        public void begin() {
                            isDoNotifi = false;
                            progressDialog.show();
                        }

                        @Override
                        public void success() {
                            isDoNotifi = true;
                            downloadListAdapter.notifyDataSetChanged();

                            progressDialog.hide();

                            updateButtonContent();
                        }

                        @Override
                        public void fail(BaseRuntimeException ex) {
                            isDoNotifi = true;
                            downloadListAdapter.notifyDataSetChanged();

                            progressDialog.hide();
                            Toast.makeText(getActivity(), "失败了 " + ex.getMessage(), Toast.LENGTH_SHORT).show();

                            updateButtonContent();
                        }
                    });
                } else {
                    downloadManager.pauseAllDownloads(new IDoSomethingProgress() {
                        @Override
                        public void begin() {
                            isDoNotifi = false;
                            progressDialog.show();
                        }

                        @Override
                        public void success() {
                            isDoNotifi = true;
                            downloadListAdapter.notifyDataSetChanged();

                            progressDialog.hide();

                            updateButtonContent();
                        }

                        @Override
                        public void fail(BaseRuntimeException ex) {
                            isDoNotifi = true;
                            downloadListAdapter.notifyDataSetChanged();

                            progressDialog.hide();
                            Toast.makeText(getActivity(), "失败了 " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.cancelAllDownloads(new IDoSomethingProgress() {
                    @Override
                    public void begin() {
                        isDoNotifi = false;
                        progressDialog.show();
                    }

                    @Override
                    public void success() {
                        isDoNotifi = true;
                        progressDialog.hide();

                        onRemoved();
                    }

                    @Override
                    public void fail(BaseRuntimeException ex) {
                        isDoNotifi = true;
                        progressDialog.hide();
                        Toast.makeText(getActivity(), "失败了 " + ex.getMessage(), Toast.LENGTH_SHORT).show();

                        onRemoved();
                    }
                });
            }
        });

        updateButtonContent();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(downloadManager == null) {
            downloadManager = XmDownloadManager.getInstance();
        }

        if(isVisibleToUser) {
            updateButtonContent();

            if(downloadListAdapter != null) {
                List<Track> tracks = downloadManager.getDownloadTracks(false);
                downloadListAdapter.tracks = tracks;
                downloadListAdapter.notifyDataSetChanged();
            }
        }

        if(isVisibleToUser) {
            downloadManager.addDownloadStatueListener(this);
        } else {
            downloadManager.removeDownloadStatueListener(this);
        }
    }

    private class DownloadListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private List<Track> tracks;

        public void setTracks(List<Track> tracks) {
            this.tracks = tracks;
        }

        private DownloadListAdapter(List<Track> tracks) {
            mInflater = LayoutInflater.from(getActivity());
            this.tracks = tracks;
        }

        @Override
        public int getCount() {
            if(tracks == null) {
                return 0;
            }
            return tracks.size();
        }

        @Override
        public Object getItem(int i) {
            return tracks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return tracks.get(i).getDataId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            DownloadItemViewHolder holder = null;
            Track downloadInfo = tracks.get(i);
            if (view == null) {
                view = mInflater.inflate(R.layout.download_item, null);
                holder = new DownloadItemViewHolder(view, downloadInfo);
                view.setTag(holder);
                holder.refresh();
            } else {
                holder = (DownloadItemViewHolder) view.getTag();
                holder.update(downloadInfo);
            }

            return view;
        }
    }

    public class DownloadItemViewHolder  {
        TextView label;
        TextView state;
        ProgressBar progressBar;
        Button stopBtn ,removeBtn;
        private ImageView imageview;

        private Track downloadInfo;

        public DownloadItemViewHolder(View view, Track downloadInfo) {
            label = (TextView) view.findViewById(R.id.download_label);
            state = (TextView) view.findViewById(R.id.download_state);
            progressBar = (ProgressBar) view.findViewById(R.id.download_pb);
            progressBar.setMax(100);
            stopBtn = (Button) view.findViewById(R.id.download_stop_btn);
            removeBtn = (Button) view.findViewById(R.id.download_remove_btn);
            imageview = (ImageView) view.findViewById(R.id.imageview);

            stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleEvent(v);
                }
            });

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeEvent(v);
                }
            });

            this.downloadInfo = downloadInfo;
            refresh();
        }

        private void toggleEvent(View view) {
            DownloadState state = DownloadState.valueOf(downloadInfo.getDownloadStatus());
            switch (state) {
                case WAITING:
                case STARTED:
                    downloadManager.pauseDownloadSingleTrack(downloadInfo.getDataId());
                    break;
                case ERROR:
                case STOPPED:
                    downloadManager.resumeDownloadSingleTrack(downloadInfo.getDataId());
                    break;
                case FINISHED:
                    Toast.makeText(getActivity(), "已经下载完成", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        private void removeEvent(View view) {
            if(downloadManager.getSingleTrackDownloadStatus(downloadInfo.getDataId()) == DownloadState.FINISHED) {
                downloadManager.clearDownloadedTrack(downloadInfo.getDataId());
            } else {
                downloadManager.cancelDownloadSingleTrack(downloadInfo.getDataId());
            }
            downloadListAdapter.notifyDataSetChanged();
        }

        public void update(Track downloadInfo) {
            this.downloadInfo = downloadInfo;
            refresh();
        }

        public void refresh() {
            label.setText(downloadInfo.getTrackTitle());
            state.setText(DownloadState.valueOf(downloadInfo.getDownloadStatus()).toString());
            x.image().bind(imageview, downloadInfo.getCoverUrlLarge());

            if(downloadInfo.getDownloadSize() != 0) {
                int curr = (int) (downloadInfo.getDownloadedSize() * 1.0f / downloadInfo.getDownloadSize()  * 100);
                progressBar.setProgress(curr);
            }

            stopBtn.setVisibility(View.VISIBLE);
            stopBtn.setText(getString(R.string.stop));
            DownloadState state = DownloadState.valueOf(downloadInfo.getDownloadStatus());
            switch (state) {
                case WAITING:
                case STARTED:
                    stopBtn.setText(getString(R.string.stop));
                    break;
                case ERROR:
                case STOPPED:
                    stopBtn.setText(getString(R.string.start));
                    break;
                case FINISHED:
                    stopBtn.setVisibility(View.INVISIBLE);
                    break;
                default:
                    stopBtn.setText(getString(R.string.start));
                    break;
            }
        }
    }

    private void updateButtonContent() {
        if(downloadManager == null || button1 == null) {
            return;
        }
        if(downloadManager.haveDowningTask()) {
            button1.setText("全部暂停");
        } else {
            button1.setText("全部继续");
        }
    }

    private boolean isDownloaded = false;
    @Override
    public void onWaiting(Track track) {
        if(!isDoNotifi) {
            return;
        }

        List<Track> tracks = downloadManager.getDownloadTracks(isDownloaded);
        downloadListAdapter.tracks = tracks;
        downloadListAdapter.notifyDataSetChanged();

        updateButtonContent();
    }

    @Override
    public void onStarted(Track track) {
        if(!isDoNotifi) {
            return;
        }

        downloadListAdapter.notifyDataSetChanged();
        updateButtonContent();
    }

    @Override
    public void onSuccess(Track track) {
        List<Track> tracks = downloadManager.getDownloadTracks(isDownloaded);
        downloadListAdapter.tracks = tracks;
        downloadListAdapter.notifyDataSetChanged();

        updateButtonContent();
    }

    @Override
    public void onError(Track track, Throwable ex) {
        if(!isDoNotifi) {
            return;
        }

        Logger.log("DownloadActivity :  出错了  " + ex);
        downloadListAdapter.notifyDataSetChanged();

        updateButtonContent();
    }

    @Override
    public void onCancelled(Track track, Callback.CancelledException cex) {
        if(!isDoNotifi) {
            return;
        }
        downloadListAdapter.notifyDataSetChanged();

        updateButtonContent();
    }

    @Override
    public void onProgress(Track track, long total, long current) {
        downloadListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemoved() {
        if(!isDoNotifi) {
            return;
        }
        List<Track> tracks = downloadManager.getDownloadTracks(isDownloaded);
        downloadListAdapter.tracks = tracks;
        downloadListAdapter.notifyDataSetChanged();

        updateButtonContent();
    }
}
