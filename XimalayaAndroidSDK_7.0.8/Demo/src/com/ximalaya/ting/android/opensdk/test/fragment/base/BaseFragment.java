/**
 * BaseFragment.java
 * com.ximalaya.ting.android.opensdk.test
 * <p/>
 * <p/>
 * ver     date      		author
 * ---------------------------------------
 * 2015-6-4 		chadwii
 * <p/>
 * Copyright (c) 2015, chadwii All Rights Reserved.
 */

package com.ximalaya.ting.android.opensdk.test.fragment.base;

import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {

    public void refresh() {
    }


    public View findViewById(int id) {
        return getView().findViewById(id);
    }
}

