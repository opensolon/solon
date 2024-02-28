package org.noear.solon.scheduling.scheduled.wrap;

import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.Job;
import org.noear.solon.scheduling.scheduled.JobHolder;

/**
 * @author noear
 * @since 1.6
 */
public class JobImpl implements Job {
    private final JobHolder holder;
    private final Context context;

    public JobImpl(JobHolder holder, Context context) {
        this.holder = holder;
        this.context = context;
    }

    /**
     * 获取任务
     */
    public String getName() {
        return holder.getName();
    }

    /**
     * 获取计划表达式
     */
    public Scheduled getScheduled() {
        return holder.getScheduled();
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return context;
    }
}
