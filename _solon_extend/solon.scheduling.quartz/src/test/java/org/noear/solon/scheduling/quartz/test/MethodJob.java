package org.noear.solon.scheduling.quartz.test;

import org.noear.solon.annotation.Component;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * @author noear 2022/12/1 created
 */
@Component
public class MethodJob {
    @Scheduled(cron7x = "1s")
    public void job3() {
        System.out.println("job3:: " + new Date());
    }

    @Scheduled(cron7x = "* * * * * ? ")
    public void job4(JobExecutionContext context) {
        System.out.println("job4:: " + context.getJobDetail().getKey().getName());
        System.out.println("job4:: " + new Date());
    }
}
