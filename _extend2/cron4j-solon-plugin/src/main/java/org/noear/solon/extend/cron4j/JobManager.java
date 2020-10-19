package org.noear.solon.extend.cron4j;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import org.noear.solon.core.BeanWrap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class JobManager {
    static Scheduler _server = null;
    static ScheduledThreadPoolExecutor _taskScheduler;
    static Map<Class<?>, JobEntity> jobMap = new HashMap<>();

    protected static void init() {
        _server = new Scheduler();
        _taskScheduler = new ScheduledThreadPoolExecutor(1);
    }

    protected static void start() {
        _server.start();
    }

    protected static void stop() {

        if (_server != null) {
            _server.stop();

            jobMap.forEach((k,v)->{
                if(v.getFuture() != null){
                    v.getFuture().cancel(true);
                }
            });

            _server = null;
        }
    }

    protected static void doAddBean(String name, String cron4x, boolean enable, BeanWrap bw){
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

    /**
     *
     * */
    public static void addJob(JobEntity jobEntity) {
        jobMap.putIfAbsent(jobEntity.beanWrap.clz(), jobEntity);

        if (jobEntity.cron4x.indexOf(" ") < 0) {
            if (jobEntity.cron4x.endsWith("ms")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 2));
                addFuture(jobEntity, period, TimeUnit.MILLISECONDS);
            } else if (jobEntity.cron4x.endsWith("s")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addFuture(jobEntity, period, TimeUnit.SECONDS);
            } else if (jobEntity.cron4x.endsWith("m")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addSchedule(jobEntity, "*/"+period+" * * * *");
            } else if (jobEntity.cron4x.endsWith("h")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addSchedule(jobEntity, "* */"+period+" * * *");
            } else if (jobEntity.cron4x.endsWith("d")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addSchedule(jobEntity, "* * */"+period+" * *");
            }
        } else{
            addSchedule(jobEntity, jobEntity.cron4x);
        }
    }

    private static void addSchedule(JobEntity jobEntity, String cron4x) {
        String jobID = null;
        if(jobEntity.beanWrap.raw() instanceof Runnable) {
            jobID = _server.schedule(cron4x, jobEntity::exec);
        } else if(jobEntity.beanWrap.raw() instanceof Task){
            jobID = _server.schedule(cron4x, (Task)jobEntity.beanWrap.raw());
        } else{
            return;
        }

        jobEntity.setJobID(jobID);
    }

    private static void addFuture(JobEntity jobEntity, long period, TimeUnit unit) {
        ScheduledFuture<?> future =  _taskScheduler.scheduleAtFixedRate(jobEntity::exec, 0, period, unit);
        jobEntity.setFuture(future);
    }
}
