package org.noear.solon.scheduling.simple.test.features;

import org.noear.solon.annotation.Component;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2022/12/1 created
 */
@Component
public class MethodJob {
    @Scheduled(cron = "1s")
    public void job3() {
        System.out.println("job3:: " + new Date());
    }

    @Scheduled(cron = "* * * * * ? ")
    public void job4() {
        System.out.println("job4:: " + new Date());
    }
}
