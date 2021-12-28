package demo;

import org.noear.solon.schedule.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2021/12/28 created
 */
@Scheduled(fixedRate = 1000 * 3)
public class Job1 implements Runnable {
    @Override
    public void run() {
        System.out.println(new Date() + ": 1000*3");
    }
}
