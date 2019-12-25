package webapp.demof_schedule;

import org.noear.solon.extend.cron4j.Job;

@Job(cron4 = "* * * * *")
public class TestTask implements Runnable {

    @Override
    public void run() {
        System.out.println("我是定时任务: TestTask");
    }
}
