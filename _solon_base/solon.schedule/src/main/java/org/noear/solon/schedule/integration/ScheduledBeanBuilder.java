package org.noear.solon.schedule.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.schedule.JobManager;
import org.noear.solon.schedule.MethodRunnable;
import org.noear.solon.schedule.annotation.Scheduled;

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

            if (anno.fixedRate() > 0) {
                JobManager.add(name, anno.fixedRate(), anno.fixedDelay(), anno.concurrent(), bw.raw());
            } else {
                JobManager.add(name, anno.cron(), anno.zone(), anno.concurrent(), bw.raw());
            }
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

        if (anno.fixedRate() > 0) {
            JobManager.add(name, anno.fixedRate(), anno.fixedDelay(), anno.concurrent(), runnable);
        } else {
            JobManager.add(name, anno.cron(), anno.zone(), anno.concurrent(), runnable);
        }
    }
}
