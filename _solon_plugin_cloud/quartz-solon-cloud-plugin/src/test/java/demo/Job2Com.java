package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.annotation.CloudJob;
import org.quartz.JobExecutionContext;

@Component
public class Job2Com {
    @CloudJob("job2")
    public void job2(JobExecutionContext jobExecutionContext){

    }
}
