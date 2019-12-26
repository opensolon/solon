package org.noear.solon.extend.schedule;

import it.sauronsoftware.cron4j.Task;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XPlugin;

import java.util.Properties;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        JobManager.init();

        Aop.factory().beanLoaderAdd(Job.class, (clz, bw, anno) -> {
            String cron4x = anno.cron4x();

            if(anno.enable()) {
                scheduleAdd(cron4x, bw);
            }
        });

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, bw) -> {
                if (k.startsWith("job:") && k.length() > 5) {
                    String key = k.split(":")[1];
                    Properties prop = XApp.cfg().getProp("solon.schedule." + key);
                    if (prop.size() > 0) {
                        String cron4x = prop.getProperty("cron4x");
                        String enable = prop.getProperty("enable");

                        if ("false".equals(enable)) {
                            return;
                        }

                        scheduleAdd(cron4x, bw);
                    }
                }
            });

            JobManager.start();
        });
    }

    private void scheduleAdd(String cron4x, BeanWrap bw) {
        if (Runnable.class.isAssignableFrom(bw.clz())) {
            JobManager.addJob(cron4x, bw.raw());
        }

        if (Task.class.isAssignableFrom(bw.clz())) {
            if (cron4x.indexOf(" ") < 0) {
                throw new RuntimeException("Job only supported Runnable：" + bw.clz().getName());
            }

            JobManager.addTask(cron4x, bw.raw());
        }
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}