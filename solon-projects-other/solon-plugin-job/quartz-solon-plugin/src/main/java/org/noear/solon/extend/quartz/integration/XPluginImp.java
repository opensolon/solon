package org.noear.solon.extend.quartz.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.quartz.EnableQuartz;
import org.noear.solon.extend.quartz.JobManager;
import org.noear.solon.extend.quartz.Quartz;
import org.quartz.Scheduler;

public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        if (Solon.app().source().getAnnotation(EnableQuartz.class) == null) {
            return;
        }

        //允许产生 Scheduler bean
        context.getBeanAsync(Scheduler.class, bean -> {
            JobManager.setScheduler(bean);
        });

        QuartzBeanBuilder beanBuilder = new QuartzBeanBuilder();
        //获取 Quartz 注解的类
        context.beanBuilderAdd(Quartz.class, beanBuilder);

        //获取 Quartz 注解的函数
        context.beanExtractorAdd(Quartz.class, beanBuilder);

        context.lifecycle(Integer.MAX_VALUE, () -> {
            JobManager.start();
        });
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}
