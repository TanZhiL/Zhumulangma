package com.gykj.zhumulangma.common.extra;


/**
 * Author: Thomas.<br/>
 * Date: 2019/10/24 12:43<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:用来存放局部变量,实现匿名内部类直接修改局部变量的情况
 */
public class RxField<F> {

    private F field;

    public RxField() {
    }

    public RxField(F field) {
        this.field = field;
    }

    public F get() {
        return field;
    }

    public void set(F field) {
        this.field = field;
    }
}
