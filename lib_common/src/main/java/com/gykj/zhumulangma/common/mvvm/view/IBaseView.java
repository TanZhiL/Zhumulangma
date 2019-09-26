package com.gykj.zhumulangma.common.mvvm.view;

import android.content.Context;

/**
 * Description: <IBaseView><br>
 * Author:      mxdl<br>
 * Date:        2019/06/30<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public interface IBaseView {
    void initView();
    void initListener();
    void initData();
    void showInitView(boolean show);
    void showEmptyView(boolean show);
    void showLoadingView(String tip);
    void showErrorView(boolean show);
    Context getContext();
}
