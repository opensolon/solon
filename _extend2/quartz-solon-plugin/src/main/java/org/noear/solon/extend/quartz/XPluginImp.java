package org.noear.solon.extend.quartz;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

import java.util.Properties;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        try {
            JobManager.init();
        } catch (Exception ex) {
            throw XUtil.throwableWrap(ex);
        }

        Aop.context().beanBuilderAdd(Quartz.class, (clz, bw, anno) -> {
            String cronx = anno.cron7x();
            String name = anno.name();
            boolean enable = anno.enable();

            if (XUtil.isNotEmpty(name)) {
                Properties prop = XApp.cfg().getProp("solon.quartz." + name);

                if (prop.size() > 0) {
                    String cronxTmp = prop.getProperty("cron7x");
                    String enableTmp = prop.getProperty("enable");

                    if ("false".equals(enableTmp)) {
                        enable = false;
                    }

                    if (XUtil.isNotEmpty(cronxTmp)) {
                        cronx = cronxTmp;
                    }
                }
            }

            JobManager.doAddBean(name, cronx, enable, bw);
        });

        Aop.context().beanOnloaded(() -> {
            try {
                JobManager.start();
            } catch (Exception ex) {
                throw XUtil.throwableWrap(ex);
            }
        });
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}
