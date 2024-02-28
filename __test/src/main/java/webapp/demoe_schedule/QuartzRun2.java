package webapp.demoe_schedule;

import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

@Scheduled(cron = "0 0/1 * * * ? *")
public class QuartzRun2 implements Runnable {

    @Override
    public void run() {
        System.out.println("我是定时任务: QuartzRun2(0 0/1 * * * ? *) -- " + new Date().toString());
    }
}
