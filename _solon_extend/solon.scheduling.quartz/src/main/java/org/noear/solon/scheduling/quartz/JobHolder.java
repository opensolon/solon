package org.noear.solon.scheduling.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz job 实体
 * */
public class JobHolder implements Job {
    public final String name;
    /**
     * cron4 or 100ms,2s,1m,1h,1d(ms:毫秒；s:秒；m:分；h:小时；d:天)
     */
    public final String cronx;
    public final boolean enable;

    public final AbstractJob job;
    public final String jobID;

    public JobHolder(String name, String cronx, boolean enable, AbstractJob job) {
        this.name = name;
        this.cronx = cronx;
        this.enable = enable;

        this.job = job;
        this.jobID = job.getJobId();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        job.execute(context);
    }
}
