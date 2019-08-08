package com.gykj.zhumulangma.common.provider;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;

/**
 * Description: <IFindProvider><br>
 * Author:      mxdl<br>
 * Date:        2019/5/23<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public interface IFindProvider extends IProvider {
    BaseFragment getMainFindFragment();
}
