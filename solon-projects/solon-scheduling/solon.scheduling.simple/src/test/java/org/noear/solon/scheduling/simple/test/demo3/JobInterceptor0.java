package org.noear.solon.scheduling.simple.test.demo3;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.scheduling.scheduled.Job;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobInterceptor;

@Slf4j
@Component
public class JobInterceptor0 implements JobInterceptor {
    @Override
    public void doIntercept(Job job, JobHandler handler) throws Throwable {
        log.warn("000");
        handler.handle(job.getContext());
    }
}
