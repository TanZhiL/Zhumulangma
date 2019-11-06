package com.gykj.zhumulangma.common.bean;

import com.gykj.zhumulangma.common.mvvm.view.SupportFragment;

import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/16 14:34
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class NavigateBean {
   public String path;
   public SupportFragment fragment;
   public ExtraTransaction extraTransaction;
   public @ISupportFragment.LaunchMode int  launchMode=ISupportFragment.SINGLETASK;

    public NavigateBean(String path, SupportFragment fragment) {
        this.path = path;
        this.fragment = fragment;
    }

    public NavigateBean(String path, SupportFragment fragment,ExtraTransaction extraTransaction) {
        this.path = path;
        this.fragment = fragment;
        this.extraTransaction = extraTransaction;
    }

    @Override
    public String toString() {
        return path;
    }
}
