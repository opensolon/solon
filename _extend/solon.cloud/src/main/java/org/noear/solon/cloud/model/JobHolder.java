package org.noear.solon.cloud.model;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.core.handle.Context;

/**
 * 任务处理实体
 *
 * @author noear
 * @since 1.4
 */
public class JobHolder implements CloudJobHandler {
    private final String name;
    private final String cron7x;
    private final String description;
    private final CloudJobHandler handler;

    public JobHolder(String name, String cron7x, String description, CloudJobHandler handler) {
        this.name = name;
        this.cron7x = cron7x;
        this.description = description;
        this.handler = handler;
    }

    /**
     * 获取任务
     */
    public String getName() {
        return name;
    }

    /**
     * 获取计划表达式
     */
    public String getCron7x() {
        return cron7x;
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * ::起到代理作用，从而附加能力
     *
     * @since 1.6
     */
    @Override
    public void handle(Context ctx) throws Throwable {
        if (CloudManager.jobInterceptor() == null) {
            handler.handle(ctx);
        } else {
            CloudManager.jobInterceptor().doInterceptor(new JobImpl(this, ctx), handler);
        }
    }
}
