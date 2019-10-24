package com.gykj.zhumulangma.common.mvvm.view;

import android.support.annotation.DrawableRes;
import android.view.View;

import com.gykj.zhumulangma.common.mvvm.view.status.BlankCallback;
import com.gykj.zhumulangma.common.mvvm.view.status.EmptyCallback;
import com.gykj.zhumulangma.common.mvvm.view.status.ErrorCallback;
import com.gykj.zhumulangma.common.mvvm.view.status.LoadingCallback;
import com.kingja.loadsir.callback.Callback;

import java.util.List;

/**
 * Author: Thomas.<br/>
 * Date: 2019/10/24 15:25<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public interface BaseView {

    interface SimpleBarStyle {
        /**
         * 返回图标(默认)
         */
        int LEFT_BACK = 0;
        /**
         * 返回图标+文字
         */
        int LEFT_BACK_TEXT = 1;
        /**
         * 附加图标
         */
        int LEFT_ICON = 2;
        /**
         * 标题(默认)
         */
        int CENTER_TITLE = 7;
        /**
         * 自定义布局
         */
        int CENTER_CUSTOME = 8;
        /**
         * 文字
         */
        int RIGHT_TEXT = 4;
        /**
         * 图标(默认)
         */
        int RIGHT_ICON = 5;
        /**
         * 自定义布局
         */
        int RIGHT_CUSTOME = 6;
    }
    /**
     * 提供状态布局
     *
     * @return
     */
    default Callback getInitCallBack() {
        return new BlankCallback();
    }

    default Callback getLoadingCallback() {
        return new LoadingCallback();
    }

    default Callback getErrorCallback() {
        return new ErrorCallback();
    }

    default Callback getEmptyCallback() {
        return new EmptyCallback();
    }

    /**
     * 提供额外状态布局
     *
     * @return
     */
    default List<Callback> onBindExtraCallBack() {
        return null;
    }


    /**
     * 是否开启通用标题栏,默认true
     *
     * @return
     */
    default boolean enableSimplebar() {
        return true;
    }

    /**
     * 初始化右边标题栏类型
     *
     * @return
     */
    default int onBindBarRightStyle() {
        return BaseActivity.SimpleBarStyle.RIGHT_ICON;
    }

    /**
     * 初始化左边标题栏类型
     *
     * @return
     */
    default int onBindBarLeftStyle() {
        return BaseActivity.SimpleBarStyle.LEFT_BACK;
    }

    /**
     * 初始化中间标题栏类型
     *
     * @return
     */
    default int onBindBarCenterStyle() {
        return BaseActivity.SimpleBarStyle.CENTER_TITLE;
    }

    /**
     * 初始化标题栏右边文本
     *
     * @return
     */
    default String[] onBindBarRightText() {
        return null;
    }

    /**
     * 初始化标题文本
     *
     * @return
     */
    default String[] onBindBarTitleText() {
        return null;
    }

    /**
     * 初始化标题栏右边图标
     *
     * @return
     */
    default @DrawableRes
    Integer[] onBindBarRightIcon() {
        return null;
    }

    /**
     * 初始化标题栏左边附加图标
     *
     * @return
     */
    default @DrawableRes
    Integer onBindBarLeftIcon() {
        return null;
    }

    /**
     * 初始化标题栏左边返回按钮图标
     *
     * @return
     */
    default @DrawableRes
    Integer onBindBarBackIcon() {
        return null;
    }

    /**
     * 点击标题栏返回按钮事件
     */
     void onSimpleBackClick();
    /**
     * 初始化标题栏右侧自定义布局
     *
     * @return
     */
    default View onBindBarRightCustome() {
        return null;
    }

    /**
     * 初始化标题栏中间自定义布局
     *
     * @return
     */
    default View onBindBarCenterCustome() {
        return null;
    }

    /**
     * 点击标题栏靠右第一个事件
     *
     * @return
     */
    default void onRight1Click(View v) {

    }

    /**
     * 点击标题栏靠右第二个事件
     *
     * @return
     */
    default void onRight2Click(View v) {

    }

    /**
     * 点击标题栏靠左附加事件
     *
     * @return
     */
    default void onLeftIconClick(View v) {

    }
}
