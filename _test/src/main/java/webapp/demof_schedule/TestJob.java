package webapp.demof_schedule;

import org.noear.solon.extend.schedule.Job;

@Job(cron4x = "2s")
public class TestJob implements Runnable {

    @Override
    public void run() {
        System.out.println("我是定时任务: TestJob(2s)");
    }
}
