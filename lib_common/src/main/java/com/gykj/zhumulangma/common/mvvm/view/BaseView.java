package com.gykj.zhumulangma.common.mvvm.view;

import androidx.annotation.Nullable;

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
     * 提供额外状态布局
     *
     * @return
     */
    default @Nullable
    List<Callback> getExtraStatus() {
        return null;
    }

}
