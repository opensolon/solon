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

        Aop.context().beanBuilderAdd(QuartzJob.class, (clz, bw, anno) -> {
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

            JobManager.doAddBean(name, cron4x, enable, bw);
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
