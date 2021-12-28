package demo;

import org.noear.solon.schedule.annotation.Scheduled;

/**
 * @author noear 2021/12/28 created
 */
@Scheduled(fixedRate = 1000 * 2)
public class Job1 implements Runnable{
    @Override
    public void run() {
        System.out.println(System.currentTimeMillis() +": 1000*2");
    }
}
