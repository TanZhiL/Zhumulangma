package com.ximalaya.ting.android.opensdk.test.download;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.test.data.ViewHolder;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.AddDownloadException;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by le.xin on 2016/9/13.
 */
public class BatchDownloadPayActivity extends Activity implements IXmDownloadTrackCallBack {
    private ListView listView;

    private LinearLayout linearLayout;

    private Button checkNumButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fra_list);

        this.listView = (ListView) findViewById(R.id.list);
        linearLayout = (LinearLayout) findViewById(R.id.linear);

        checkNumButton = new Button(this);
        checkNumButton.setText("全选");
        checkNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("全选".equals(checkNumButton.getText())) {
                    for (Track track : rankTrackList.getTracks()) {
                        if(longDownloadStateMap.get(track.getDataId()) == DownloadState.NOADD) {
                            checkTrackMap.put(track.getDataId() ,true);
                        }
                    }
                } else {
                    checkTrackMap.clear();
                }

                downloadTrackAdapter.notifyDataSetChanged();
            }
        });
        linearLayout.addView(checkNumButton);

        Button button = new Button(this);
        button.setText("下载已选");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pro = new ProgressDialog(BatchDownloadPayActivity.this);

                XmDownloadManager.getInstance().downloadPayTracks(new ArrayList<Long>(checkTrackMap.keySet()),true, new IDoSomethingProgress<AddDownloadException>() {
                    @Override
                    public void begin() {
                        pro.show();
                    }

                    @Override
                    public void success() {
                        pro.hide();
                    }

                    @Override
                    public void fail(AddDownloadException ex) {
                        pro.hide();
                    }
                });
            }
        });
        linearLayout.addView(button);

        downloadTrackAdapter = new BatchDownloadTrackAdapter(BatchDownloadPayActivity.this ,null);
        listView.setAdapter(downloadTrackAdapter);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BatchDownloadPayActivity.this ,DownloadActivity.class));
            }
        });

        Button viewById = (Button) findViewById(R.id.button2);
        viewById.setText("下一页");
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rankTrackList == null || rankTrackList.getCurrentPage() < rankTrackList.getTotalPage()) {
                    loadData();
                }
            }
        });

        loadData();
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

    private int mPageId = 0;
    private TrackList rankTrackList;

    private int noAddNum = 0;
    Map<Long, DownloadState> longDownloadStateMap;

    private void loadData() {
        Map<String ,String> map = new HashMap<>();
        map.put("album_id" ,"8225829");
        map.put("page" , ++mPageId + "");
        map.put("count" ,50 + "");
        CommonRequest.getPaidTrackByAlbum(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList object) {
                BatchDownloadPayActivity.this.rankTrackList = object;
                downloadTrackAdapter.setData(rankTrackList.getTracks());

                List<Long> trackIds = new ArrayList<Long>();
                for (Track track : rankTrackList.getTracks()) {
                    trackIds.add(track.getDataId());
                }

                noAddNum = 0;

                checkTrackMap.clear();

                longDownloadStateMap = XmDownloadManager.getInstance().batchGetTracksDownloadStatus(trackIds);
                for (DownloadState downstate : longDownloadStateMap.values()) {
                    if(downstate == DownloadState.NOADD) {
                        noAddNum++;
                    }
                }
            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }

    private BatchDownloadTrackAdapter downloadTrackAdapter;
    private void notif() {
        if(downloadTrackAdapter != null) {
            downloadTrackAdapter.notifyDataSetChanged();
        }

        if(checkTrackMap.size() == noAddNum) {
            checkNumButton.setText("全不选");
        } else {
            checkNumButton.setText("全选");
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

    private Map<Long ,Boolean> checkTrackMap = new HashMap<>();

    private class BatchDownloadTrackAdapter extends BaseAdapter {
        List<Track> tracks;
        Context context;
        LayoutInflater layoutInflater;


        public BatchDownloadTrackAdapter(Context context, List<Track> tracks) {
            this.tracks = tracks;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Track> tracks) {
            this.tracks = tracks;
            notifyDataSetChanged();
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
                holder.downloadStatue.setVisibility(View.GONE);

                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                holder.checkBox.setVisibility(View.VISIBLE);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Track track = tracks.get(position);
            holder.title.setText(track.getTrackTitle());
            holder.intro.setText(track.getAnnouncer() == null ? track.getTrackTags() : track.getAnnouncer().getNickname());
            x.image().bind(holder.cover, track.getCoverUrlLarge());

            DownloadState downloadState = XmDownloadManager.getInstance().getSingleTrackDownloadStatus(track.getDataId());
            if(downloadState == DownloadState.NOADD) {
                holder.checkBox.setEnabled(true);
                holder.checkBox.setClickable(true);
                holder.checkBox.setFocusable(true);

                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked) {
                            checkTrackMap.remove(track.getDataId());
                        } else {
                            checkTrackMap.put(track.getDataId() ,isChecked);
                        }
                        if(checkTrackMap.size() == noAddNum) {
                            checkNumButton.setText("全不选");
                        } else {
                            checkNumButton.setText("全选");
                        }
                    }
                });

                Boolean flag = checkTrackMap.get(track.getDataId());
                if(flag == null) {
                    holder.checkBox.setChecked(false);
                } else {
                    holder.checkBox.setChecked(flag);
                }
            } else {
                holder.checkBox.setEnabled(false);
                holder.checkBox.setClickable(false);
                holder.checkBox.setFocusable(false);
            }

            return convertView;
        }
    }

}
