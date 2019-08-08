package com.gykj.zhumulangma.common.net.config;

/**
 * Description: <API><br>
 * Author:      mxdl<br>
 * Date:        2019/6/23<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public interface API {

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
   String OFFLINE_HOST1 = "http://192.168.1.41:8082/demo-hzzmap/";
   String OFFLINE_HOST2 = "http://192.168.31.105:8767/";
    /**
     * 正式环境
     */
    String ONLINE_HOST1 = "http://121.201.127.153:8082/demo-hzzmap/";
    String ONLINE_HOST2 = "http://192.168.31.105:8767/";
}
