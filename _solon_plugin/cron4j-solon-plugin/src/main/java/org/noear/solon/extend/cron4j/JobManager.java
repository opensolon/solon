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
        _taskScheduler = new ScheduledThreadPoolExecutor(2);
    }

    protected static void start() {
        if (_server.isStarted()) {
            return;
        }

        //初始化job
        for (JobEntity jobEntity : jobMap.values()) {
            initJob(jobEntity);
        }

        //启动服务
        _server.start();
    }

    protected static void stop() {

        if (_server != null) {
            _server.stop();

            jobMap.forEach((k,v)->{
                v.stop();
            });

            _server = null;
        }
    }

    protected static void register(String name, String cronx, boolean enable, BeanWrap bw){
        if (enable == false) {
            return;
        }

        if (Task.class.isAssignableFrom(bw.clz())) {
            if (cronx.indexOf(" ") < 0) {
                throw new IllegalStateException("Job cronx only supported Runnable：" + bw.clz().getName());
            }
        }

        if (Runnable.class.isAssignableFrom(bw.clz()) || Task.class.isAssignableFrom(bw.clz())) {
            JobEntity jobEntity = new JobEntity(name, cronx, enable, bw);
            jobMap.putIfAbsent(jobEntity.beanWrap.clz(), jobEntity);
        }
    }

    /**
     *
     * */
    protected static void initJob(JobEntity jobEntity) {
        if (jobEntity.cronx.indexOf(" ") < 0) {
            if (jobEntity.cronx.endsWith("ms")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 2));
                addFuture(jobEntity, period, TimeUnit.MILLISECONDS);
            } else if (jobEntity.cronx.endsWith("s")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 1));
                addFuture(jobEntity, period, TimeUnit.SECONDS);
            } else if (jobEntity.cronx.endsWith("m")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 1));
                addFuture(jobEntity, period, TimeUnit.MINUTES);
            } else if (jobEntity.cronx.endsWith("h")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 1));
                addFuture(jobEntity, period, TimeUnit.HOURS);
            } else if (jobEntity.cronx.endsWith("d")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 1));
                addFuture(jobEntity, period, TimeUnit.DAYS);
            }
        } else{
            addSchedule(jobEntity, jobEntity.cronx);
        }
    }

    private static void addSchedule(JobEntity jobEntity, String cronx) {
        String jobID = null;
        if(jobEntity.beanWrap.raw() instanceof Runnable) {
            jobID = _server.schedule(cronx, jobEntity::start);
        } else if(jobEntity.beanWrap.raw() instanceof Task){
            jobID = _server.schedule(cronx, (Task)jobEntity.beanWrap.raw());
        } else{
            return;
        }

        jobEntity.setJobID(jobID);
    }

    private static void addFuture(JobEntity jobEntity, long period, TimeUnit unit) {
        ScheduledFuture<?> future =  _taskScheduler.scheduleAtFixedRate(jobEntity::start, 0, period, unit);
        jobEntity.setFuture(future);
    }
}
