package com.gykj.zhumulangma.common.util;

import com.blankj.utilcode.util.FileUtils;

import java.util.List;

/**
 * Author: Thomas.
 * Date: 2019/8/7 9:51
 * Email: 1071931588@qq.com
 * Description:
 */
public class FileUtil {
    public static void deleteFiles(List<String> paths){
        for (int i = 0; i < paths.size(); i++) {
            FileUtils.delete(paths.get(i));
        }
    }
}
