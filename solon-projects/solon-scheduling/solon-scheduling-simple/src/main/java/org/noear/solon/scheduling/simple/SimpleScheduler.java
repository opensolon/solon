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
package org.noear.solon.scheduling.simple;

import org.noear.java_cron.CronExpressionPlus;
import org.noear.java_cron.CronUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * Job 简单调度器
 *
 * @author noear
 * @since 2.2
 */
public class SimpleScheduler implements Lifecycle {
    static final Logger log = LoggerFactory.getLogger(SimpleScheduler.class);

    private final JobHolder jobHolder;
    /**
     * 调度表达式
     */
    private CronExpressionPlus cron;

    /**
     * 延后时间
     */
    private long delayMillis;
    /**
     * 下次执行时间
     */
    private Date nextTime;
    /**
     * 执行任务
     */
    private ScheduledFuture<?> jobFutureOfFixed;
    private Future<?> jobFutureOfCron;

    public SimpleScheduler(JobHolder jobHolder) {
        this.jobHolder = jobHolder;

        if (Utils.isNotEmpty(jobHolder.getScheduled().cron())) {
            this.cron = CronUtils.get(jobHolder.getScheduled().cron());

            if (Utils.isNotEmpty(jobHolder.getScheduled().zone())) {
                this.cron.setZoneId(ZoneId.of(jobHolder.getScheduled().zone()));
            }
        }
    }

    /**
     * 是否开始
     */
    private boolean isStarted = false;

    /**
     * 是否已开始
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * 开始
     */
    @Override
    public void start() throws Throwable {
        if (isStarted) {
            return;
        } else {
            isStarted = true;
        }

        //重置（可能会二次启动）
        nextTime = null;

        if (jobHolder.getScheduled().fixedDelay() > 0) {
            jobFutureOfFixed = RunUtil.scheduleWithFixedDelay(this::exec0,
                    jobHolder.getScheduled().initialDelay(),
                    jobHolder.getScheduled().fixedDelay());
        } else if (jobHolder.getScheduled().fixedRate() > 0) {
            jobFutureOfFixed = RunUtil.scheduleAtFixedRate(this::exec0,
                    jobHolder.getScheduled().initialDelay(),
                    jobHolder.getScheduled().fixedRate());
        } else {
            RunUtil.async(this::run);
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() throws Throwable {
        if (isStarted == false) {
            return;
        } else {
            isStarted = false;
        }

        if (jobFutureOfFixed != null) {
            jobFutureOfFixed.cancel(false);
        }

        if (jobFutureOfCron != null) {
            jobFutureOfCron.cancel(false);
        }
    }


    private void run() {
        if (isStarted == false) {
            return;
        }

        try {
            runAsCron();
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            if (e instanceof InterruptedException) {
                //任务中止
                isStarted = false;
                return;
            }

            log.warn(e.getMessage(), e);
        }

        if (delayMillis < 0L) {
            delayMillis = 100L;
        }

        RunUtil.delay(this::run, delayMillis);
    }

    /**
     * 调度
     */
    private void runAsCron() throws Throwable {
        if (nextTime == null) {
            //说明是第一次
            nextTime = cron.getNextValidTimeAfter(new Date());
        }

        if (nextTime != null) {
            delayMillis = nextTime.getTime() - System.currentTimeMillis();

            if (delayMillis <= 0L) {
                //到时（=0）或超时（<0）了
                jobFutureOfCron = RunUtil.async(this::exec0);

                nextTime = cron.getNextValidTimeAfter(nextTime);
                if (nextTime != null) {
                    //重新设定休息时间
                    delayMillis = nextTime.getTime() - System.currentTimeMillis();
                }
            }
        }

        if (nextTime == null) {
            isStarted = false;
            log.warn("The cron expression has expired, and the job is complete!");
        }
    }

    /**
     * 执行
     */
    private void exec0() {
        try {
            if (jobHolder.getSimpleName() != null) {
                MDC.put("job", jobHolder.getSimpleName());
            }

            jobHolder.handle(null);
        } catch (Throwable e) {
            if (jobHolder.getSimpleName() != null) {
                log.warn("Invoke failed: " + jobHolder.getSimpleName(), e);
            } else {
                log.warn("Invoke failed!", e);
            }
        }
    }
}