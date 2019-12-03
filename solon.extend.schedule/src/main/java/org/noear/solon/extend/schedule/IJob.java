package org.noear.solon.extend.schedule;

public interface IJob {
    String getName();
    int getInterval();//秒为单位

    void exec() throws Throwable;
}
