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
            //0点到6点，不跑
            return;
        }

        System.out.println("xxxxbbbb");
    }
}
