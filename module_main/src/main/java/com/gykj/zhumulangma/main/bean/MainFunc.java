package com.gykj.zhumulangma.main.bean;

import android.support.annotation.DrawableRes;

public class MainFunc {
    private @DrawableRes
    int cover;
    private String title;

    public MainFunc() {
    }

    public MainFunc(int cover, String title) {
        this.cover = cover;
        this.title = title;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
