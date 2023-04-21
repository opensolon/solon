package org.noear.solon.scheduling.quartz.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.scheduling.annotation.EnableScheduling;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.quartz.JobQuartzManager;
import org.noear.solon.scheduling.scheduled.JobManager;
import org.quartz.Scheduler;

public class XPluginImp implements Plugin {
    private JobQuartzManager jobManager;
    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableScheduling.class) == null) {
            return;
        }

        //创建管理器
        if (context.hasWrap(JobQuartzManager.class)) {
            jobManager = context.getBean(JobQuartzManager.class);
        } else {
            jobManager = new JobQuartzManager();
            context.wrapAndPut(JobQuartzManager.class, jobManager);
        }

        //设置全局管理器
        JobManager.setInstance(jobManager);

        //允许产生 Scheduler bean
        context.getBeanAsync(Scheduler.class, bean -> {
            jobManager.setScheduler(bean);
        });

        //提取任务
        JobExtractor beanBuilder = new JobExtractor(jobManager);
        context.beanBuilderAdd(Scheduled.class, beanBuilder);
        context.beanExtractorAdd(Scheduled.class, beanBuilder);

        //容器加载完后，再启动任务
        context.lifecycle(99, () -> {
            jobManager.start();
        });
    }

    @Override
    public void stop() throws Throwable {
        if(jobManager != null) {
            jobManager.stop();
            jobManager = null;
        }
    }
}
