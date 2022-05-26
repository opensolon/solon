package demo2;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
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
                    Runnable runnable = JobManager.getRunnable(name);
                    if (runnable != null) { //如果找到了对应的执行函数
                        JobManager.remove(name); //移除
                        JobManager.add(name, cron, false, runnable); //再添加
                    }
                }
            });
        });

        JobManager.add("job1", 1000 * 3, false, () -> {
            System.out.println("job1::" + LocalDateTime.now());
        });

        Runnable job2 = () -> {
            System.out.println("job2::" + LocalDateTime.now());
        };
        JobManager.add("job2", "0/10 * * * * ? *", false, job2);

        Runnable job3 = Aop.getOrNew(Job3.class); //如果不存在自动生成bean
        JobManager.add("job3", 1000, false, job3);

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
