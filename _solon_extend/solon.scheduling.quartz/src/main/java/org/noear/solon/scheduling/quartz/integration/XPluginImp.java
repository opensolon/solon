package org.noear.solon.scheduling.quartz.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.scheduling.annotation.EnableScheduling;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.quartz.JobManager;
import org.quartz.Scheduler;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableScheduling.class) == null) {
            return;
        }

        //允许产生 Scheduler bean
        context.getBeanAsync(Scheduler.class, bean -> {
            JobManager.setScheduler(bean);
        });

        QuartzBeanBuilder beanBuilder = new QuartzBeanBuilder();
        //获取 Quartz 注解的类
        context.beanBuilderAdd(Scheduled.class, beanBuilder);

        //获取 Quartz 注解的函数
        context.beanExtractorAdd(Scheduled.class, beanBuilder);

        Solon.app().onEvent(AppLoadEndEvent.class, e -> {
            JobManager.start();
        });
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}
