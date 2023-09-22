package org.noear.solon.scheduling.simple.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.scheduling.annotation.EnableScheduling;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.manager.IJobManager;
import org.noear.solon.scheduling.scheduled.manager.JobExtractor;
import org.noear.solon.scheduling.simple.JobManager;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        if (Solon.app().source().getAnnotation(EnableScheduling.class) == null) {
            return;
        }

        //注册 IJobManager
        context.wrapAndPut(IJobManager.class, JobManager.getInstance());

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