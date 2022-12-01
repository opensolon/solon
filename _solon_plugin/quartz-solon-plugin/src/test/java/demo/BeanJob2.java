package demo;

import org.noear.solon.extend.quartz.Quartz;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * @author noear 2022/12/1 created
 */
@Quartz(cron7x = "* * * * * ? ")
public class BeanJob2 implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("job2:: " + context.getJobDetail().getKey().getName());
        System.out.println("job2:: " + new Date());
    }
}
