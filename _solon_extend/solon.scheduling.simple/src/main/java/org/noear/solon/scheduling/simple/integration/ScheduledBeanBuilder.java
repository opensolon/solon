package org.noear.solon.scheduling.simple.integration;

import org.noear.solon.Utils;
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
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Scheduled anno) throws Throwable {
        if (Runnable.class.isAssignableFrom(clz)) {
            String name = anno.name();
            if (Utils.isEmpty(name)) {
                name = clz.getName();
            }


            ScheduledAnno warpper = new ScheduledAnno(anno);
            ScheduledHelper.configScheduled(warpper);


            JobManager.add(name, warpper, bw.raw());
        }
    }

    @Override
    public void doExtract(BeanWrap bw, Method method, Scheduled anno) throws Throwable {
        if (method.getParameterCount() > 0) {
            throw new IllegalStateException("Scheduling local job not supports parameter!");
        }

        String name = anno.name();
        if (Utils.isEmpty(name)) {
            name = bw.clz().getName() + "::" + method.getName();
        }

        MethodRunnable runnable = new MethodRunnable(bw.raw(), method);

        ScheduledAnno warpper = new ScheduledAnno(anno);
        ScheduledHelper.configScheduled(warpper);

        JobManager.add(name, warpper, runnable);
    }
}
