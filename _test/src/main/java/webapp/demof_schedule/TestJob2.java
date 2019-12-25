package webapp.demof_schedule;

import org.noear.solon.annotation.XBean;
import org.noear.solon.extend.schedule.IJob;

import java.time.LocalDateTime;

@XBean
public class TestJob2 implements IJob {
    @Override
    public int getInterval() {
        return 1000;
    }

    @Override
    public void exec() throws Throwable {
        int hour = LocalDateTime.now().getHour();
        if (hour < 6) {
            //控制：0点到6点不跑
            return;
        }

        System.out.println("我是定时任务: TestJob2");
    }
}
