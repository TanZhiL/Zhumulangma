package com.gykj.zhumulangma.common.mvvm.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gykj.zhumulangma.common.mvvm.view.status.BlankStatus;
import com.gykj.zhumulangma.common.mvvm.view.status.EmptyStatus;
import com.gykj.zhumulangma.common.mvvm.view.status.ErrorStatus;
import com.gykj.zhumulangma.common.mvvm.view.status.LoadingStatus;
import com.kingja.loadsir.callback.Callback;

import java.util.List;

/**
 * Author: Thomas.<br/>
 * Date: 2019/10/24 15:25<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description: Activity和Fragment公用方法
 */
public interface BaseView {

    long SHOW_SPACE = 200L;

    @LayoutRes
    int onBindLayout();

    void initCommonView();

    default void initParam() {
    }

    void initView();

    default void initListener() {
    }

    void initData();

    /**
     * 提供状态布局
     *
     * @return
     */
    default Callback getInitStatus() {
        return new BlankStatus();
    }

    default Callback getLoadingStatus() {
        return new LoadingStatus();
    }

    default Callback getErrorStatus() {
        return new ErrorStatus();
    }

    default Callback getEmptyStatus() {
        return new EmptyStatus();
    }

    /**
     * 隐藏软键盘
     */
    default void hideSoftInput() {
        Activity activity = null;
        if (this instanceof Fragment) {
            activity = ((Fragment) this).getActivity();
        } else if (this instanceof Activity) {
            activity = ((Activity) this);
        }
        if (activity == null) return;
        View view = activity.getWindow().getDecorView();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示软键盘,调用该方法后,会在onPause时自动隐藏软键盘
     */
    default void showSoftInput(View view) {
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        view.postDelayed(() -> imm.showSoftInput(view, InputMethodManager.SHOW_FORCED), SHOW_SPACE);
    }

    default Fragment getTopFragment() {
        FragmentManager fm = null;
        if (this instanceof Fragment) {
            fm = ((Fragment) this).getChildFragmentManager();
        } else if (this instanceof Activity) {
            fm = ((FragmentActivity) this).getSupportFragmentManager();
        }
        List<Fragment> fragmentList = fm.getFragments();
        if (fragmentList == null) return null;

        return fragmentList.get(fragmentList.size() - 1);
    }

    default FragmentTransaction beginTransaction() {
        FragmentManager fm = null;
        if (this instanceof Fragment) {
            fm = ((Fragment) this).getChildFragmentManager();
        } else if (this instanceof Activity) {
            fm = ((FragmentActivity) this).getSupportFragmentManager();
        }
        return fm.beginTransaction();
    }

    /**
     * 提供额外状态布局
     *
     * @return
     */
    default @Nullable
    List<Callback> getExtraStatus() {
        return null;
    }

    enum SimpleBarStyle {
        /**
         * 返回图标(默认)
         */
        LEFT_BACK,
        /**
         * 返回图标+文字
         */
        LEFT_BACK_TEXT,
        /**
         * 附加图标
         */
        LEFT_ICON,
        /**
         * 标题(默认)
         */
        CENTER_TITLE,
        /**
         * 自定义布局
         */
        CENTER_CUSTOME,
        /**
         * 文字
         */
        RIGHT_TEXT,
        /**
         * 图标(默认)
         */
        RIGHT_ICON,
        /**
         * 自定义布局
         */
        RIGHT_CUSTOME,
    }
}
