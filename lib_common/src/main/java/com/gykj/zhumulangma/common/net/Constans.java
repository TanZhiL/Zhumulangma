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


    String HOST_KEY = "HOST";
    /**
     * 主机1
     */
    String HOST1_BING = "bing";
    /**
     * 主机2
     */
    String HOST2_XMLY = "ximalaya";

    /**
     * 必应
     */
    String HEADER_BING = HOST_KEY + ":" + HOST1_BING;
    /**
     * 喜马拉雅
     */
    String HEADER_XMLY = HOST_KEY + ":" + HOST2_XMLY;

    /**
     * 第三方api
     */
    String BING_HOST="https://cn.bing.com/";
    String BING_URL="https://cn.bing.com/HPImageArchive.aspx";
    String GITHUB_URL="https://api.github.com/repos/TanZhiL/Zhumulangma";
    String REDIRECT_URL = "http://api.ximalaya.com/openapi-collector-app/get_access_token";
    String REFRESH_TOKEN_URL = "https://api.ximalaya.com/oauth2/refresh_token?";

}
