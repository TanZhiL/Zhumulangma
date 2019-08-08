package com.gykj.zhumulangma.common.event;

/**
 * Description: <RequestCode><br>
 * Author:      mxdl<br>
 * Date:        2019/5/27<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public interface RequestCode {
    interface Common{
        //500开始
        int PIKE_IMAGE=501;
        int PIKE_VIDEO =502;
        int VIDEO_TRIMMER =503;
    }

    interface Main {
        //1000开始
    }

    interface News {
        //2000开始
    }

    interface Find {
        //3000开始
    }

    interface Patrol{
        //4000开始
        int FACE_COMPARE=4000;
        int OUT_PATROL=4001;
    }
}
