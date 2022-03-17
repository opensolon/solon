package demo2;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Aop;
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

        Runnable job3 = Aop.getOrNew(Job3.class); //如果不存在自动生成bean
        JobManager.add("job3", 1000, false, job3);

        JobManager.start();
    }

    public static class Job3 implements Runnable{
        @Inject
        UserService userService;

        @Override
        public void run() {
            System.out.println("job3::" + LocalDateTime.now());
        }
    }

    @Component
    public static class UserService{

    }
}
