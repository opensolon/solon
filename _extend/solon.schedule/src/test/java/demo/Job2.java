package demo;

import org.noear.solon.schedule.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2021/12/28 created
 */
@Scheduled(cron = "0/30 * * * * ? *")
public class Job2 implements Runnable{
    @Override
    public void run() {
        System.out.println(new Date() +": 0/30 * * * * ? *");
    }
}
