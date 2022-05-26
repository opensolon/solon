package org.noear.solon.extend.cron4j;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;

import java.util.Properties;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableCron4j.class) == null) {
            return;
        }

        JobManager.init();

        context.beanBuilderAdd(Cron4j.class, (clz, bw, anno) -> {
            String cronx = anno.cron5x();
            String name = anno.name();
            boolean enable = anno.enable();

            if (Utils.isNotEmpty(name)) {
                Properties prop = Solon.cfg().getProp("solon.cron4j." + name);

                if (prop.size() > 0) {
                    String cronxTmp = prop.getProperty("cron5x");
                    String enableTmp = prop.getProperty("enable");

                    if ("false".equals(enableTmp)) {
                        enable = false;
                    }

                    if (Utils.isNotEmpty(cronxTmp)) {
                        cronx = cronxTmp;
                    }
                }
            }

            JobManager.register(name, cronx, enable, bw);
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
