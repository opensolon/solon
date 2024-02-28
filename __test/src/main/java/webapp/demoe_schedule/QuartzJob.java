package webapp.demoe_schedule;



import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;

import java.util.Date;

@Scheduled(cron = "0 0/1 * * * ? *")
public class QuartzJob implements JobHandler {
    @Override
    public void handle(Context ctx)  {
        System.out.println("我是定时任务: QuartzJob(0 0/1 * * * ? *) -- " + new Date().toString());
    }
}
