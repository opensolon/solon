package org.noear.solon.scheduling.quartz.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.scheduling.annotation.EnableScheduling;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.quartz.JobManager;
import org.noear.solon.scheduling.scheduled.manager.JobExtractor;
import org.quartz.Job;
import org.quartz.Scheduler;

import java.lang.reflect.Method;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableScheduling.class) == null) {
            return;
        }

        //允许产生 Scheduler bean
        context.getBeanAsync(Scheduler.class, bean -> {
            JobManager.getInstance().setScheduler(bean);
        });

        //提取任务
        JobExtractor jobExtractor = new JobExtractor(JobManager.getInstance());
        context.beanBuilderAdd(Scheduled.class, ((clz, bw, anno) -> {
            if (bw.raw() instanceof Job) {
                Method method = Job.class.getDeclaredMethods()[0];
                jobExtractor.doExtract(bw, method, anno);
            } else {
                jobExtractor.doBuild(clz, bw, anno);
            }
        }));
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
