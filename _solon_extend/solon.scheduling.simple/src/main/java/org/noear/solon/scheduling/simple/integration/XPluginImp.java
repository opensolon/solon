package org.noear.solon.scheduling.simple.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.scheduling.annotation.EnableScheduling;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.manager.JobExtractor;
import org.noear.solon.scheduling.simple.JobManager;

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

        //提取任务
        JobExtractor jobExtractor = new JobExtractor(JobManager.getInstance());
        context.beanBuilderAdd(Scheduled.class, jobExtractor);
        context.beanExtractorAdd(Scheduled.class, jobExtractor);

        //容器加载完后，再启动任务
        context.lifecycle(Integer.MAX_VALUE, () -> {
            JobManager.getInstance().start();
        });
    }

    @Override
    public void stop() throws Throwable {
        JobManager.getInstance().stop();
    }
}