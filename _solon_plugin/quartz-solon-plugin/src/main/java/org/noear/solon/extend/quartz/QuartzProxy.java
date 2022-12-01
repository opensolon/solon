package org.noear.solon.extend.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz job 的执行代理
 *
 * @author noear
 * */
public class QuartzProxy implements Job {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobEntity jobEntity = JobManager.getJob(ctx.getJobDetail().getJobDataMap().getString("__jobID"));

        if (jobEntity != null) {
            jobEntity.execute(ctx);
        }
    }
}
