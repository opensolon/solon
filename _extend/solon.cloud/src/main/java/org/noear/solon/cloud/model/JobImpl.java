package org.noear.solon.cloud.model;

import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.6
 */
public class JobImpl implements Job{
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
    public String getCron7x() {
        return holder.getCron7x();
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return holder.getDescription();
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return context;
    }
}
