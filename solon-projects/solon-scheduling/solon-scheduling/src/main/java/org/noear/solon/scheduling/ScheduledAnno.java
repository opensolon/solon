/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private long initialDelay = 0L;
    private long fixedRate = 0L;
    private long fixedDelay = 0L;

    private boolean enable = true;

    public ScheduledAnno() {

    }

    public ScheduledAnno(Scheduled anno) {
        this.name = anno.name();
        this.cron = anno.cron();
        this.zone = anno.zone();

        this.initialDelay = anno.initialDelay();
        this.fixedRate = anno.fixedRate();
        this.fixedDelay = anno.fixedDelay();

        this.enable = anno.enable();
    }

    public ScheduledAnno name(String name) {
        if (name == null) {
            name = "";
        }

        this.name = name;
        return this;
    }

    public ScheduledAnno cron(String cron) {
        if (cron == null) {
            cron = "";
        }

        this.cron = cron;
        return this;
    }

    public ScheduledAnno zone(String zone) {
        if (zone == null) {
            zone = "";
        }

        this.zone = zone;
        return this;
    }

    public ScheduledAnno initialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public ScheduledAnno fixedRate(long fixedRate) {
        this.fixedRate = fixedRate;
        return this;
    }

    public ScheduledAnno fixedDelay(long fixedDelay) {
        this.fixedDelay = fixedDelay;
        return this;
    }

    public ScheduledAnno enable(boolean enable) {
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

    /**
     * 初次执行延时
     * */
    @Override
    public long initialDelay() {
        return initialDelay;
    }

    /**
     * 固定频率（并行执行）
     * */
    @Override
    public long fixedRate() {
        return fixedRate;
    }

    /**
     * 固定延时（串行执行）
     * */
    @Override
    public long fixedDelay() {
        return fixedDelay;
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