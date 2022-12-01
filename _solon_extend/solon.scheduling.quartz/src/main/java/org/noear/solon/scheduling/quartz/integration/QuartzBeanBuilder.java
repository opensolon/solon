package org.noear.solon.scheduling.quartz.integration;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.scheduling.ScheduledWarpper;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.quartz.JobManager;
import org.noear.solon.scheduling.utils.ScheduledHelper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.11
 */
public class QuartzBeanBuilder implements BeanBuilder<Scheduled>, BeanExtractor<Scheduled> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Scheduled anno) throws Throwable {
        if (!(bw.raw() instanceof Job) && !(bw.raw() instanceof Runnable)) {
            throw new IllegalStateException("Quartz job only supports Runnable or Job types!");
        }

        ScheduledWarpper warpper = new ScheduledWarpper(anno);
        ScheduledHelper.configScheduled(warpper);


        JobManager.addJob(warpper.name(), warpper, new BeanJob(bw.raw()));
    }

    @Override
    public void doExtract(BeanWrap bw, Method method, Scheduled anno) throws Throwable {
        if (method.getParameterCount() > 1) {
            throw new IllegalStateException("Scheduling quartz job supports only one JobExecutionContext parameter!");
        }

        if (method.getParameterCount() == 1) {
            Class<?> tmp = method.getParameterTypes()[0];
            if (tmp != JobExecutionContext.class) {
                throw new IllegalStateException("Scheduling quartz supports only one JobExecutionContext parameter!");
            }
        }

        ScheduledWarpper warpper = new ScheduledWarpper(anno);
        ScheduledHelper.configScheduled(warpper);

        JobManager.addJob(warpper.name(), warpper, new MethodJob(bw.raw(), method));
    }


}