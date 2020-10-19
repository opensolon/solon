package webapp.demof_schedule;

import org.noear.solon.extend.quartz.Quartz;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

@Quartz(cron7x = "0 0/1 * * * ? *")
public class QuartzJob implements Job {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        System.out.println("我是定时任务: QuartzJob(0 0/1 * * * ? *) -- " + new Date().toString());
    }
}
