package com.ximalaya.ting.android.opensdk.test.download;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.test.data.ViewHolder;
import com.ximalaya.ting.android.opensdk.test.fragment.base.BaseFragment;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IXmDownloadTrackCallBack;
import com.ximalaya.ting.android.sdkdownloader.exception.BaseRuntimeException;
import com.ximalaya.ting.android.sdkdownloader.model.XmDownloadAlbum;
import com.ximalaya.ting.android.sdkdownloader.model.XmDownloadAlbumHaveTracks;
import com.ximalaya.ting.android.sdkdownloader.task.Callback;

import org.xutils.x;

import java.util.List;

/**
 * Created by le.xin on 2016/9/14.
 */
public class DownloadAlbumAndTrackFragment extends BaseFragment implements IXmDownloadTrackCallBack {
    private ExpandableListView list;
    private Button button1;
    private Button button2;
    ProgressDialog progressDialog;
    private XmDownloadManager downloadManager;
    private DownloadAdapter adapter;
    private LayoutInflater mInflater;

    private List<XmDownloadAlbumHaveTracks> albumHaveTrackses;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.download_album_and_track ,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        downloadManager = XmDownloadManager.getInstance();

        list = (ExpandableListView) findViewById(R.id.list);
        mInflater = LayoutInflater.from(getActivity());

        albumHaveTrackses = downloadManager.getDownloadListByAlbum(false);
        adapter = new DownloadAdapter();
        list.setAdapter(adapter);

