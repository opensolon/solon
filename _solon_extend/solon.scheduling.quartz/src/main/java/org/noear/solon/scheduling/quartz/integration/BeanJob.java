package org.noear.solon.scheduling.quartz.integration;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.scheduling.quartz.AbstractJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 类运行器（支持非单例）
 *
 * @author noear
 * @since 1.11
 */
public class BeanJob extends AbstractJob {
    private final String jobId;
    private final BeanWrap target;
    private final boolean isRunnable;

    public BeanJob(BeanWrap target) {
        this.target = target;
        this.isRunnable = target.raw() instanceof Runnable;
        this.jobId = target.clz().getName();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (isRunnable) {
            ((Runnable) target.get()).run();
        } else {
            ((Job) target.get()).execute(context);
        }
    }

    @Override
    public String getJobId() {
        return jobId;
    }
}