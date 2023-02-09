package org.noear.solon.extend.cron4j;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventBus;

import java.util.concurrent.ScheduledFuture;

public class JobEntity {
    public final String name;
    /**
     * cron or 100ms,2s,1m,1h,1d(ms:毫秒；s:秒；m:分；h:小时；d:天)
     * */
    public final String cronx;
    public final BeanWrap beanWrap;
    public final boolean enable;

    private String jobID;
    private ScheduledFuture<?> future;

    public JobEntity(String name, String cronx, boolean enable, BeanWrap beanWrap){
        this.name = name;
        this.cronx = cronx;
        this.beanWrap = beanWrap;
        this.enable = enable;
    }

    public void start(){
        try {
            Runnable job = beanWrap.raw();
            job.run();
        } catch (Throwable e) {
            EventBus.pushTry(e);
        }
    }

    public void stop(){
        if(future != null){
            future.cancel(true);
        }
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }
}
