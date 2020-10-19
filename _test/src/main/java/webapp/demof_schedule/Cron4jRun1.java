package webapp.demof_schedule;


import org.noear.solon.extend.cron4j.Cron4j;

import java.util.Date;

//@Cron4j(cronx = "200ms")
public class Cron4jRun1 implements Runnable {

    @Override
    public void run() {
        System.out.println("我是定时任务: Cron4jRun1(200ms) -- " + new Date().toString());
        //throw new RuntimeException("异常");
    }
}
