package org.noear.solon.scheduling.scheduled;

import org.noear.solon.scheduling.annotation.Scheduled;

/**
 * 任务持有人
 *
 * @author noear
 * @since 2.2
 */
public class JobHolder {
    String name;
    Scheduled scheduled;
    JobHandler handler;

    public JobHolder(String name, Scheduled scheduled, JobHandler handler) {
        this.name = name;
        this.scheduled = scheduled;
        this.handler = handler;
    }

    /**
     * 获取名字
     */
    public String getName() {
        return name;
    }

    /**
     * 获取计划
     */
    public Scheduled getScheduled() {
        return scheduled;
    }

    /**
     * 获取处理器
     */
    public JobHandler getHandler() {
        return handler;
    }
}
