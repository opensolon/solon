package webapp.demof_schedule;

import org.noear.solon.extend.quartz.Quartz;

import java.util.Date;

@Quartz(cron7x = "0 0/1 * * * ? *")
public class QuartzRun2 implements Runnable {

    @Override
    public void run() {
        System.out.println("我是定时任务: QuartzRun2(0 0/1 * * * ? *) -- " + new Date().toString());
    }
}
