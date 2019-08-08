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
import android.widget.TextView;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.test.data.ViewHolder;
import com.ximalaya.ting.android.opensdk.test.fragment.base.BaseFragment;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.BaseRuntimeException;
import com.ximalaya.ting.android.sdkdownloader.model.XmDownloadAlbum;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import org.xutils.x;

import java.util.List;

/**
 * Created by le.xin on 2016/9/14.
 */
public class DownloadAlbumFragment extends BaseFragment implements IXmDownloadTrackCallBack {

    private ListView listView;

    private Button button1;
    private Button button2;
    ProgressDialog progressDialog;
    private XmDownloadManager downloadManager;

    private DownloadListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_list ,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) findViewById(R.id.list);
        progressDialog =  new ProgressDialog(getActivity());

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        downloadManager = XmDownloadManager.getInstance();

        adapter = new DownloadListAdapter(downloadManager.getDownloadAlbums(true));
        listView.setAdapter(adapter);

        button1.setVisibility(View.GONE);
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
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(downloadManager == null) {
            downloadManager = XmDownloadManager.getInstance();
        }
        if(isVisibleToUser) {
            downloadManager.addDownloadStatueListener(this);

            if(adapter != null) {
                List<XmDownloadAlbum> albumList = downloadManager.getDownloadAlbums(true);
                adapter.albumList = albumList;
                adapter.notifyDataSetChanged();
            }
        } else {
            downloadManager.removeDownloadStatueListener(this);
        }
    }

    private class DownloadListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private List<XmDownloadAlbum> albumList;

        private DownloadListAdapter(List<XmDownloadAlbum> tracks) {
            mInflater = LayoutInflater.from(getActivity());
            this.albumList = tracks;
        }

        @Override
        public int getCount() {
            if(albumList == null) {
                return 0;
            }
            return albumList.size();
        }

        @Override
        public Object getItem(int i) {
            return albumList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return albumList.get(i).getAlbumId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.track_content, parent, false);
                holder = new com.ximalaya.ting.android.opensdk.test.data.ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                holder.downloadStatue = (Button) convertView.findViewById(R.id.downloadstatue);
                holder.downloadStatue.setVisibility(View.VISIBLE);
                holder.downloadStatue.setText("删除");

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final XmDownloadAlbum album = albumList.get(position);
            holder.title.setText(album.getAlbumTitle());
            holder.intro.setText("已下载 " + album.getTrackCount() + "个");
            x.image().bind(holder.cover, album.getCoverUrlLarge());

            holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadManager.clearDownloadedAlbum(album.getAlbumId(), new IDoSomethingProgress() {
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
        List<XmDownloadAlbum> albumList = downloadManager.getDownloadAlbums(true);
        adapter.albumList = albumList;
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
        List<XmDownloadAlbum> albumList = downloadManager.getDownloadAlbums(true);
        adapter.albumList = albumList;
        adapter.notifyDataSetChanged();
    }

}
