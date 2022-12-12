package org.noear.solon.schedule.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.schedule.JobManager;
import org.noear.solon.schedule.annotation.EnableScheduling;
import org.noear.solon.schedule.annotation.Scheduled;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableScheduling.class) == null) {
            return;
        }

        ScheduledBeanBuilder scheduledBeanBuilder = new ScheduledBeanBuilder();

        context.beanBuilderAdd(Scheduled.class, scheduledBeanBuilder);
        context.beanExtractorAdd(Scheduled.class, scheduledBeanBuilder);

        //应用加载完后，再启动任务
        Solon.app().onEvent(AppLoadEndEvent.class, e -> {
            JobManager.start();
        });
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}