package org.noear.solon.extend.quartz.integration;

import org.noear.solon.extend.quartz.AbstractJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.11
 */
public class MethodJob extends AbstractJob {
    private final String jobId;
    private final Object target;
    private final Method method;
    private final boolean isRunnable;

    public MethodJob(Object target, Method method) {
        this.target = target;
        this.method = method;
        this.isRunnable = method.getParameterCount() == 0;
        this.jobId = target.getClass().getName() + "::" + method.getName();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            if (isRunnable) {
                method.invoke(target);
            } else {
                method.invoke(target, context);
            }
        } catch (Throwable e) {
            throw new JobExecutionException(e);
        }
    }

    @Override
    public String getJobId() {
        return jobId;
    }
}