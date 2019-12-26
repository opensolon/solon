package org.noear.solon.extend.schedule;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        JobManager.init();

        Aop.factory().beanLoaderAdd(Job.class, (clz, bw, anno) -> {

            //只对Runnable有效
            if (Runnable.class.isAssignableFrom(clz) == false) {
                throw new RuntimeException("Job only supported Runnable：" + clz.getName());
            }

            String cron4x = anno.cron4x();

            JobManager.addJob(cron4x, bw.raw());

        });

        Aop.beanOnloaded(() -> {
            JobManager.start();
        });
    }



    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}
