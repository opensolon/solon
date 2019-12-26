package org.noear.solon.extend.cron4j;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XPlugin;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class XPluginImp implements XPlugin {
    Scheduler _server = null;
    ScheduledThreadPoolExecutor _taskScheduler;

    @Override
    public void start(XApp app) {
        _server = new Scheduler();

        _taskScheduler = new ScheduledThreadPoolExecutor(1);

        Aop.factory().beanLoaderAdd(Job.class, (clz, bw, anno) -> {

            String cron4x = anno.cron4x();

            if (cron4x.indexOf(" ") < 0) {
                if (Runnable.class.isAssignableFrom(clz)) {
                    if (cron4x.endsWith("ms")) {
                        long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 2));
                        _taskScheduler.scheduleAtFixedRate(bw.raw(), 0, period, TimeUnit.MILLISECONDS);
                    } else if (cron4x.endsWith("s")) {
                        long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 1));
                        _taskScheduler.scheduleAtFixedRate(bw.raw(), 0, period, TimeUnit.SECONDS);
                    } else if (cron4x.endsWith("m")) {
                        long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 1));
                        _taskScheduler.scheduleAtFixedRate(bw.raw(), 0, period, TimeUnit.MINUTES);
                    } else if (cron4x.endsWith("h")) {
                        long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 1));
                        _taskScheduler.scheduleAtFixedRate(bw.raw(), 0, period, TimeUnit.HOURS);
                    }else if (cron4x.endsWith("d")) {
                        long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 1));
                        _taskScheduler.scheduleAtFixedRate(bw.raw(), 0, period, TimeUnit.DAYS);
                    }
                } else {
                    throw new RuntimeException("Non-cron4 mode, only Runnable is supported：" + clz.getName());
                }
            } else {
                if (Runnable.class.isAssignableFrom(clz)) {
                    _server.schedule(cron4x, (Runnable) bw.get());
                }

                if (Task.class.isAssignableFrom(clz)) {
                    _server.schedule(cron4x, (Task) bw.get());
                }
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
