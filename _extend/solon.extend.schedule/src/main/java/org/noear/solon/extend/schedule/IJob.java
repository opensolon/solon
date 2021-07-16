package org.noear.solon.extend.schedule;

/**
 * 任务
 *
 * @author noear
 * @since 1.0
 * */
public interface IJob {
    /**
     * 间格时间（单位：毫秒）
     */
    int getInterval();

    /**
     * 执行代码
     */
    void exec() throws Throwable;

    /**
     * 延迟（单位：毫秒）
     * */
    default int getDelay() {
        return 0;
    }


    /**
     * 任务名称，可能由bean确定
     */
    default String getName() {
        return null;
    }

    /**
     * 执行线程数
     */
    default int getThreads() {
        return 1;
    }
}
