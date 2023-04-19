package org.noear.solon.scheduling.simple.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.scheduling.ScheduledAnno;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.simple.JobManager;
import org.noear.solon.scheduling.utils.ScheduledHelper;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.11
 */
public class ScheduledBeanBuilder implements BeanBuilder<Scheduled>, BeanExtractor<Scheduled> {
    private final AopContext context;
    public ScheduledBeanBuilder(AopContext context){
        this.context = context;
    }

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Scheduled anno) throws Throwable {
        if (Runnable.class.isAssignableFrom(clz)) {
            ScheduledAnno warpper = new ScheduledAnno(anno);

            ScheduledHelper.configScheduled(warpper);

            Runnable job = new BeanRunnable(bw);
            String jobId = clz.getName();
            String name = Utils.annoAlias(anno.name(), jobId);

            JobManager.add(name, warpper, job);
        }
    }

    @Override
    public void doExtract(BeanWrap bw, Method method, Scheduled anno) throws Throwable {
        if (method.getParameterCount() > 0) {
            throw new IllegalStateException("Scheduling local job not supports parameter!");
        }

        ScheduledAnno warpper = new ScheduledAnno(anno);

        ScheduledHelper.configScheduled(warpper);

        Runnable job = new MethodRunnable(bw, method);
        String jobId = bw.clz().getName() + "::" + method.getName();
        String name = Utils.annoAlias(warpper.name(), jobId);

        JobManager.add(name, warpper, job);
    }
}
