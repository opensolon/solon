package org.noear.solon.scheduling.quartz;

import org.noear.solon.scheduling.annotation.Scheduled;
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
    public final Scheduled anno;

    public final AbstractJob job;
    public final String jobID;

    public JobHolder(String name, Scheduled anno,AbstractJob job) {
        this.name = name;
        this.anno = anno;

        this.job = job;
        this.jobID = job.getJobId();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        job.execute(context);
    }
}
