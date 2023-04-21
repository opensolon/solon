package org.noear.solon.scheduling.quartz.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.scheduling.ScheduledAnno;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobManager;
import org.noear.solon.scheduling.scheduled.impl.JobBeanImpl;
import org.noear.solon.scheduling.scheduled.impl.JobMethodImpl;
import org.noear.solon.scheduling.utils.ScheduledHelper;
import org.quartz.Job;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.11
 * @since 2.2
 */
public class JobExtractor implements BeanBuilder<Scheduled>, BeanExtractor<Scheduled> {
    private final JobManager jobManager;

    public JobExtractor(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Scheduled anno) throws Throwable {
        if (!(bw.raw() instanceof Job) && !(bw.raw() instanceof Runnable)) {
            throw new IllegalStateException("Quartz job only supports Runnable or Job types!");
        }

        ScheduledAnno warpper = new ScheduledAnno(anno);

        ScheduledHelper.configScheduled(warpper);

        JobHandler handler = new JobBeanImpl(bw);
        String name = warpper.name();
        if (Utils.isEmpty(name)) {
            name = bw.clz().getName();
        }

        jobManager.jobAdd(name, warpper, handler);
    }

    @Override
    public void doExtract(BeanWrap bw, Method method, Scheduled anno) throws Throwable {
        ScheduledAnno warpper = new ScheduledAnno(anno);

        ScheduledHelper.configScheduled(warpper);

        JobHandler handler = new JobMethodImpl(bw, method);
        String name = warpper.name();
        if (Utils.isEmpty(name)) {
            name = bw.clz().getName() + "::" + method.getName();
        }

        jobManager.jobAdd(name, warpper, handler);
    }
}