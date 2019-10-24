package com.gykj.zhumulangma.common.event;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:EventBus 标识
 */
public interface EventCode {
    interface Main {
        int JPUSH = 1000;
        int NAVIGATE=1001;
        int HIDE_GP=1002;
        int SHOW_GP=1003;
        int LOGINSUCC=1005;
        int LOGOUTSUCC=1006;
        int SHARE=1007;
        //1000开始
    }
    interface Home {
        //2000开始
        int TAB_REFRESH=2001;
    }
    interface Listen {
        //3000开始
        int DOWNLOAD_SORT=3000;
        int DOWNLOAD_DELETE=3001;
        int TAB_REFRESH=3002;
    }
    interface Discover {
        //4000开始
        int TAB_REFRESH=4001;
    }

    interface User {
        //5000开始
        int TAB_REFRESH=5001;
    }
}
