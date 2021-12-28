package demo;

import org.noear.solon.schedule.annotation.Scheduled;

/**
 * @author noear 2021/12/28 created
 */
@Scheduled(cron = "0 0/1 * * * ? *")
public class Job2 implements Runnable{
    @Override
    public void run() {
        System.out.println(System.currentTimeMillis() +": 0 0/1 * * * ? *");
    }
}
