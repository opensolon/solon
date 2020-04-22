package org.noear.solon.extend.schedule;

public interface IJob {
    /** 间格时间 */
    int getInterval();//秒为单位

    /** 执行代码 */
    void exec() throws Throwable;

    /** 任务名称，可能由xbean确定 */
    default String getName() {
        return null;
    }

    /** 执行线程数 */
    default int getThreads(){return 1;}
}
