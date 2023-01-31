package demo;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.handle.Context;
import org.quartz.JobExecutionContext;

@CloudJob(value = "job1", cron7x = "3s")
public class Job1 implements CloudJobHandler {
    @Override
    public void handle(Context ctx) throws Throwable {
        JobExecutionContext jobContext = (JobExecutionContext)ctx.request();

        System.out.println(ONode.stringify(jobContext));
    }
}
