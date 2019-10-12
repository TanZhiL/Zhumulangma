package com.gykj.zhumulangma.common.extra;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import java.io.File;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/11 8:22
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:  图片加载器，XPopup不负责加载图片，需要你实现一个图片加载器传给我，这里以Glide为例，
 * 如果图片加载不出来，往往是网络问题，或者图片大小过大，一般跟XPopup无关，请自行检查。
 */
public class PopupImageLoader implements XPopupImageLoader {

    @Override
    public void loadImage(int position, @NonNull Object uri, @NonNull ImageView imageView) {
        Glide.with(imageView).load(uri).into(imageView);
    }

    //必须实现这个方法，返回uri对应的缓存文件，可参照下面的实现，内部保存图片会用到。如果你不需要保存图片这个功能，可以返回null。
    @Override
    public File getImageFile(@NonNull Context context, @NonNull Object uri) {
        try {
            return Glide.with(context).downloadOnly().load(uri).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}