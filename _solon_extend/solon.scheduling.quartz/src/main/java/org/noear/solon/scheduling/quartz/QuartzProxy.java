package org.noear.solon.scheduling.quartz;

import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz 任务执行代理
 *
 * @author noear
 * @since 1.11
 * @since 2.2
 * */
public class QuartzProxy implements Job {
    @Override
    public void execute(JobExecutionContext jc) throws JobExecutionException {
        String name = jc.getJobDetail().getKey().getName();
        JobHolder jobHolder = JobManager.getInstance().jobGet(name);

        if (jobHolder != null) {
            Context ctx = QuartzContext.getContext(jc);

            try {
                jobHolder.handle(ctx);
            } catch (Throwable e) {
                throw new JobExecutionException("Job execution failed: " + name, e);
            }
        }
    }
}
