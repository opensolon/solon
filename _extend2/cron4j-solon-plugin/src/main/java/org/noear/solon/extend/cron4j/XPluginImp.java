package org.noear.solon.extend.cron4j;

import it.sauronsoftware.cron4j.Task;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XPlugin;

import java.util.Properties;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        JobManager.init();

        Aop.factory().beanCreatorAdd(Job.class, (clz, bw, anno) -> {
            String cron4x = anno.cron4x();
            String name = anno.name();
            boolean enable = anno.enable();

            if (XUtil.isNotEmpty(name)) {
                Properties prop = XApp.cfg().getProp("solon.schedule." + name);

                if (prop.size() > 0) {
                    String cron4xTmp = prop.getProperty("cron4x");
                    String enableTmp = prop.getProperty("enable");

                    if ("false".equals(enableTmp)) {
                        enable = false;
                    }

                    if (XUtil.isNotEmpty(cron4xTmp)) {
                        cron4x = cron4xTmp;
                    }
                }
            }

            scheduleAdd(name, cron4x, enable, bw);
        });

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, bw) -> {
                if (k.startsWith("job:") && k.length() > 5) {
                    String name = k.split(":")[1];
                    Properties prop = XApp.cfg().getProp("solon.schedule." + name);
                    if (prop.size() > 0) {
                        String cron4x = prop.getProperty("cron4x");
                        boolean enable = !("false".equals(prop.getProperty("enable")));

                        scheduleAdd(name, cron4x, enable, bw);
                    }
                }
            });

            JobManager.start();
        });
    }

    private void scheduleAdd(String name, String cron4x, boolean enable, BeanWrap bw) {
        if (enable == false) {
            return;
        }

        if (Task.class.isAssignableFrom(bw.clz())) {
            if (cron4x.indexOf(" ") < 0) {
                throw new RuntimeException("Job only supported Runnable：" + bw.clz().getName());
            }
        }

        JobManager.addJob(new JobEntity(name, cron4x, enable, bw));
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}
