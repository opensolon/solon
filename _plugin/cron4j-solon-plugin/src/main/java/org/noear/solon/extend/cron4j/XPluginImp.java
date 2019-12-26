package org.noear.solon.extend.cron4j;

import it.sauronsoftware.cron4j.Task;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        JobManager.init();

        Aop.factory().beanLoaderAdd(Job.class, (clz, bw, anno) -> {

            String cron4x = anno.cron4x();

            if (Runnable.class.isAssignableFrom(clz)) {
                JobManager.addJob(cron4x, bw.raw());
            }

            if(Task.class.isAssignableFrom(clz)){
                if(cron4x.indexOf(" ") < 0){
                    throw new RuntimeException("Job only supported Runnable：" + clz.getName());
                }

                JobManager.addTask(cron4x,bw.raw());
            }
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
