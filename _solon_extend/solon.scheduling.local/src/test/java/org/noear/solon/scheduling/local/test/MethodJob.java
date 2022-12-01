package org.noear.solon.scheduling.local.test;

import org.noear.solon.annotation.Component;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2022/12/1 created
 */
@Component
public class MethodJob {
    @Scheduled(cron = "* * * * * ?")
    public void job3() {
        System.out.println("job3:: " + new Date());
    }

}
