package webapp.demof_schedule;

import org.noear.solon.annotation.XBean;
import org.noear.solon.extend.schedule.IJob;

//@XBean("job:test")
public class TestJob implements IJob {
    @Override
    public int getInterval() {
        return 30000;
    }

    @Override
    public void exec() throws Throwable {
        System.out.println("我是定时任务: TestJob");
    }
}
