package org.noear.solon.extend.quartz;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.quartz.Job;

import java.util.Properties;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableQuartz.class) == null) {
            return;
        }

        context.beanBuilderAdd(Quartz.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof Job || bw.raw() instanceof Runnable) {
                String cronx = anno.cron7x();
                String name = anno.name();
                boolean enable = anno.enable();

                if (Utils.isNotEmpty(name)) {
                    Properties prop = Solon.cfg().getProp("solon.quartz." + name);

                    if (prop.size() > 0) {
                        String cronxTmp = prop.getProperty("cron7x");
                        String enableTmp = prop.getProperty("enable");

                        if ("false".equals(enableTmp)) {
                            enable = false;
                        }

                        if (Utils.isNotEmpty(cronxTmp)) {
                            cronx = cronxTmp;
                        }
                    }
                }

                JobManager.addJob(name, cronx, enable, bw);
            }
        });

        Solon.app().onEvent(AppLoadEndEvent.class, e -> {
            JobManager.start();
        });
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}
