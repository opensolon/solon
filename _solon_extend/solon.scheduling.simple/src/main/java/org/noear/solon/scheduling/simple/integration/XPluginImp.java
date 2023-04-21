package org.noear.solon.scheduling.simple.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.scheduling.annotation.EnableScheduling;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobManager;
import org.noear.solon.scheduling.simple.JobSimpleManager;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    private JobManager jobManager;

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableScheduling.class) == null) {
            return;
        }

        //创建管理器
        if (context.hasWrap(JobManager.class)) {
            jobManager = context.getBean(JobManager.class);
        } else {
            jobManager = new JobSimpleManager();
            context.wrapAndPut(JobManager.class, jobManager);
        }

        //设置全局管理器
        JobManager.setInstance(jobManager);

        //提取任务
        JobExtractor scheduledBeanBuilder = new JobExtractor(jobManager);
        context.beanBuilderAdd(Scheduled.class, scheduledBeanBuilder);
        context.beanExtractorAdd(Scheduled.class, scheduledBeanBuilder);

        //容器加载完后，再启动任务
        context.lifecycle(99, () -> {
            jobManager.start();
        });
    }

    @Override
    public void stop() throws Throwable {
        if (jobManager != null) {
            jobManager.stop();
            jobManager = null;
        }
    }
}