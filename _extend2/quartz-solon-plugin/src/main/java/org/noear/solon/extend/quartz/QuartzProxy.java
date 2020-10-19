package org.noear.solon.extend.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzProxy implements Job {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobEntity jobEntity = JobManager.getJob(ctx.getJobDetail().getJobDataMap().getString("__jobID"));

        if (jobEntity != null) {
            jobEntity.exec(ctx);
        }
    }
}
