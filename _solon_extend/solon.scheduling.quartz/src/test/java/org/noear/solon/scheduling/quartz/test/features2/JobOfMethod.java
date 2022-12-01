package org.noear.solon.scheduling.quartz.test.features2;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * @author noear 2022/12/1 created
 */
@Slf4j
@Component
public class JobOfMethod {
    @Scheduled(cron = "* * * * * ? ")
    public void job2(JobExecutionContext context) {
        log.debug("job2:: " + context.getJobDetail().getKey().getName());
        log.debug("job2:: " + new Date());
    }
}
