package com.gykj.zhumulangma.common.util;

import android.text.InputFilter;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Thomas.
 * Date: 2019/9/19 8:57
 * Email: 1071931588@qq.com
 * Description:
 */
public class UiUtil {
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter= (source, start, end, dest, dstart, dend) -> {
            if(source.equals(" "))return "";
            else return null;
        };
        editText.setFilters(new InputFilter[]{filter});
    }
    public static void setEditTextInhibitInputSpeChat(EditText editText){

        InputFilter filter= (source, start, end, dest, dstart, dend) -> {
            String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            Pattern pattern = Pattern.compile(speChat);
            Matcher matcher = pattern.matcher(source.toString());
            if(matcher.find())return "";
            else return null;
        };
        editText.setFilters(new InputFilter[]{filter});
    }
}
