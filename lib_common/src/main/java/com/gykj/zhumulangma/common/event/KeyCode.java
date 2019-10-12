package com.gykj.zhumulangma.common.event;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:ARouter 传递Key
 */
public interface KeyCode {
    interface Main {
    }

    interface Home {
        String KEYWORD = "keyword";
        String TYPE = "type";
        String TITLE = "title";
        String ALBUMID = "albumid";
        String HOTWORD = "hotword";
        String RADIO_ID = "radioId";
        String ANNOUNCER_ID = "announcerId";
        String CATEGORY_ID = "categoryId";
        String ANNOUNCER_NAME= "announcerName";
    }

    interface Listen {
        String TAB_INDEX="tab_index";
        String ALBUMID = "albumid";
    }
    interface Discover {
        String PATH="path";
    }

    interface Video {
        String CHANNEL="channel";
    }
    interface Task {
        String TYPE="type";
    }
    interface Patrol {
        String FACE_PATH="face_path";
    }
}
