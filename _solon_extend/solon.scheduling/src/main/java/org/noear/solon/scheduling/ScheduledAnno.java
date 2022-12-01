package org.noear.solon.scheduling;

import org.noear.solon.scheduling.annotation.Scheduled;

import java.lang.annotation.Annotation;

/**
 * Scheduled 模拟类
 *
 * @author noear
 * @since 1.11
 */
public class ScheduledAnno implements Scheduled {
    private String name = "";
    private String cron = "";
    private String zone = "";

    private int fixedRate = 0;
    private int fixedDelay = 0;
    private boolean concurrent = false;

    private boolean enable = true;

    public ScheduledAnno name(String name){
        this.name = name;
        return this;
    }

    public ScheduledAnno cron(String cron){
        this.cron = cron;
        return this;
    }

    public ScheduledAnno zone(String zone){
        this.zone = zone;
        return this;
    }

    public ScheduledAnno fixedRate(int fixedRate){
        this.fixedRate = fixedRate;
        return this;
    }

    public ScheduledAnno fixedDelay(int fixedDelay){
        this.fixedDelay = fixedDelay;
        return this;
    }

    public ScheduledAnno concurrent(boolean concurrent){
        this.concurrent = concurrent;
        return this;
    }

    public ScheduledAnno enable(boolean enable){
        this.enable = enable;
        return this;
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public String cron() {
        return cron;
    }

    @Override
    public String zone() {
        return zone;
    }

    @Override
    public long fixedRate() {
        return fixedRate;
    }

    @Override
    public long fixedDelay() {
        return fixedDelay;
    }

    @Override
    public boolean concurrent() {
        return concurrent;
    }

    @Override
    public boolean enable() {
        return enable;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Scheduled.class;
    }
}
