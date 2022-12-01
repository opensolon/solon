package org.noear.solon.scheduling;

import org.noear.solon.scheduling.annotation.Scheduled;

import java.lang.annotation.Annotation;

/**
 * Scheduled 模拟类
 *
 * @author noear
 * @since 1.11
 */
public class ScheduledWarpper implements Scheduled {
    private String name = "";
    private String cron = "";
    private String zone = "";

    private long fixedRate = 0;
    private long fixedDelay = 0;
    private boolean concurrent = false;

    private boolean enable = true;

    public ScheduledWarpper() {

    }

    public ScheduledWarpper(Scheduled anno) {
        this.name = anno.name();
        this.cron = anno.cron();
        this.zone = anno.zone();

        this.fixedRate = anno.fixedRate();
        this.fixedDelay = anno.fixedDelay();
        this.concurrent = anno.concurrent();

        this.enable = anno.enable();
    }

    public ScheduledWarpper name(String name) {
        if (name == null) {
            name = "";
        }

        this.name = name;
        return this;
    }

    public ScheduledWarpper cron(String cron) {
        if (cron == null) {
            cron = "";
        }

        this.cron = cron;
        return this;
    }

    public ScheduledWarpper zone(String zone) {
        if (zone == null) {
            zone = "";
        }

        this.zone = zone;
        return this;
    }

    public ScheduledWarpper fixedRate(long fixedRate) {
        this.fixedRate = fixedRate;
        return this;
    }

    public ScheduledWarpper fixedDelay(long fixedDelay) {
        this.fixedDelay = fixedDelay;
        return this;
    }

    public ScheduledWarpper concurrent(boolean concurrent) {
        this.concurrent = concurrent;
        return this;
    }

    public ScheduledWarpper enable(boolean enable) {
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