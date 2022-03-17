package demo2;

import org.noear.solon.Solon;
import org.noear.solon.schedule.JobManager;

import java.time.LocalDateTime;

/**
 * @author noear 2022/3/17 created
 */
public class DemoApp2 {
    public static void main(String[] args) throws Throwable {
        Solon.start(DemoApp2.class, args);

        JobManager.add("job1", 1000 * 3, false, () -> {
            System.out.println("job1::" + LocalDateTime.now());
        });

        JobManager.add("job2", "0/10 * * * * ? *", false, () -> {
            System.out.println("job2::" + LocalDateTime.now());
        });

        JobManager.start();
    }
}
