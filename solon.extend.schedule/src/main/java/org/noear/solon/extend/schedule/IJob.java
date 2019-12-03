package org.noear.solon.extend.schedule;

public interface IJob {
    int getInterval();//秒为单位

    void exec() throws Throwable;
}
