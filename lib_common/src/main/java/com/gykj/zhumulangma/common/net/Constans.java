package com.gykj.zhumulangma.common.net;

/**
 * Author: Thomas.<br/>
 * Date: 2019/10/12 11:16<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:Constans
 */
public interface Constans {
    /**
     * Token
     */
    String TOKEN_KEY="token";

    /**
     * 在线
     */
    int NET_ONLINE = 0;
    /**
     * 离线
     */
    int NET_OFFLINE = 1;

    String HOST_KEY = "HOST";
    /**
     * 主机1
     */
    String HOST1_VALUE = "Local";
    /**
     * 主机2
     */
    String HOST2_VALUE = "Other";

    /**
     * 标识使用主机1
     */
    String HEADER_HOST1 = HOST_KEY + ":" + HOST1_VALUE;
    /**
     * 标识使用主机2
     */
    String HEADER_HOST2 = HOST_KEY + ":" + HOST2_VALUE;

    /**
     * 第三方api
     */
    String BING_HOST="https://cn.bing.com/";
    String BING_URL="https://cn.bing.com/HPImageArchive.aspx";
    String GITHUB_URL="https://api.github.com/repos/TanZhiL/Zhumulangma";
    String REDIRECT_URL = "http://api.ximalaya.com/openapi-collector-app/get_access_token";
    String REFRESH_TOKEN_URL = "https://api.ximalaya.com/oauth2/refresh_token?";
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
