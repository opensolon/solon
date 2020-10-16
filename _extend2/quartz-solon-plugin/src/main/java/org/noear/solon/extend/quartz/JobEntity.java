package org.noear.solon.extend.cron4j;

import org.noear.solon.core.BeanWrap;

import java.util.concurrent.ScheduledFuture;

public class JobEntity {
    public final String name;
    /**
     * cron4 or 100ms,2s,1m,1h,1d(ms:毫秒；s:秒；m:分；h:小时；d:天)
     * */
    public final String cron4x;
    public final BeanWrap beanWrap;
    public final boolean enable;

    private String jobID;
    private ScheduledFuture<?> future;

    public JobEntity(String name, String cron4x, boolean enable, BeanWrap beanWrap){
        this.name = name;
        this.cron4x = cron4x;
        this.beanWrap = beanWrap;
        this.enable = enable;
    }

    public void exec(){
        exec0(beanWrap.raw());
    }

    private void exec0(Runnable job) {
        try {
            job.run();
        } catch (Throwable ex) {
            ex.printStackTrace();
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
