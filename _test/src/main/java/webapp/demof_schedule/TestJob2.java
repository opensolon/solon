package webapp.demof_schedule;

import org.noear.solon.annotation.XBean;
import org.noear.solon.extend.schedule.IJob;

@XBean
public class TestJob2 implements IJob {
    @Override
    public int getInterval() {
        return 1000;
    }

    @Override
    public void exec() throws Throwable {
System.out.println("xxxxbbbb");
    }
}
