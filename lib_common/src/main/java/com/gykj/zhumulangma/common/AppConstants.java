package com.gykj.zhumulangma.common;

/**
 * Created by 10719
 * on 2019/6/6
 */
public interface  AppConstants {
     interface Router{

         interface Common{
             String A_LOGIN="/common/login";
         }
         interface Main{
            String F_MAIN="/main/main";
            String A_MAIN="/main/MainActivity";
         }
         interface Home{
            String F_MAIN="/home/main";
            String F_SEARCH="/home/search";
            String F_RANK="/home/rank";
            String F_SEARCH_RESULT="/home/search/result";
            String F_SEARCH_SUGGEST="/home/search/suggest";
            String F_ALBUM_LIST="/home/album/list";
            String F_TRACK_LIST="/home/track/list";
            String F_ANNOUNCER_LIST="/home/announcer/list";
            String F_RADIO_LIST="/home/radio/list";
            String F_ALBUM_DETAIL="/home/album/detail";
             String F_PLAY_TRACK="/home/play/track";
             String F_PLAY_RADIIO="/home/play/radio";
             String F_ANNOUNCER_DETAIL="/home/announcer/detail";
             String F_BATCH_DOWNLOAD="/home/batch/download";
         }
         interface User{
             String F_MAIN="/user/main";
             String F_MESSAGE="/user/message";
             String F_ACCEPT="/task/accept";
             String F_FEEDBACK="/task/feedback";
         }
         interface  Discover{
             String F_MAIN="/discover/main";
         }
         interface Listen{
             String F_MAIN="/listen/main";
             String F_DOWNLOAD="/listen/download";
             String F_HISTORY="/listen/history";
             String F_FAVORITE="/listen/favorite";
         }
         interface Player{
             String F_PLAY_TRACK="/player/track";
             String F_HAPPEN="/pollution/happen";
             String F_ACCEPT="/pollution/accept";
             String F_FEEDBACK="/pollution/feedback";
         }
    }
    interface ArcSoft{
         String ID="5Z9BcusHbDyyojuZ2VCgYiVNkSP6JWAHHk6QjyLVMn9W";
         String KEY="F76h96nqKitMA7ckcavJixkpVQqq9YGZFXS1fcS5g4nA";
    }
    interface Agora{
         String ID="8b7694dd12fe4c9db395a5660bbb0916";
    }
    interface Speech{
        String ID="5d1ec054";
    }
    interface Bugly{
        String ID="849542e8da";
    }
    interface Ximalaya{
        String SECRET="6a43f2188877ee43a950e8e07b6dcb6e";
        /**
         * 当前 DEMO 应用的回调页，第三方应用应该使用自己的回调页。
         */
        String REDIRECT_URL = "http://api.ximalaya.com/openapi-collector-app/get_access_token";
        String REFRESH_TOKEN_URL = "https://api.ximalaya.com/oauth2/refresh_token?";
        int NOTIFICATION_ID = 10001;
    }
    interface SP{
         String USER="user";
         String TOKEN="token";
         String HOST="host";
         String CITY_CODE="city_code";
         String CITY_NAME="city_name";
         String PROVINCE_CODE="province_code";
         String PROVINCE_NAME="province_name";
         String PLAY_SCHEDULE_TYPE="play_schedule_type";
         String PLAY_SCHEDULE_TIME="play_schedule_time";
         String AD_TIME="ad_time";
         String AD_LABEL="ad_label";
    }
    interface Defualt{
        String CITY_CODE="4301";
        String CITY_NAME="长沙";
        String PROVINCE_CODE="430000";
        String PROVINCE_NAME="湖南";
        String AD_NAME="/ad.jpg";
    }
    interface Cache{
        String FACE="face";
    }

}
