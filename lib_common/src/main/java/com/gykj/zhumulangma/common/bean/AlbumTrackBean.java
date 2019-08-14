package com.gykj.zhumulangma.common.bean;


import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;

/**
 * Author: Thomas.
 * Date: 2019/7/12 11:21
 * Email: 1071931588@qq.com
 * Description:专辑详情列表项
 */
public class AlbumTrackBean {

    private DownloadState downloadState;
    /**
     * 是否正在播放
     */
    private boolean isPlaying;
    /**
     * 是否正在下载
     */
    private boolean isDownloading;
    /**
     * 是否下载完毕
     */
    private boolean isDownload;
    /**
     * 声音对象
     */
    private Track track;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track trace) {
        this.track = trace;
    }

    public DownloadState getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState;
    }
}
