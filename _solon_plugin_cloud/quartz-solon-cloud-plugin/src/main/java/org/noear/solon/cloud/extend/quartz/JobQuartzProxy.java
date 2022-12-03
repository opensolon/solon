package org.noear.solon.cloud.extend.quartz;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.core.handle.ContextEmpty;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz job 的执行代理
 *
 * @author noear
 * */
public class JobQuartzProxy implements Job {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        String jobId = ctx.getJobDetail().getKey().getName();
        CloudJobHandler jobReal = JobManager.getJob(jobId);

        if (jobReal != null) {
            ContextEmpty context = new ContextEmpty();
            try {
                jobReal.handle(context);
            } catch (Throwable e) {
                throw new JobExecutionException("Job execution failed: " + jobId, e);
            }
        }
    }
}
