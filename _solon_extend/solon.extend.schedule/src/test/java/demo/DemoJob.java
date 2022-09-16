package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.extend.schedule.IJob;

@Component
public class DemoJob implements IJob {
    @Override
    public int getInterval() {
        return 1000;
    }

    @Override
    public void exec() throws Throwable {
        System.out.println("Hello world!");
    }
}
