package demo;

import org.noear.snack.ONode;
import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.annotation.CloudJob;
import org.quartz.JobExecutionContext;

@Component
public class Job2Com {
    @CloudJob(value = "job2", cron7x = "* * * * * ? +07")
    public void job2(JobExecutionContext jobContext){
        System.out.println(ONode.stringify(jobContext));
    }
}
