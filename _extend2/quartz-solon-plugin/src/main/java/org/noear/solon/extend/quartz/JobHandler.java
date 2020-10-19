package org.noear.solon.extend.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobHandler implements Job {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobEntity jobEntity = (JobEntity) ctx.get("jobID");
        if (jobEntity != null) {
            jobEntity.exec(ctx);
        }
    }
}
