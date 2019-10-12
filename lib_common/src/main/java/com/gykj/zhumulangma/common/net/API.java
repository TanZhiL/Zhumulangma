package com.gykj.zhumulangma.common.net;

/**
 * <br/>Description: <API><br>
 * Author:      mxdl<br>
 * <br/>Date:        2019/6/23<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public interface API {
    String BING_HOST="https://cn.bing.com/";
    String BING_URL="https://cn.bing.com/HPImageArchive.aspx";
    String GITHUB_URL="https://api.github.com/repos/TanZhiL/Zhumulangma";

    interface HostStatus {
            int ONLINE=0;
            int OFFLINE=1;
    }
    interface BaseUrl{
        String KEY="Host";
        String HOST1="Local";
        String HOST2="Other";
    }
    /**
     * 测试环境
     */
   String OFFLINE_HOST1 = "https://cn.bing.com/";
   String OFFLINE_HOST2 = "http://192.168.31.105:8767/";
    /**
     * 正式环境
     */
    String ONLINE_HOST1 = "https://cn.bing.com/";
    String ONLINE_HOST2 = "http://192.168.31.105:8767/";
}
