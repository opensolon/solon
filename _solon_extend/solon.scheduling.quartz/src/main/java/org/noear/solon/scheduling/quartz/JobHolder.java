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
    public final Scheduled anno;
    public final AbstractJob job;

    public JobHolder(String name, Scheduled anno, AbstractJob job) {
        this.name = name;
        this.anno = anno;

        this.job = job;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        job.execute(context);
    }
}
