package com.gykj.zhumulangma.common.event;

/**
 * Description: <RequestCode><br>
 * Author:      mxdl<br>
 * Date:        2019/5/27<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
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
