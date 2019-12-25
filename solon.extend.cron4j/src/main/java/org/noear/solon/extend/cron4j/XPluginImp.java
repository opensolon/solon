package org.noear.solon.extend.cron4j;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    Scheduler _server = null;

    @Override
    public void start(XApp app) {
        _server = new Scheduler();

        Aop.factory().beanLoaderAdd(Job.class, (clz, wrap, anno) -> {
            try {
                if (Runnable.class.isAssignableFrom(clz)) {
                    _server.schedule(anno.cron4(), (Runnable) wrap.get());
                }

                if (Task.class.isAssignableFrom(clz)) {
                    _server.schedule(anno.cron4(), (Task) wrap.get());
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

        Aop.beanOnloaded(() -> {
            _server.start();
        });
    }

    @Override
    public void stop() throws Throwable {
        if(_server != null){
            _server.stop();
            _server = null;
        }
    }
}
