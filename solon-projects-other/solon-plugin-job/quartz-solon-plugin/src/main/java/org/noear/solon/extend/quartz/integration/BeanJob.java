package org.noear.solon.extend.quartz.integration;

import org.noear.solon.extend.quartz.AbstractJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author noear
 * @since 1.11
 */
public class BeanJob extends AbstractJob {
    private final String jobId;
    private final Object target;
    private final boolean isRunnable;

    public BeanJob(Object target) {
        this.target = target;
        this.isRunnable = target instanceof Runnable;
        this.jobId = target.getClass().getName();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (isRunnable) {
            ((Runnable) target).run();
        } else {
            ((Job) target).execute(context);
        }
    }

    @Override
    public String getJobId() {
        return jobId;
    }
}