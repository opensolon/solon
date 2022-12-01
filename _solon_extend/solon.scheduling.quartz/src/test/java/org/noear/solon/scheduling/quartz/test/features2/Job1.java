package org.noear.solon.scheduling.quartz.test.features2;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * @author noear 2022/12/1 created
 */
@Slf4j
@Scheduled(cron = "* * * * * ? ")
public class Job1 implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("job1:: " + context.getJobDetail().getKey().getName());
        log.debug("job1:: " + new Date());
    }
}
