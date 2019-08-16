package com.ximalaya.ting.android.opensdk.test.download;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.test.data.ViewHolder;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.ITransferFileProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.AddDownloadException;
import com.ximalaya.ting.android.sdkdownloader.exception.TransferSavePathException;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadTrackActivity extends Activity implements IXmDownloadTrackCallBack {
    private ListView list;

    private List<Track> tracks = new ArrayList<>();
    DownloadTrackAdapter downloadTrackAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fra_list);
        this.list = (ListView) findViewById(R.id.list);
        downloadTrackAdapter = new DownloadTrackAdapter(DownloadTrackActivity.this ,tracks);
        list.setAdapter(downloadTrackAdapter);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DownloadTrackActivity.this ,DownloadActivity.class));
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DownloadTrackActivity.this ,BatchDownloadActivity.class));
            }
        });

        final ProgressDialog download = new ProgressDialog(this);
        findViewById(R.id.button3).setVisibility(View.VISIBLE);
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmDownloadManager.getInstance().changeSavePathAndTransfer(
                        ContextCompat.getExternalCacheDirs(DownloadTrackActivity.this)[0].getAbsolutePath(), new ITransferFileProgress() {
                    @Override
                    public void progress(int cur, int count ,Track track) {
                        download.setProgress((int) (cur * 1.0f / count * 100));
                    }

                    @Override
                    public void begin() {
                        download.show();
                    }

                    @Override
                    public void success() {
                        download.hide();
                    }

                    @Override
                    public void fail(TransferSavePathException ex) {
                        System.out.println("转移失败了   " + ex);
                    }
                });
            }
        });

        loadData();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                XmPlayerManager.getInstance(DownloadTrackActivity.this).playList(tracks , position);
                List<Track> downloadTracks = XmDownloadManager.getInstance().getDownloadTracks(true);
                XmPlayerManager.getInstance(DownloadTrackActivity.this).playList(downloadTracks , 0);
            }
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    mPageId ++;
                    int count = view.getCount();
                    count = count - 5 > 0 ? count - 5 : count - 1;
                    if (view.getLastVisiblePosition() > count && mPageId < rankTrackList.getTotalPage()) {
                        loadData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        XmDownloadManager.getInstance().addDownloadStatueListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        XmDownloadManager.getInstance().removeDownloadStatueListener(this);
    }

    private int mPageId = 1;
    private TrackList rankTrackList;

    private void loadData() {
        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.ALBUM_ID,DTransferConstants.isRelease ? "203355" : "98768");
        param.put(DTransferConstants.PAGE ,mPageId + "");
        CommonRequest.getTracks(param, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList rankTrackList) {
                DownloadTrackActivity.this.rankTrackList = rankTrackList;
                tracks.addAll(rankTrackList.getTracks());
                downloadTrackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void notif() {
        if(downloadTrackAdapter != null) {
            downloadTrackAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onWaiting(Track track) {
        notif();
    }

    @Override
    public void onStarted(Track track) {
        notif();
    }

    @Override
    public void onSuccess(Track track) {
        notif();
    }

    @Override
    public void onError(Track track, Throwable ex) {
        notif();
    }

    @Override
    public void onCancelled(Track track, Callback.CancelledException cex) {
        notif();
    }

    @Override
    public void onProgress(Track track, long total, long current) {
        notif();
    }

    @Override
    public void onRemoved() {
        notif();
    }

    private class DownloadTrackAdapter extends BaseAdapter {
        List<Track> tracks;
        Context context;
        LayoutInflater layoutInflater;
        public DownloadTrackAdapter(Context context, List<Track> tracks) {
            this.tracks = tracks;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if(tracks == null) {
                return 0;
            }
            return tracks.size();
        }

        @Override
        public Track getItem(int position) {
            return tracks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return tracks.get(position).getDataId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.track_content, parent, false);
                holder = new ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                holder.downloadStatue = (Button) convertView.findViewById(R.id.downloadstatue);
                holder.downloadStatue.setVisibility(View.VISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Track track = tracks.get(position);
            holder.title.setText(track.getTrackTitle());
            holder.intro.setText(track.getAnnouncer() == null ? track.getTrackTags() : track.getAnnouncer().getNickname());
            x.image().bind(holder.cover, track.getCoverUrlLarge());
            DownloadState downloadState = XmDownloadManager.getInstance().getSingleTrackDownloadStatus(track.getDataId());
            holder.downloadStatue.setEnabled(true);
            if(downloadState == DownloadState.FINISHED) {
                holder.downloadStatue.setText("已下载");
                holder.downloadStatue.setEnabled(false);
            } else if(downloadState.value() < DownloadState.FINISHED.value()) {
                holder.downloadStatue.setText("正在下载");
                holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmDownloadManager.getInstance().pauseDownloadSingleTrack(track.getDataId());
                    }
                });
            } else if(downloadState == DownloadState.STOPPED) {
                holder.downloadStatue.setText("已暂停");
                holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmDownloadManager.getInstance().resumeDownloadSingleTrack(track.getDataId());
                    }
                });
            } else if(downloadState == DownloadState.ERROR) {
                holder.downloadStatue.setText("下载失败");
                holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmDownloadManager.getInstance().resumeDownloadSingleTrack(track.getDataId());
                    }
                });
            } else if(downloadState == DownloadState.NOADD){
                holder.downloadStatue.setText("开始下载");
                holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmDownloadManager.getInstance().downloadSingleTrack(track.getDataId(), new IDoSomethingProgress<AddDownloadException>() {
                            @Override
                            public void begin() {
                                Toast.makeText(context, "开始", Toast.LENGTH_SHORT).show();
                                holder.downloadStatue.setEnabled(false);
                            }

                            @Override
                            public void success() {
                                Toast.makeText(context, "加入下载成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void fail(AddDownloadException ex) {
                                Toast.makeText(context, "加入下载失败" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            return convertView;
        }
    }
}
