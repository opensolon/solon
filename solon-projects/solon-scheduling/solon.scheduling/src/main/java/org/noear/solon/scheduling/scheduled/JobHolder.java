package org.noear.solon.scheduling.scheduled;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.manager.IJobManager;
import org.noear.solon.scheduling.scheduled.wrap.JobImpl;

import java.util.Map;

/**
 * 任务持有人
 *
 * @author noear
 * @since 2.2
 */
public class JobHolder implements JobHandler {
    protected final String name;
    protected final Scheduled scheduled;
    protected final JobHandler handler;
    protected final IJobManager jobManager;

    protected Map<String, String> data;
    protected Object attachment;

    public JobHolder(IJobManager jobManager, String name, Scheduled scheduled, JobHandler handler) {
        this.name = name;
        this.scheduled = scheduled;
        this.handler = handler;
        this.jobManager = jobManager;
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

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    /**
     * 处理
     */
    @Override
    public void handle(Context ctx) throws Throwable {
        if (ctx == null) {
            ctx = new ContextEmpty();
        }

        if (data != null) {
            ctx.paramMap().putAll(data);
        }

        if (jobManager.hasJobInterceptor()) {
            Job job = new JobImpl(this, ctx);
            JobHandlerChain handlerChain = new JobHandlerChain(job, handler, jobManager.getJobInterceptors());
            handlerChain.handle(ctx);
        } else {
            handler.handle(ctx);
        }
    }
}
