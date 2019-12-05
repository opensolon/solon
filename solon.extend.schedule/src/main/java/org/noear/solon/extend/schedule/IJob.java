package org.noear.solon.extend.schedule;

public interface IJob {
    int getInterval();//秒为单位

    void exec() throws Throwable;

    //可能由xbean确定
    default String getName() {
        return null;
    }
}
