package testapp;

import it.sauronsoftware.cron4j.Scheduler;
import org.junit.Test;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DemoTest {
    @Test
    public void test1(){
        Scheduler scheduler = new Scheduler();

        //写法一：此种方式，控制台每分钟打印
        scheduler.schedule("10-59/1 * * * *", () -> System.out.println("Every Minute Run."));

        //写法二：此种方式，控制台不会有任何打印
        scheduler.schedule("10/1 * * * *", () -> System.out.println("Every Minute Run."));

        scheduler.start();
        try {
            Thread.sleep(1000L * 60L * 10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduler.stop();
    }

    @Test
    public void test2(){
        Scheduler s = new Scheduler();

        ScheduledThreadPoolExecutor taskScheduler;

        taskScheduler = new ScheduledThreadPoolExecutor(1);

        taskScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("我设置了2秒哦.");
            }
        }, 0, 2, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("我设置了3秒哦.");
            }
        }, 0, 3, TimeUnit.SECONDS);


        s.start();

        try {
            Thread.sleep(1000L * 60L * 10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
