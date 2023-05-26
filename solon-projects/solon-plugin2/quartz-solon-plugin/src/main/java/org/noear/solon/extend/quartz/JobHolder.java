package org.noear.solon.extend.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 任务持有人
 * */
public class JobHolder implements Job {
    public final String name;
    public final String cronx;
    public final boolean enable;

    public final AbstractJob job;

    public JobHolder(String name, String cronx, boolean enable, AbstractJob job) {
        this.name = name;
        this.cronx = cronx;
        this.enable = enable;

        this.job = job;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        job.execute(context);
    }
}
