package org.noear.solon.scheduling.local.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.scheduling.ScheduledWarpper;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.local.JobManager;
import org.noear.solon.scheduling.local.MethodRunnable;
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
            String name = Utils.annoAlias(anno.name(), clz.getSimpleName());

            ScheduledWarpper warpper = new ScheduledWarpper(anno);
            ScheduledHelper.configScheduled(warpper);


            JobManager.add(name, anno, bw.raw());
        }
    }

    @Override
    public void doExtract(BeanWrap bw, Method method, Scheduled anno) throws Throwable {
        MethodRunnable runnable = new MethodRunnable(bw.raw(), method);
        String name = Utils.annoAlias(anno.name(), method.getName());

        ScheduledWarpper warpper = new ScheduledWarpper(anno);
        ScheduledHelper.configScheduled(warpper);

        JobManager.add(name, anno, runnable);
    }
}
