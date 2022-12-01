package org.noear.solon.scheduling.simple.test.demo4;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.scheduling.ScheduledAnno;
import org.noear.solon.scheduling.simple.JobManager;

import java.time.LocalDateTime;

/**
 * @author noear 2022/3/17 created
 */
public class DemoApp2 {
    public static void main(String[] args) throws Throwable {
        Solon.start(DemoApp2.class, args, app -> {
            //删掉job
            app.get("/removeJob", (ctx) -> {
                String name = ctx.param("name");
                if (Utils.isNotEmpty(name)) {
                    JobManager.remove(name);
                }
            });

            //更新job
            app.get("/updateJob", (ctx) -> {
                String name = ctx.param("name");
                String cron = ctx.param("cron");
                if (Utils.isNotEmpty(name) && Utils.isNotEmpty(cron)) {
                    JobManager.reset(name, cron);
                }
            });
        });

        JobManager.add("job1", new ScheduledAnno().fixedRate(1000 * 3), () -> {
            System.out.println("job1::" + LocalDateTime.now());
        });

        Runnable job2 = () -> {
            System.out.println("job2::" + LocalDateTime.now());
        };
        JobManager.add("job2", new ScheduledAnno().cron("0/10 * * * * ? *"),  job2);

        Runnable job3 = Solon.context().getBeanOrNew(Job3.class); //如果不存在自动生成bean
        JobManager.add("job3", new ScheduledAnno().fixedRate(1000), job3);

        JobManager.start();
    }

    public static class Job3 implements Runnable {
        @Inject
        UserService userService;

        @Override
        public void run() {
            System.out.println("job3::" + LocalDateTime.now());
        }
    }

    @Component
    public static class UserService {

    }
}
