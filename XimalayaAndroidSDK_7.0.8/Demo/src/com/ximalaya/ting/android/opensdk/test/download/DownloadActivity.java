package com.ximalaya.ting.android.opensdk.test.download;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import android.view.Window;

import com.app.test.android.R;

public class DownloadActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private PagerTabStrip mIndicator;
    private PagerAdapter mAdapter;

    private static final String[] CONTENT = new String[]{"下载中", "已下载", "已下载专辑" ,"下载中按专辑分"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fra_view_page);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (PagerTabStrip) findViewById(R.id.indicator);

        mViewPager.setOffscreenPageLimit(2);
        mIndicator.setTabIndicatorColor(Color.RED);
        mIndicator.setTextColor(Color.RED);

        mAdapter = new MyPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
    }

    class MyPageAdapter extends FragmentPagerAdapter {
        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if(i == 0) {
                return new DownloadingFragment();
            } else if(i == 1) {
                return new DownloadedFragment();
            } else if(i == 2) {
                return new DownloadAlbumFragment();
            } else if(i == 3) {
                return  new DownloadAlbumAndTrackFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

}
