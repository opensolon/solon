package demo3;

import org.noear.solon.Solon;
import org.noear.solon.core.event.BeanLoadEndEvent;
import org.noear.solon.schedule.JobManager;
import org.noear.solon.schedule.annotation.EnableScheduling;
import org.noear.solon.schedule.cron.CronExpressionPlus;
import org.noear.solon.schedule.cron.CronUtils;

/**
 * @author noear 2022/6/26 created
 */
@EnableScheduling
public class DemoApp3 {
    public static void main(String[] args) {
        Solon.start(DemoApp3.class, args, app -> {
//            app.onEvent(BeanLoadEndEvent.class, e -> {
//                //容器加载完成后，读取数据库的值，修改配置
//                //修改job1的时间
//                JobManager.reset("job1", "0/1 * * * * * ?");
//            });

            app.get("/reset",ctx->{
                //通过接口或事件，直接修改配置
                //修改job1的时间
                JobManager.reset("Job1", "* * * * * ? *");
            });

            app.get("/reset2",ctx->{
                //修改job1的时间
                JobManager.reset("Job1", 10*1000);
            });
        });
    }
}