        for (int i = 0; i < albumHaveTrackses.size(); i++) {
            list.expandGroup(i);
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("全部继续".equals(button1.getText())) {
                    downloadManager.resumeAllDownloads(new IDoSomethingProgress() {
                        @Override
                        public void begin() {
                            progressDialog.show();
                        }

                        @Override
                        public void success() {
                            progressDialog.hide();

                            updateButtonContent();
                        }

                        @Override
                        public void fail(BaseRuntimeException ex) {
                            progressDialog.hide();
                            Toast.makeText(getActivity(), "失败了 " + ex.getMessage(), Toast.LENGTH_SHORT).show();

                            updateButtonContent();
                        }
                    });
                } else {
                    downloadManager.pauseAllDownloads(new IDoSomethingProgress() {
                        @Override
                        public void begin() {
                            progressDialog.show();
                        }

                        @Override
                        public void success() {
                            progressDialog.hide();

                            updateButtonContent();
                        }

                        @Override
                        public void fail(BaseRuntimeException ex) {
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
                        progressDialog.show();
                    }

                    @Override
                    public void success() {
                        progressDialog.hide();
                    }

                    @Override
                    public void fail(BaseRuntimeException ex) {
                        progressDialog.hide();
                        Toast.makeText(getActivity(), "失败了 " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public class DownloadAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            if(albumHaveTrackses != null) {
                return albumHaveTrackses.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return albumHaveTrackses.get(groupPosition).getTracks().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return albumHaveTrackses.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return albumHaveTrackses.get(groupPosition).getTracks().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return albumHaveTrackses.get(groupPosition).getAlbum().getAlbumId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return albumHaveTrackses.get(groupPosition).getTracks().get(childPosition).getDataId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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
                holder.pause = (Button) convertView.findViewById(R.id.pause);
                holder.pause.setVisibility(View.VISIBLE);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final XmDownloadAlbum album = albumHaveTrackses.get(groupPosition).getAlbum();
            final boolean havedownload = downloadManager.albumHaveDownloadingTrack(album.getAlbumId());
            holder.pause.setText(havedownload ? "暂停" : "继续");
            holder.pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(havedownload) {
                        downloadManager.pauseDownloadTracksInAlbum(album.getAlbumId(), new IDoSomethingProgress() {
                            @Override
                            public void begin() {
                                progressDialog.show();
                            }

                            @Override
                            public void success() {
                                progressDialog.hide();
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void fail(BaseRuntimeException ex) {
                                progressDialog.hide();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        downloadManager.resumeDownloadTracksInAlbum(album.getAlbumId(), new IDoSomethingProgress() {
                            @Override
                            public void begin() {
                                progressDialog.show();
                            }

                            @Override
                            public void success() {
                                progressDialog.hide();
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void fail(BaseRuntimeException ex) {
                                progressDialog.hide();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });

            holder.title.setText(album.getAlbumTitle());
            holder.intro.setText("正在下载 " + albumHaveTrackses.get(groupPosition).getTracks().size() + "个");
            x.image().bind(holder.cover, album.getCoverUrlLarge());

            holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadManager.cancelDownloadTracksInAlbum(album.getAlbumId(), new IDoSomethingProgress() {
                        @Override
                        public void begin() {
                            progressDialog.show();
                        }

                        @Override
                        public void success() {
                            progressDialog.hide();

                            albumHaveTrackses = downloadManager.getDownloadListByAlbum(false);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void fail(BaseRuntimeException ex) {
                            progressDialog.hide();

                            albumHaveTrackses = downloadManager.getDownloadListByAlbum(false);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });


            convertView.setBackgroundColor(ContextCompat.getColor(getContext() ,android.R.color.darker_gray));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
            final Track downloadInfo = albumHaveTrackses.get(groupPosition).getTracks().get(childPosition);
            ChildViewHolder childViewHolder;

            if (view == null) {
                view = mInflater.inflate(R.layout.download_item, null);
                childViewHolder = new ChildViewHolder();
                childViewHolder.label = (TextView) view.findViewById(R.id.download_label);
                childViewHolder.state = (TextView) view.findViewById(R.id.download_state);
                childViewHolder.progressBar = (ProgressBar) view.findViewById(R.id.download_pb);
                childViewHolder.progressBar.setMax(100);

                childViewHolder.stopBtn = (Button) view.findViewById(R.id.download_stop_btn);
                childViewHolder.removeBtn = (Button) view.findViewById(R.id.download_remove_btn);
                childViewHolder.imageview = (ImageView) view.findViewById(R.id.imageview);

                view.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) view.getTag();
            }

            childViewHolder.stopBtn.setVisibility(View.VISIBLE);
            childViewHolder.stopBtn.setText(getString(R.string.stop));
            DownloadState state = DownloadState.valueOf(downloadInfo.getDownloadStatus());
            switch (state) {
                case WAITING:
                case STARTED:
                    childViewHolder.stopBtn.setText(getString(R.string.stop));
                    break;
                case ERROR:
                case STOPPED:
                    childViewHolder.stopBtn.setText(getString(R.string.start));
                    break;
                case FINISHED:
                    childViewHolder.stopBtn.setVisibility(View.INVISIBLE);
                    break;
                default:
                    childViewHolder.stopBtn.setText(getString(R.string.start));
                    break;
            }

            childViewHolder.stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            });

            childViewHolder.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(downloadManager.getSingleTrackDownloadStatus(downloadInfo.getDataId()) == DownloadState.FINISHED) {
                        downloadManager.clearDownloadedTrack(downloadInfo.getDataId());
                    } else {
                        downloadManager.cancelDownloadSingleTrack(downloadInfo.getDataId());
                    }
                    adapter.notifyDataSetChanged();
                }
            });

            childViewHolder.label.setText(downloadInfo.getTrackTitle());
            childViewHolder.state.setText(DownloadState.valueOf(downloadInfo.getDownloadStatus()).toString());
            x.image().bind(childViewHolder.imageview, downloadInfo.getCoverUrlLarge());

            if(downloadInfo.getDownloadSize() != 0) {
                int curr = (int) (downloadInfo.getDownloadedSize() * 1.0f / downloadInfo.getDownloadSize()  * 100);
                childViewHolder.progressBar.setProgress(curr);
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


    class ChildViewHolder {
        TextView label;
        TextView state;
        ProgressBar progressBar;
        Button stopBtn ,removeBtn;
        ImageView imageview;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(downloadManager == null) {
            downloadManager = XmDownloadManager.getInstance();
        }
        if(isVisibleToUser) {
            if(adapter != null) {
                albumHaveTrackses = downloadManager.getDownloadListByAlbum(false);
                adapter.notifyDataSetChanged();
            }

            updateButtonContent();
        }

        if(isVisibleToUser) {
            downloadManager.addDownloadStatueListener(this);
        } else {
            downloadManager.removeDownloadStatueListener(this);
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

    @Override
    public void onWaiting(Track track) {

        updateButtonContent();
    }

    @Override
    public void onStarted(Track track) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(Track track) {
        albumHaveTrackses = downloadManager.getDownloadListByAlbum(false);
        adapter.notifyDataSetChanged();

        updateButtonContent();
    }

    @Override
    public void onError(Track track, Throwable ex) {

        updateButtonContent();
    }

    @Override
    public void onCancelled(Track track, Callback.CancelledException cex) {
        adapter.notifyDataSetChanged();

        updateButtonContent();
    }

    @Override
    public void onProgress(Track track, long total, long current) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRemoved() {
        albumHaveTrackses = downloadManager.getDownloadListByAlbum(false);
        adapter.notifyDataSetChanged();

        updateButtonContent();
    }
}