package com.gykj.zhumulangma.common.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by 10719
 * on 2019/6/19
 */
public class TFragmentPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> pages;
    private final SparseArrayCompat<WeakReference<Fragment>> holder;

    public TFragmentPagerAdapter(FragmentManager fm, List<Fragment> pages) {
        super(fm);
        this.pages = pages;
        this.holder = new SparseArrayCompat<>(pages.size());
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        if (item instanceof Fragment) {
            holder.put(position, new WeakReference<Fragment>((Fragment) item));
        }
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        holder.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getPage(int position) {
        final WeakReference<Fragment> weakRefItem = holder.get(position);
        return (weakRefItem != null) ? weakRefItem.get() : null;
    }
}