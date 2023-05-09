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
        String name = ctx.getJobDetail().getKey().getName();
        Job jobReal = JobManager.getJob(name);

        if (jobReal != null) {
            jobReal.execute(ctx);
        }
    }
}
