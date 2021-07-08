package org.noear.solon.cloud.model;

import org.noear.solon.core.handle.Handler;

/**
 * 处理实现
 *
 * @author noear
 * @since 1.4
 */
public class JobHandlerEntity {
    private String name;
    private String cron7x;
    private String description;
    private Handler handler;

    public JobHandlerEntity(String name, String cron7x, String description, Handler handler) {
        this.name = name;
        this.cron7x = cron7x;
        this.description = description;
        this.handler = handler;
    }

    /**
     * 获取任务
     * */
    public String getName() {
        return name;
    }

    /**
     * 获取计划表达式
     * */
    public String getCron7x() {
        return cron7x;
    }

    /**
     * 获取描述
     * */
    public String getDescription() {
        return description;
    }

    /**
     * 获取处理者
     * */
    public Handler getHandler() {
        return handler;
    }
}
