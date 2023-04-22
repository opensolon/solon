package org.noear.solon.scheduling.scheduled;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.scheduling.annotation.Scheduled;

/**
 * 任务持有人
 *
 * @author noear
 * @since 2.2
 */
public class JobHolder implements JobHandler {
    protected String name;
    protected Scheduled scheduled;
    protected JobHandler handler;
    protected Context context;

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

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 处理
     */
    @Override
    public void handle(Context ctx) throws Throwable {
        if (ctx == null) {
            if (context == null) {
                ctx = new ContextEmpty();
            } else {
                ctx = context;
            }
        }

        handler.handle(ctx);
    }
}
