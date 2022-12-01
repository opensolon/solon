package demo;

import org.noear.solon.extend.quartz.Quartz;

import java.util.Date;

/**
 * @author noear 2022/11/24 created
 */
@Quartz(cron7x = "1s")
public class BeanJob1 implements Runnable {
    @Override
    public void run() {
        System.out.println("job1:: " + new Date());
    }
}
