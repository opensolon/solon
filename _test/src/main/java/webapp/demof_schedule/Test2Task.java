package webapp.demof_schedule;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.noear.solon.extend.cron4j.Job;

@Job(cron4x = "* * * * *")
public class Test2Task extends Task {

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
        System.out.println("我是定时任务: Test2Task(* * * * *)");
    }
}
