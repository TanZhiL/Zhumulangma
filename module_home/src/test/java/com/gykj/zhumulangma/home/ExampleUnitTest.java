package com.gykj.zhumulangma.home;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
      String s="搜索\"a\"";

        int start = s.indexOf("\"");
        int end = s.lastIndexOf("\"");
        System.out.println(start);
        System.out.println(end);
    }
}