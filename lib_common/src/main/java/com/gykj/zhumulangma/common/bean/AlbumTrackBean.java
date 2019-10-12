package com.gykj.zhumulangma.common.bean;


import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;

/**
 * Author: Thomas.
 * <br/>Date: 2019/7/12 11:21
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:专辑详情列表项
 */
public class AlbumTrackBean {

    private DownloadState downloadState;
    /**
     * 是否正在播放
     */
    private boolean isPlaying;
    /**
     * 已播放百分比
     */
    private float playcent;
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

    public float getPlaycent() {
        return playcent;
    }

    public void setPlaycent(float playcent) {
        this.playcent = playcent;
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
