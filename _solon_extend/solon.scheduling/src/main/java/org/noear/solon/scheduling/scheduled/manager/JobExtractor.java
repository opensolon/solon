package org.noear.solon.scheduling.scheduled.manager;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.scheduling.ScheduledAnno;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.impl.JobBeanImpl;
import org.noear.solon.scheduling.scheduled.impl.JobMethodImpl;
import org.noear.solon.scheduling.utils.ScheduledHelper;

import java.lang.reflect.Method;

/**
 * 任务提取器
 *
 * @author noear
 * @since 1.11
 * @since 2.2
 */
public class JobExtractor implements BeanBuilder<Scheduled>, BeanExtractor<Scheduled> {
    private final IJobManager jobManager;

    public JobExtractor(IJobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Scheduled anno) throws Throwable {
        if (bw.raw() instanceof Runnable || bw.raw() instanceof JobHandler) {
            ScheduledAnno warpper = new ScheduledAnno(anno);

            ScheduledHelper.configScheduled(warpper);

            JobHandler handler = new JobBeanImpl(bw);
            String name = warpper.name();
            if (Utils.isEmpty(name)) {
                name = bw.clz().getName();
            }

            jobManager.jobAdd(name, warpper, handler);
        } else {
            throw new IllegalStateException("Job only supports Runnable or JobHandler types!");
        }
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