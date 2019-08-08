package com.ximalaya.ting.android.opensdk.test.download;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.test.fragment.base.BaseFragment;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.ComparatorUtil;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.BaseRuntimeException;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import org.xutils.x;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by le.xin on 2016/9/14.
 */
public class DownloadedFragment extends BaseFragment implements IXmDownloadTrackCallBack {
    private ListView listView;

    private Button button1;
    private Button button2;
    ProgressDialog progressDialog;
    private XmDownloadManager downloadManager;

    private DownloadListAdapter adapter;
    boolean isSortMode = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_list ,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) findViewById(R.id.list);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
        progressDialog =  new ProgressDialog(getActivity());

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        downloadManager = XmDownloadManager.getInstance();

        adapter = new DownloadListAdapter(downloadManager.getDownloadTracks(true));
        listView.setAdapter(adapter);

        button1.setText("进入排序模式");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSortMode = !isSortMode;
                if(isSortMode) {
                    button1.setText("退出排序并保存排序结果");
                } else {
                    button1.setText("进入排序模式");
                }
                Map<Long ,Integer> swap = new HashMap<Long, Integer>();
                int position = 0;
                for (Track track : adapter.tracks) {
                    swap.put(track.getDataId() ,position++);
                }

                final ProgressDialog progress = new ProgressDialog(getActivity());
                XmDownloadManager.getInstance().swapDownloadedPosition(swap, new IDoSomethingProgress() {
                    @Override
                    public void begin() {
                        progress.show();
                    }

                    @Override
                    public void success() {
                        progress.hide();
                    }

                    @Override
                    public void fail(BaseRuntimeException ex) {
                        progress.hide();
                    }
                });

                adapter.notifyDataSetChanged();
            }
        });

        button2.setText("一键清空");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.clearAllDownloadedTracks(new IDoSomethingProgress() {
                    @Override
                    public void begin() {
                        progressDialog.show();
                    }

                    @Override
                    public void success() {
                        progressDialog.hide();
                    }

                    @Override
                    public void fail(BaseRuntimeException ex) {
                        progressDialog.hide();
                    }
                });
            }
        });

        Button button3 = new Button(getActivity());
        button3.setText("排序类型");
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(adapter.tracks , ComparatorUtil.comparatorByTypeMap(new LinkedHashMap<Integer, Boolean>() {
                    {
                        put(ComparatorUtil.UserSort ,true);
                        put(ComparatorUtil.DownloadOverTime ,true);
                    }
                }));

                adapter.notifyDataSetChanged();
            }
        });
        linearLayout.addView(button3);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                XmPlayerManager.getInstance(getActivity()).playList(adapter.tracks ,position);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(downloadManager == null) {
            downloadManager = XmDownloadManager.getInstance();
        }
        if(isVisibleToUser) {
            downloadManager.addDownloadStatueListener(this);
        } else {
            downloadManager.removeDownloadStatueListener(this);
        }

        if(adapter != null) {
            List<Track> tracks = downloadManager.getDownloadTracks(true);
            adapter.tracks = tracks;
            adapter.notifyDataSetChanged();
        }
    }
    private class DownloadListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private List<Track> tracks;

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final com.ximalaya.ting.android.opensdk.test.data.ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.track_content, parent, false);
                holder = new com.ximalaya.ting.android.opensdk.test.data.ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                holder.downloadStatue = (Button) convertView.findViewById(R.id.downloadstatue);
                holder.downloadStatue.setVisibility(View.VISIBLE);

                convertView.setTag(holder);
            } else {
                holder = (com.ximalaya.ting.android.opensdk.test.data.ViewHolder) convertView.getTag();
            }


            if(isSortMode) {
                holder.downloadStatue.setText("置顶");
            } else {
                holder.downloadStatue.setText("删除");
            }

            final Track track = tracks.get(position);
            holder.title.setText(track.getTrackTitle());
            holder.intro.setText(track.getAnnouncer() == null ? track.getTrackTags() : track.getAnnouncer().getNickname());
            x.image().bind(holder.cover, track.getCoverUrlLarge());

            holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSortMode) {
                        tracks.add(0 ,tracks.remove(position));
                        notifyDataSetChanged();
                    } else {
                        downloadManager.clearDownloadedTrack(track.getDataId());
                    }
                }
            });

            return convertView;
        }
    }

    @Override
    public void onWaiting(Track track) {

    }

    @Override
    public void onStarted(Track track) {

    }

    @Override
    public void onSuccess(Track track) {
        List<Track> tracks = downloadManager.getDownloadTracks(true);
        adapter.tracks = tracks;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Track track, Throwable ex) {

    }

    @Override
    public void onCancelled(Track track, Callback.CancelledException cex) {

    }

    @Override
    public void onProgress(Track track, long total, long current) {

    }

    @Override
    public void onRemoved() {
        List<Track> tracks = downloadManager.getDownloadTracks(true);
        adapter.tracks = tracks;
        adapter.notifyDataSetChanged();
    }
}
