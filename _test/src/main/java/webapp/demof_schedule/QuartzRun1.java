package webapp.demof_schedule;

import org.noear.solon.extend.quartz.Quartz;

import java.util.Date;

@Quartz(cron7x = "200ms")
public class QuartzRun1 implements Runnable {
    @Override
    public void run() {
        System.out.println("我是定时任务: QuartzRun1(200ms) -- " + new Date().toString());
        //throw new RuntimeException("异常");
    }
}
