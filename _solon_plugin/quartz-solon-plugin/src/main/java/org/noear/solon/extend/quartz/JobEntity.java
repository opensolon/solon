package org.noear.solon.extend.quartz;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventBus;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class JobEntity {
    public final String name;
    /**
     * cron4 or 100ms,2s,1m,1h,1d(ms:毫秒；s:秒；m:分；h:小时；d:天)
     * */
    public final String cronx;
    public final BeanWrap beanWrap;
    public final boolean enable;
    public final String jobID;

    private boolean isRunnable;

    public JobEntity(String name, String cronx, boolean enable, BeanWrap beanWrap){
        this.name = name;
        this.cronx = cronx;
        this.beanWrap = beanWrap;
        this.enable = enable;
        this.jobID = beanWrap.clz().getName();
        this.isRunnable = Runnable.class.isAssignableFrom(beanWrap.clz());
    }

    public void exec(JobExecutionContext ctx){
        try {
            if (isRunnable) {
                ((Runnable) beanWrap.raw()).run();
            } else {
                ((Job) beanWrap.raw()).execute(ctx);
            }
        }catch (Throwable ex){
            EventBus.push(ex);
        }
    }
}
