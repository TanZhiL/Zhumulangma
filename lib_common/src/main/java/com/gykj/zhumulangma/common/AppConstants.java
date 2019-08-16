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
            String F_ALBUM_LIST="/home/album/list";
            String F_ALBUM_DETAIL="/home/album/detail";
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
    interface Ximalaya{
        String SECRET="6a43f2188877ee43a950e8e07b6dcb6e";
    }
    interface SP{
         String USER="user";
         String TOKEN="token";
         String HOST="host";
    }
    interface Cache{
        String FACE="face";
    }

}
