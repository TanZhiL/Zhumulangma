package com.gykj.zhumulangma.common.util;

import com.blankj.utilcode.util.FileUtils;

import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/7 9:51
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
public class FileUtil {
    public static void deleteFiles(List<String> paths){
        for (int i = 0; i < paths.size(); i++) {
            FileUtils.delete(paths.get(i));
        }
    }
}
