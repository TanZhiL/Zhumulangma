package com.gykj.zhumulangma.common.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:通用FragmentPagerAdapter适配器
 */
public class TFragmentStateAdapter extends FragmentStateAdapter {

    private final List<Fragment> pages;

    public TFragmentStateAdapter(Fragment fragment, List<Fragment> pages) {
        super(fragment);
        this.pages = pages;
    }
    public TFragmentStateAdapter(FragmentActivity activity, List<Fragment> pages) {
        super(activity);
        this.pages = pages;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return pages.get(position);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}