package webapp.demof_schedule;


import org.noear.solon.extend.cron4.Cron4j;

import java.util.Date;

@Cron4j(cron5x = "200ms", name = "Cron4jRun1")
public class Cron4jRun1 implements Runnable {

    @Override
    public void run() {
        System.out.println("我是定时任务: Cron4jRun1(200ms) -- " + new Date().toString());
        //throw new RuntimeException("异常");
        System.out.println("如果间隔太长，我可能被配置给控制了");
    }
}
