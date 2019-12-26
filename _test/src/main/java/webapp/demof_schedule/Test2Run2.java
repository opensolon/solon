package webapp.demof_schedule;

import org.noear.solon.extend.cron4j.Job;

@Job(cron4x = "200ms")
public class Test2Run2 implements Runnable {

    @Override
    public void run() {
        System.out.println("我是定时任务: Test2Run2(200ms)");
    }
}
