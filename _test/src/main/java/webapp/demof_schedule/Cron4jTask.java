package webapp.demof_schedule;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.noear.solon.extend.cron4j.Cron4j;

import java.util.Date;

@Cron4j(cron5x = "*/1 * * * *")
public class Cron4jTask extends Task {

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
        System.out.println("我是定时任务: Cron4jTask(*/1 * * * *) -- " + new Date().toString());
    }
}
