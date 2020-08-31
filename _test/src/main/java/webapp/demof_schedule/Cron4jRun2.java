package webapp.demof_schedule;

import org.noear.solon.extend.cron4j.Job;

@Job(cron4x = "*/1 * * * *")
public class Cron4jRun2 implements Runnable {

    @Override
    public void run() {
        System.out.println("我是定时任务: Cron4jRun2(*/1 * * * *)");
    }
}
