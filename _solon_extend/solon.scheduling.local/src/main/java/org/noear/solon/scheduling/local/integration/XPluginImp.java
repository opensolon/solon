package org.noear.solon.scheduling.local.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.scheduling.annotation.EnableScheduling;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.local.JobManager;
import org.noear.solon.scheduling.local.MethodRunnable;

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

        context.beanBuilderAdd(Scheduled.class, (clz, bw, anno) -> {
            if (Runnable.class.isAssignableFrom(clz)) {
                String name = Utils.annoAlias(anno.name(), clz.getSimpleName());

                if (anno.fixedRate() > 0) {
                    JobManager.add(name, anno.fixedRate(), anno.fixedDelay(), anno.concurrent(), bw.raw());
                } else {
                    JobManager.add(name, anno.cron7x(), anno.zone(), anno.concurrent(), bw.raw());
                }
            }
        });

        context.beanExtractorAdd(Scheduled.class, (bw, method, anno) -> {
            MethodRunnable runnable = new MethodRunnable(bw.raw(), method);
            String name = Utils.annoAlias(anno.name(), method.getName());

            if (anno.fixedRate() > 0) {
                JobManager.add(name, anno.fixedRate(), anno.fixedDelay(), anno.concurrent(), runnable);
            } else {
                JobManager.add(name, anno.cron7x(), anno.zone(), anno.concurrent(), runnable);
            }
        });

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