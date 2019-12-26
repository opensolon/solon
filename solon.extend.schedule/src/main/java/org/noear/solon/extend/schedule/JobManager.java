package org.noear.solon.extend.schedule;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class JobManager {
    static Scheduler _server = null;
    static ScheduledThreadPoolExecutor _taskScheduler;

    protected static void init(){
        _server = new Scheduler();
        _taskScheduler = new ScheduledThreadPoolExecutor(1);
    }

    protected static void start(){
        _server.start();
    }

    protected static void stop(){

        if(_server != null){
            _server.stop();
            _server = null;
        }
    }

    public static void addTask(String cron4, Task task) {
        _server.schedule(cron4, task);
    }

    /**
     * @param cron4x cron4 or 100ms,2s,1m,1h,1d(ms:毫秒；s:秒；m:分；h:小时；d:天)
     * */
    public static void addJob(String cron4x, Runnable job){

        if (cron4x.indexOf(" ") < 0) {
            if (cron4x.endsWith("ms")) {
                long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 2));
                _taskScheduler.scheduleAtFixedRate(() -> do_exec(job), 0, period, TimeUnit.MILLISECONDS);
            } else if (cron4x.endsWith("s")) {
                long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 1));
                _taskScheduler.scheduleAtFixedRate(() -> do_exec(job), 0, period, TimeUnit.SECONDS);
            } else if (cron4x.endsWith("m")) {
                long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 1));
                _taskScheduler.scheduleAtFixedRate(() -> do_exec(job), 0, period, TimeUnit.MINUTES);
            } else if (cron4x.endsWith("h")) {
                long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 1));
                _taskScheduler.scheduleAtFixedRate(() -> do_exec(job), 0, period, TimeUnit.HOURS);
            } else if (cron4x.endsWith("d")) {
                long period = Long.parseLong(cron4x.substring(0, cron4x.length() - 1));
                _taskScheduler.scheduleAtFixedRate(() -> do_exec(job), 0, period, TimeUnit.DAYS);
            }
        } else {
            _server.schedule(cron4x, () -> do_exec(job));
        }
    }

    private static void do_exec(Runnable job) {
        try {
            job.run();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
