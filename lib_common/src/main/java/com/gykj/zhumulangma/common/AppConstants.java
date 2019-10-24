package com.gykj.zhumulangma.common;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:App常量
 */
public interface AppConstants {
    interface Router {

        interface Main {
            String F_MAIN = "/main/main";
            String A_MAIN = "/main/MainActivity";
        }

        interface Home {
            String F_MAIN = "/home/main";
            String F_SEARCH = "/home/search";
            String F_RANK = "/home/rank";
            String F_SEARCH_RESULT = "/home/search/result";
            String F_SEARCH_SUGGEST = "/home/search/suggest";
            String F_ALBUM_LIST = "/home/album/list";
            String F_TRACK_LIST = "/home/track/list";
            String F_ANNOUNCER_LIST = "/home/announcer/list";
            String F_RADIO_LIST = "/home/radio/list";
            String F_ALBUM_DETAIL = "/home/album/detail";
            String F_PLAY_TRACK = "/home/play/track";
            String F_PLAY_RADIIO = "/home/play/radio";
            String F_ANNOUNCER_DETAIL = "/home/announcer/detail";
            String F_BATCH_DOWNLOAD = "/home/batch/download";
            String F_SCAN = "/home/scan";
        }

        interface User {
            String F_MAIN = "/user/main";
            String F_MESSAGE = "/user/message";
        }

        interface Discover {
            String F_MAIN = "/discover/main";
            String F_WEB = "/discover/web";
        }

        interface Listen {
            String F_MAIN = "/listen/main";
            String F_DOWNLOAD = "/listen/download";
            String F_DOWNLOAD_DELETE = "/listen/download/delete";
            String F_DOWNLOAD_SORT = "/listen/download/sort";
            String F_DOWNLOAD_ALBUM = "/listen/download/album";
            String F_HISTORY = "/listen/history";
            String F_FAVORITE = "/listen/favorite";
        }

    }

    interface Third {
        //虹软
        String ARCSOFT_ID = "5Z9BcusHbDyyojuZ2VCgYiVNkSP6JWAHHk6QjyLVMn9W";
        String ARCSOFT_KEY = "F76h96nqKitMA7ckcavJixkpVQqq9YGZFXS1fcS5g4nA";
        //声网
        String AGORA_ID = "8b7694dd12fe4c9db395a5660bbb0916";
        //讯飞语音识别
        String SPEECH_ID = "5d1ec054";
        //Bugly
        String BUGLY_ID = "849542e8da";
        //友盟
        String UM_ID = "5d9c3b0b570df30848000687";
        String WX_ID = "5d9c3b0b570df30848000687";
        String WX_KEY = "5d9c3b0b570df30848000687";
        String QQ_ID = "101806238";
        String QQ_KEY = "748f15b84b0747c24e01037edfcd124c";
        String SINA_ID = "3537697814";
        String SINA_KEY = "da65b94015cacf559208b821f89a0f85";
        //喜马拉雅
        String XIMALAYA_SECRET = "6a43f2188877ee43a950e8e07b6dcb6e";
        int XIMALAYA_NOTIFICATION = 10001;
        //Bmob
       String BMOB_ID="337f9a96329e163f3cb94c7ffee8cefb";

    }

    interface SP {
        String USER = "user";
        String TOKEN = "token";
        String HOST = "host";
        String CITY_CODE = "city_code";
        String CITY_NAME = "city_name";
        String PROVINCE_CODE = "province_code";
        String PROVINCE_NAME = "province_name";
        String PLAY_SCHEDULE_TYPE = "play_schedule_type";
        String PLAY_SCHEDULE_TIME = "play_schedule_time";
        String AD_TIME = "ad_time";
        String AD_LABEL = "ad_label";
        String AD_URL = "ad_url";
    }

    interface Default {
        String CITY_CODE = "4301";
        String CITY_NAME = "长沙";
        String PROVINCE_CODE = "430000";
        String PROVINCE_NAME = "湖南";
        String AD_NAME = "/ad.jpg";
    }

    interface Cache {
        String FACE = "face";
    }

}
